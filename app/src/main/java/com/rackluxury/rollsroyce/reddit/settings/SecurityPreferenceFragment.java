package com.rackluxury.rollsroyce.reddit.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import com.rackluxury.rollsroyce.reddit.events.ChangeRequireAuthToAccountSectionEvent;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class SecurityPreferenceFragment extends PreferenceFragmentCompat {

    private AppCompatActivity activity;
    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.security_preferences, rootKey);

        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        SwitchPreference requireAuthToAccountSectionSwitch = findPreference(SharedPreferencesUtils.REQUIRE_AUTHENTICATION_TO_GO_TO_ACCOUNT_SECTION_IN_NAVIGATION_DRAWER);

        if (requireAuthToAccountSectionSwitch != null) {
            requireAuthToAccountSectionSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new ChangeRequireAuthToAccountSectionEvent((Boolean) newValue));
                return true;
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Executor executor = ContextCompat.getMainExecutor(activity);
        BiometricPrompt biometricPrompt = new BiometricPrompt(SecurityPreferenceFragment.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                activity.onBackPressed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.unlock))
                .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }
}