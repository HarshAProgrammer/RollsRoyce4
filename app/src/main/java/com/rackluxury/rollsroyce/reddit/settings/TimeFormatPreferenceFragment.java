package com.rackluxury.rollsroyce.reddit.settings;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import com.rackluxury.rollsroyce.reddit.events.ChangeShowElapsedTimeEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeTimeFormatEvent;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

public class TimeFormatPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.time_format_preferences, rootKey);

        SwitchPreference showElapsedTimeSwitch = findPreference(SharedPreferencesUtils.SHOW_ELAPSED_TIME_KEY);
        ListPreference timeFormatList = findPreference(SharedPreferencesUtils.TIME_FORMAT_KEY);

        if (showElapsedTimeSwitch != null) {
            showElapsedTimeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeShowElapsedTimeEvent((Boolean) newValue));
                return true;
            });
        }

        if (timeFormatList != null) {
            timeFormatList.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeTimeFormatEvent((String) newValue));
                return true;
            });
        }
    }
}
