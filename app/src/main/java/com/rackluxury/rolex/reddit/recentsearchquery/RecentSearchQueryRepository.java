package com.rackluxury.rolex.reddit.recentsearchquery;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;

public class RecentSearchQueryRepository {
    private LiveData<List<RecentSearchQuery>> mAllRecentSearchQueries;

    RecentSearchQueryRepository(RedditDataRoomDatabase redditDataRoomDatabase, String username) {
        mAllRecentSearchQueries = redditDataRoomDatabase.recentSearchQueryDao().getAllRecentSearchQueriesLiveData(username);
    }

    LiveData<List<RecentSearchQuery>> getAllRecentSearchQueries() {
        return mAllRecentSearchQueries;
    }
}
