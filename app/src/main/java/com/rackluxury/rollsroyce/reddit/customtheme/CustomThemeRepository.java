package com.rackluxury.rollsroyce.reddit.customtheme;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class CustomThemeRepository {
    private final LiveData<List<CustomTheme>> mAllCustomThemes;
    private final LiveData<CustomTheme> mCurrentLightCustomTheme;
    private final LiveData<CustomTheme> mCurrentDarkCustomTheme;
    private final LiveData<CustomTheme> mCurrentAmoledCustomTheme;

    CustomThemeRepository(RedditDataRoomDatabase redditDataRoomDatabase) {
        mAllCustomThemes = redditDataRoomDatabase.customThemeDao().getAllCustomThemes();
        mCurrentLightCustomTheme = redditDataRoomDatabase.customThemeDao().getLightCustomThemeLiveData();
        mCurrentDarkCustomTheme = redditDataRoomDatabase.customThemeDao().getDarkCustomThemeLiveData();
        mCurrentAmoledCustomTheme = redditDataRoomDatabase.customThemeDao().getAmoledCustomThemeLiveData();
    }

    LiveData<List<CustomTheme>> getAllCustomThemes() {
        return mAllCustomThemes;
    }

    LiveData<CustomTheme> getCurrentLightCustomTheme() {
        return mCurrentLightCustomTheme;
    }

    LiveData<CustomTheme> getCurrentDarkCustomTheme() {
        return mCurrentDarkCustomTheme;
    }

    LiveData<CustomTheme> getCurrentAmoledCustomTheme() {
        return mCurrentAmoledCustomTheme;
    }
}
