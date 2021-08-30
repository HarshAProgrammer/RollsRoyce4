package com.rackluxury.rollsroyce.reddit.recentsearchquery;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class RecentSearchQueryRepository {
    private final LiveData<List<RecentSearchQuery>> mAllRecentSearchQueries;

    RecentSearchQueryRepository(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
        mAllRecentSearchQueries = redditDataRoomDatabase.recentSearchQueryDao().getAllRecentSearchQueriesLiveData(username);
    }

    LiveData<List<RecentSearchQuery>> getAllRecentSearchQueries() {
        return mAllRecentSearchQueries;
    }
}
