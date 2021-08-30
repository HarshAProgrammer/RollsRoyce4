package com.rackluxury.rollsroyce.reddit.recentsearchquery;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class RecentSearchQueryViewModel extends ViewModel {
    private final LiveData<List<RecentSearchQuery>> mAllRecentSearchQueries;

    public RecentSearchQueryViewModel(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
        mAllRecentSearchQueries = new RecentSearchQueryRepository(redditDataRoomDatabase, username).getAllRecentSearchQueries();
    }

    public LiveData<List<RecentSearchQuery>> getAllRecentSearchQueries() {
        return mAllRecentSearchQueries;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final RedditDataRoomDatabase mRedditDataRoomDatabase;
        private final String mUsername;

        public Factory(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
            mRedditDataRoomDatabase = redditDataRoomDatabase;
            mUsername = username;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new RecentSearchQueryViewModel(mRedditDataRoomDatabase, mUsername);
        }
    }
}
