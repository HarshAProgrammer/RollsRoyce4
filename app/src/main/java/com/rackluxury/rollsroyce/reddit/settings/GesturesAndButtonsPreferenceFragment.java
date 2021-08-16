package com.rackluxury.rollsroyce.reddit.settings;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import com.rackluxury.rollsroyce.reddit.events.ChangeLockBottomAppBarEvent;
import com.rackluxury.rollsroyce.reddit.fragments.ChangePullToRefreshEvent;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class GesturesAndButtonsPreferenceFragment extends PreferenceFragmentCompat {

    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;
    private Activity activity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.gestures_and_buttons_preferences, rootKey);
        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        SwitchPreference lockJumpToNextTopLevelCommentButtonSwitch =
                findPreference(SharedPreferencesUtils.LOCK_JUMP_TO_NEXT_TOP_LEVEL_COMMENT_BUTTON);
        SwitchPreference lockBottomAppBarSwitch = findPreference(SharedPreferencesUtils.LOCK_BOTTOM_APP_BAR);
        SwitchPreference swipeUpToHideJumpToNextTopLevelCommentButtonSwitch =
                findPreference(SharedPreferencesUtils.SWIPE_UP_TO_HIDE_JUMP_TO_NEXT_TOP_LEVEL_COMMENT_BUTTON);
        SwitchPreference pullToRefreshSwitch = findPreference(SharedPreferencesUtils.PULL_TO_REFRESH);

        if (lockJumpToNextTopLevelCommentButtonSwitch != null && lockBottomAppBarSwitch != null &&
                swipeUpToHideJumpToNextTopLevelCommentButtonSwitch != null) {
            lockJumpToNextTopLevelCommentButtonSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    swipeUpToHideJumpToNextTopLevelCommentButtonSwitch.setVisible(false);
                } else {
                    swipeUpToHideJumpToNextTopLevelCommentButtonSwitch.setVisible(true);
                }
                return true;
            });

            if (sharedPreferences.getBoolean(SharedPreferencesUtils.BOTTOM_APP_BAR_KEY, true)) {
                lockBottomAppBarSwitch.setVisible(true);
                lockBottomAppBarSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                    EventBus.getDefault().post(new ChangeLockBottomAppBarEvent((Boolean) newValue));
                    return true;
                });
            }

            if (!sharedPreferences.getBoolean(SharedPreferencesUtils.LOCK_JUMP_TO_NEXT_TOP_LEVEL_COMMENT_BUTTON, false)) {
                swipeUpToHideJumpToNextTopLevelCommentButtonSwitch.setVisible(true);
            }
        }

        if (pullToRefreshSwitch != null) {
            pullToRefreshSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    EventBus.getDefault().post(new ChangePullToRefreshEvent((Boolean) newValue));
                    return true;
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
}
