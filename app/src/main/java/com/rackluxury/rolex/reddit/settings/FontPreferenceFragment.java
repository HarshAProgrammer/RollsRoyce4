package com.rackluxury.rolex.reddit.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import org.greenrobot.eventbus.EventBus;

import com.rackluxury.rolex.reddit.events.RecreateActivityEvent;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.utils.SharedPreferencesUtils;

public class FontPreferenceFragment extends PreferenceFragmentCompat {
    private Activity activity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.font_preferences, rootKey);
        ListPreference fontFamilyPreference = findPreference(SharedPreferencesUtils.FONT_FAMILY_KEY);
        ListPreference titleFontFamilyPreference = findPreference(SharedPreferencesUtils.TITLE_FONT_FAMILY_KEY);
        ListPreference contentFontFamilyPreference = findPreference(SharedPreferencesUtils.CONTENT_FONT_FAMILY_KEY);
        ListPreference fontSizePreference = findPreference(SharedPreferencesUtils.FONT_SIZE_KEY);
        ListPreference titleFontSizePreference = findPreference(SharedPreferencesUtils.TITLE_FONT_SIZE_KEY);
        ListPreference contentFontSizePreference = findPreference(SharedPreferencesUtils.CONTENT_FONT_SIZE_KEY);

        if (fontFamilyPreference != null) {
            fontFamilyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                ActivityCompat.recreate(activity);
                return true;
            });
        }

        if (titleFontFamilyPreference != null) {
            titleFontFamilyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }

        if (contentFontFamilyPreference != null) {
            contentFontFamilyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }

        if (fontSizePreference != null) {
            fontSizePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                ActivityCompat.recreate(activity);
                return true;
            });
        }

        if (titleFontSizePreference != null) {
            titleFontSizePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }

        if (contentFontSizePreference != null) {
            contentFontSizePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
}
