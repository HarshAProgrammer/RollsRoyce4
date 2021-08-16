package com.rackluxury.rollsroyce.reddit.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.activities.CustomThemeListingActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditCustomizeThemeActivity;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeViewModel;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.events.RecreateActivityEvent;
import com.rackluxury.rollsroyce.reddit.utils.CustomThemeSharedPreferencesUtils;
import com.rackluxury.rollsroyce.reddit.utils.MaterialYouUtils;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemePreferenceFragment extends PreferenceFragmentCompat {

    private AppCompatActivity activity;
    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;
    @Inject
    @Named("light_theme")
    SharedPreferences lightThemeSharedPreferences;
    @Inject
    @Named("dark_theme")
    SharedPreferences darkThemeSharedPreferences;
    @Inject
    @Named("amoled_theme")
    SharedPreferences amoledThemeSharedPreferences;
    @Inject
    RedditDataRoomDatabase redditDataRoomDatabase;
    @Inject
    CustomThemeWrapper customThemeWrapper;
    @Inject
    Executor executor;
    public CustomThemeViewModel customThemeViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.theme_preferences, rootKey);

        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        ListPreference themePreference = findPreference(SharedPreferencesUtils.THEME_KEY);
        SwitchPreference amoledDarkSwitch = findPreference(SharedPreferencesUtils.AMOLED_DARK_KEY);
        Preference customizeLightThemePreference = findPreference(SharedPreferencesUtils.CUSTOMIZE_LIGHT_THEME);
        Preference customizeDarkThemePreference = findPreference(SharedPreferencesUtils.CUSTOMIZE_DARK_THEME);
        Preference customizeAmoledThemePreference = findPreference(SharedPreferencesUtils.CUSTOMIZE_AMOLED_THEME);
        Preference selectAndCustomizeThemePreference = findPreference(SharedPreferencesUtils.MANAGE_THEMES);
        SwitchPreference enableMaterialYouSwitchPreference = findPreference(SharedPreferencesUtils.ENABLE_MATERIAL_YOU);
        Preference applyMaterialYouPreference = findPreference(SharedPreferencesUtils.APPLY_MATERIAL_YOU);

        boolean systemDefault = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        if (themePreference != null && amoledDarkSwitch != null) {
            if (systemDefault) {
                themePreference.setEntries(R.array.settings_theme_q);
            } else {
                themePreference.setEntries(R.array.settings_theme);
            }

            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int option = Integer.parseInt((String) newValue);
                switch (option) {
                    case 0:
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                        customThemeWrapper.setThemeType(CustomThemeSharedPreferencesUtils.LIGHT);
                        break;
                    case 1:
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                        if (amoledDarkSwitch.isChecked()) {
                            customThemeWrapper.setThemeType(CustomThemeSharedPreferencesUtils.AMOLED);
                        } else {
                            customThemeWrapper.setThemeType(CustomThemeSharedPreferencesUtils.DARK);
                        }
                        break;
                    case 2:
                        if (systemDefault) {
                            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO_BATTERY);
                        }

                        if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
                            customThemeWrapper.setThemeType(CustomThemeSharedPreferencesUtils.LIGHT);
                        } else {
                            if (amoledDarkSwitch.isChecked()) {
                                customThemeWrapper.setThemeType(CustomThemeSharedPreferencesUtils.AMOLED);
                            } else {
                                customThemeWrapper.setThemeType(CustomThemeSharedPreferencesUtils.DARK);
                            }
                        }
                }
                return true;
            });
        }

        if (amoledDarkSwitch != null) {
            amoledDarkSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_NO) {
                    EventBus.getDefault().post(new RecreateActivityEvent());
                    ActivityCompat.recreate(activity);
                }
                return true;
            });
        }

        if (customizeLightThemePreference != null) {
            customizeLightThemePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, RedditCustomizeThemeActivity.class);
                intent.putExtra(RedditCustomizeThemeActivity.EXTRA_THEME_TYPE, RedditCustomizeThemeActivity.EXTRA_LIGHT_THEME);
                startActivity(intent);
                return true;
            });
        }

        if (customizeDarkThemePreference != null) {
            customizeDarkThemePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, RedditCustomizeThemeActivity.class);
                intent.putExtra(RedditCustomizeThemeActivity.EXTRA_THEME_TYPE, RedditCustomizeThemeActivity.EXTRA_DARK_THEME);
                startActivity(intent);
                return true;
            });
        }

        if (customizeAmoledThemePreference != null) {
            customizeAmoledThemePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, RedditCustomizeThemeActivity.class);
                intent.putExtra(RedditCustomizeThemeActivity.EXTRA_THEME_TYPE, RedditCustomizeThemeActivity.EXTRA_AMOLED_THEME);
                startActivity(intent);
                return true;
            });
        }

        if (selectAndCustomizeThemePreference != null) {
            selectAndCustomizeThemePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(activity, CustomThemeListingActivity.class);
                startActivity(intent);
                return true;
            });
        }

        if (enableMaterialYouSwitchPreference != null && applyMaterialYouPreference != null) {
            applyMaterialYouPreference.setVisible(
                    sharedPreferences.getBoolean(SharedPreferencesUtils.ENABLE_MATERIAL_YOU, false));

            enableMaterialYouSwitchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    MaterialYouUtils.changeTheme(activity, executor, new Handler(),
                            redditDataRoomDatabase, customThemeWrapper,
                            lightThemeSharedPreferences, darkThemeSharedPreferences,
                            amoledThemeSharedPreferences);
                    applyMaterialYouPreference.setVisible(true);
                } else {
                    applyMaterialYouPreference.setVisible(false);
                }
                return true;
            });

            applyMaterialYouPreference.setOnPreferenceClickListener(preference -> {
                MaterialYouUtils.changeTheme(activity, executor, new Handler(),
                        redditDataRoomDatabase, customThemeWrapper,
                        lightThemeSharedPreferences, darkThemeSharedPreferences,
                        amoledThemeSharedPreferences);
                return true;
            });
        }

        customThemeViewModel = new ViewModelProvider(this,
                new CustomThemeViewModel.Factory(redditDataRoomDatabase))
                .get(CustomThemeViewModel.class);
        customThemeViewModel.getCurrentLightThemeLiveData().observe(this, customTheme -> {
            if (customizeLightThemePreference != null) {
                if (customTheme != null) {
                    customizeLightThemePreference.setVisible(true);
                    customizeLightThemePreference.setSummary(customTheme.name);
                } else {
                    customizeLightThemePreference.setVisible(false);
                }
            }
        });
        customThemeViewModel.getCurrentDarkThemeLiveData().observe(this, customTheme -> {
            if (customizeDarkThemePreference != null) {
                if (customTheme != null) {
                    customizeDarkThemePreference.setVisible(true);
                    customizeDarkThemePreference.setSummary(customTheme.name);
                } else {
                    customizeDarkThemePreference.setVisible(false);
                }
            }
        });
        customThemeViewModel.getCurrentAmoledThemeLiveData().observe(this, customTheme -> {
            if (customizeAmoledThemePreference != null) {
                if (customTheme != null) {
                    customizeAmoledThemePreference.setVisible(true);
                    customizeAmoledThemePreference.setSummary(customTheme.name);
                } else {
                    customizeAmoledThemePreference.setVisible(false);
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }
}
