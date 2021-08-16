package com.rackluxury.rollsroyce.reddit.multireddit;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class MultiRedditRepository {
    private LiveData<List<MultiReddit>> mAllMultiReddits;
    private LiveData<List<MultiReddit>> mAllFavoriteMultiReddits;

    MultiRedditRepository(RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        MultiRedditDao multiRedditDao = redditDataRoomDatabase.multiRedditDao();
        mAllMultiReddits = multiRedditDao.getAllMultiReddits(accountName);
        mAllFavoriteMultiReddits = multiRedditDao.getAllFavoriteMultiReddits(accountName);
    }

    LiveData<List<MultiReddit>> getAllMultiReddits() {
        return mAllMultiReddits;
    }

    LiveData<List<MultiReddit>> getAllFavoriteMultiReddits() {
        return mAllFavoriteMultiReddits;
    }
}
