package com.rackluxury.rolex.reddit.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.events.RecreateActivityEvent;
import com.rackluxury.rolex.reddit.utils.SharedPreferencesUtils;

public class ImmersiveInterfacePreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.immersive_interface_preferences, rootKey);

        SwitchPreference immersiveInterfaceSwitch = findPreference(SharedPreferencesUtils.IMMERSIVE_INTERFACE_KEY);
        SwitchPreference immersiveInterfaceIgnoreNavBarSwitch = findPreference(SharedPreferencesUtils.IMMERSIVE_INTERFACE_IGNORE_NAV_BAR_KEY);
        SwitchPreference disableImmersiveInterfaceInLandscapeModeSwitch = findPreference(SharedPreferencesUtils.DISABLE_IMMERSIVE_INTERFACE_IN_LANDSCAPE_MODE);

        if (immersiveInterfaceSwitch != null && immersiveInterfaceIgnoreNavBarSwitch != null
                && disableImmersiveInterfaceInLandscapeModeSwitch != null) {
            immersiveInterfaceSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    immersiveInterfaceIgnoreNavBarSwitch.setVisible(true);
                } else {
                    immersiveInterfaceIgnoreNavBarSwitch.setVisible(false);
                }
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });

            if (immersiveInterfaceSwitch.isChecked()) {
                immersiveInterfaceIgnoreNavBarSwitch.setVisible(true);
            } else {
                immersiveInterfaceIgnoreNavBarSwitch.setVisible(false);
            }

            immersiveInterfaceIgnoreNavBarSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });

            disableImmersiveInterfaceInLandscapeModeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }
    }
}