package com.rackluxury.rollsroyce.reddit.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import javax.inject.Inject;
import javax.inject.Named;

import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.activities.RedditPostFilterPreferenceActivity;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class MainPreferenceFragment extends PreferenceFragmentCompat {

    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;
    private AppCompatActivity activity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);
        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        Preference securityPreference = findPreference(SharedPreferencesUtils.SECURITY);
        Preference postFilterPreference = findPreference(SharedPreferencesUtils.POST_FILTER);

        BiometricManager biometricManager = BiometricManager.from(activity);
        if (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_SUCCESS) {
            if (securityPreference != null) {
                securityPreference.setVisible(false);
            }
        }

        if (postFilterPreference != null) {
            postFilterPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, RedditPostFilterPreferenceActivity.class);
                activity.startActivity(intent);
                return true;
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }
}
