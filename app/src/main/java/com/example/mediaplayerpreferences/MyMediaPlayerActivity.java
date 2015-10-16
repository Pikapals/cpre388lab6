package com.example.mediaplayerpreferences;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * CPRE 388 - Labs
 * 
 * Copyright 2013
 */
public class MyMediaPlayerActivity extends Activity {

	/**
	 * Other view elements
	 */
	private TextView songTitleLabel;

	/**
	 *  media player:
	 *  http://developer.android.com/reference/android/media/MediaPlayer.html 
	 */
	private MediaPlayer mp;

	/**
	 * Index of the current song being played
	 */
	private int currentSongIndex = 0;

	public static final int GET_SONG = 1;

	private boolean shuffle;
	private boolean loop;
	private String name;

	/**
	 * List of Sounds that can be played in the form of SongObjects
	 */
	private static ArrayList<SongObject> songsList = new ArrayList<SongObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_player_main);

		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Resources res = getResources();

		shuffle = prefs.getBoolean(res.getString(R.string.mp_shuffle_pref), false);
		loop = prefs.getBoolean(res.getString(R.string.mp_loop_pref), false);
		name = prefs.getString(res.getString(R.string.mp_name_pref), "Playlist");

		// Initialize the media player
		mp = new MediaPlayer();

		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// Changing Button Image to play image
				((Button)findViewById(R.id.playpausebutton)).setBackgroundResource(R.drawable.btn_play);
				next(findViewById(R.id.forwardbutton));
			}
		});

		// Getting all songs in a list
		populateSongsList();

		// By default play first song if there is one in the list
		playSong(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.media_player_menu, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == GET_SONG) {
			if(resultCode == RESULT_OK) {
				currentSongIndex = data.getExtras().getInt("songIndex");
				playSong(currentSongIndex);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_choose_song:
			// Open SongList to display a list of audio files to play
			//TODO
			intent = new Intent(this, SongList.class);
			startActivityForResult(intent, GET_SONG);

			return true;
		case R.id.menu_preferences:
			// Display Settings page
			//TODO
			intent = new Intent(this, MediaPreferences.class);
			startActivity(intent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		populateSongsList();
	}
	

	/**
	 * Helper function to play a song at a specific index of songsList
	 * @param songIndex - index of song to be played
	 */
	public void  playSong(int songIndex){
		// Play song if index is within the songsList
		if (songIndex < songsList.size() && songIndex >= 0) {
			try {
				mp.stop();
				mp.reset();
				mp.setDataSource(songsList.get(songIndex).getFilePath());
				mp.prepare();
				mp.start();
				// Displaying Song title
				String songTitle = songsList.get(songIndex).getTitle();
				songTitleLabel.setText(songTitle);

				// Changing Button Image to pause image
				((Button)findViewById(R.id.playpausebutton)).setBackgroundResource(R.drawable.btn_pause);

				// Update song index
				currentSongIndex = songIndex;

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		} else if (songsList.size() > 0) {
			playSong(0);
		}
	}


	/** 
	 * Get list of info for all sounds to be played
	 */
	public void populateSongsList(){
		//TODO add all songs from audio content URI to this.songsList
		String[] col = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA};

		// Get a Cursor object from the content URI
		String order = MediaStore.Audio.Media.TITLE;
		if(shuffle) {
			order = null;
		}
		Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, col, null, null, order);
		
		// Use the cursor to loop through the results and add them to 
		//		the songsList as SongObjects

		mCursor.moveToFirst();

		// Create Event objects for each item in list
		while (!mCursor.isAfterLast()) {
			SongObject song = cursorToSong(mCursor);
			songsList.add(song);
			mCursor.moveToNext();
		}

		mCursor.close();
	}

	/*
	 * Helper method to convert row data into Event
	 */
	private SongObject cursorToSong(Cursor cursor) {
		SongObject song = new SongObject(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)), cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));

		return song;
	}

	/**
	 * Get song list for display in ListView
	 * @return list of Songs 
	 */
	public static ArrayList<SongObject> getSongsList(){
		return songsList;
	}

	public boolean playPause(View v) {
		if(mp.isPlaying()) {
			mp.pause();
			((Button)findViewById(R.id.playpausebutton)).setBackgroundResource(R.drawable.btn_play);
		} else {
			mp.start();
			((Button)findViewById(R.id.playpausebutton)).setBackgroundResource(R.drawable.btn_pause);
		}
		return true;
	}

	public boolean next(View v) {
		currentSongIndex++;
		if(currentSongIndex >= songsList.size()) {
			if(loop) {
				currentSongIndex = 0;
			} else {
				currentSongIndex = songsList.size()-1;
			}
		}
		playSong(currentSongIndex);
		return true;
	}

	public boolean back(View v) {
		currentSongIndex--;
		if(currentSongIndex < 0) {
			if(loop) {
				currentSongIndex = songsList.size()-1;
			} else {
				currentSongIndex = 0;
			}
		}
		playSong(currentSongIndex);
		return true;
	}

}
