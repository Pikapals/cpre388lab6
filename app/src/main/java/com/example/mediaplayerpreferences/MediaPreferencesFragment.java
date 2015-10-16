package com.example.mediaplayerpreferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MediaPreferencesFragment extends PreferenceFragment {

    public MediaPreferencesFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.media_preferences, false);

        addPreferencesFromResource(R.xml.media_preferences);
    }
}
