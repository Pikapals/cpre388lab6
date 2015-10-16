package com.example.mediaplayerpreferences;

/**
 * This object represents a song in the form of the title and the file path for the audio file.
 */
public class SongObject {

	/**
	 * The title of the audio file
	 */
	private String title;
	
	/**
	 * The file path of the audio file 
	 */
	private String filePath;
	
	public SongObject(String title, String filePath){
		super();
		this.title = title;
		this.filePath = filePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
}
