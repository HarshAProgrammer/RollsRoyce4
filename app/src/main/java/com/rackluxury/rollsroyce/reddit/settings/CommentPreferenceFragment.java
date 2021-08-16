package com.rackluxury.rollsroyce.reddit.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.rackluxury.rollsroyce.R;

public class CommentPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.comment_preferences, rootKey);
    }
}