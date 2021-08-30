package com.rackluxury.rollsroyce.reddit.customtheme;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class CustomThemeViewModel extends ViewModel {
    private final LiveData<List<CustomTheme>> mAllCustomThemes;
    private final LiveData<CustomTheme> mCurrentLightTheme;
    private final LiveData<CustomTheme> mCurrentDarkTheme;
    private final LiveData<CustomTheme> mCurrentAmoledTheme;

    public CustomThemeViewModel(RedditDataRoomDatabase redditDataRoomDatabase) {
        CustomThemeRepository customThemeRepository = new CustomThemeRepository(redditDataRoomDatabase);
        mAllCustomThemes = customThemeRepository.getAllCustomThemes();
        mCurrentLightTheme = customThemeRepository.getCurrentLightCustomTheme();
        mCurrentDarkTheme = customThemeRepository.getCurrentDarkCustomTheme();
        mCurrentAmoledTheme = customThemeRepository.getCurrentAmoledCustomTheme();
    }

    public LiveData<List<CustomTheme>> getAllCustomThemes() {
        return mAllCustomThemes;
    }

    public LiveData<CustomTheme> getCurrentLightThemeLiveData() {
        return mCurrentLightTheme;
    }

    public LiveData<CustomTheme> getCurrentDarkThemeLiveData() {
        return mCurrentDarkTheme;
    }

    public LiveData<CustomTheme> getCurrentAmoledThemeLiveData() {
        return mCurrentAmoledTheme;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final RedditDataRoomDatabase mRedditDataRoomDatabase;

        public Factory(RedditDataRoomDatabase redditDataRoomDatabase) {
            mRedditDataRoomDatabase = redditDataRoomDatabase;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CustomThemeViewModel(mRedditDataRoomDatabase);
        }
    }
}
