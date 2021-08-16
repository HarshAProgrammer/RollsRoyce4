package com.rackluxury.rollsroyce.reddit.settings;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.events.ChangeDataSavingModeEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeDisableImagePreviewEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeOnlyDisablePreviewInVideoAndGifPostsEvent;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

public class DataSavingModePreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.data_saving_mode_preferences, rootKey);

        ListPreference dataSavingModeListPreference = findPreference(SharedPreferencesUtils.DATA_SAVING_MODE);
        SwitchPreference disableImagePreviewPreference = findPreference(SharedPreferencesUtils.DISABLE_IMAGE_PREVIEW);
        SwitchPreference onlyDisablePreviewInVideoAndGifPostsPreference = findPreference(SharedPreferencesUtils.ONLY_DISABLE_PREVIEW_IN_VIDEO_AND_GIF_POSTS);

        if (dataSavingModeListPreference != null) {
            dataSavingModeListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeDataSavingModeEvent((String) newValue));
                return true;
            });
        }

        if (disableImagePreviewPreference != null) {
            disableImagePreviewPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeDisableImagePreviewEvent((Boolean) newValue));
                return true;
            });
        }

        if (onlyDisablePreviewInVideoAndGifPostsPreference != null) {
            onlyDisablePreviewInVideoAndGifPostsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeOnlyDisablePreviewInVideoAndGifPostsEvent((Boolean) newValue));
                return true;
            });
        }
    }
}