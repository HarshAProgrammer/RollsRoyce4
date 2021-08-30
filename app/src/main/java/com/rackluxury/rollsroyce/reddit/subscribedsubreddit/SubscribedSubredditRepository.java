package com.rackluxury.rollsroyce.reddit.subscribedsubreddit;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class SubscribedSubredditRepository {
    private final SubscribedSubredditDao mSubscribedSubredditDao;
    private final LiveData<List<SubscribedSubredditData>> mAllSubscribedSubreddits;
    private final LiveData<List<SubscribedSubredditData>> mAllFavoriteSubscribedSubreddits;

    SubscribedSubredditRepository(RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        mSubscribedSubredditDao = redditDataRoomDatabase.subscribedSubredditDao();
        mAllSubscribedSubreddits = mSubscribedSubredditDao.getAllSubscribedSubreddits(accountName);
        mAllFavoriteSubscribedSubreddits = mSubscribedSubredditDao.getAllFavoriteSubscribedSubreddits(accountName);
    }

    LiveData<List<SubscribedSubredditData>> getAllSubscribedSubreddits() {
        return mAllSubscribedSubreddits;
    }

    public LiveData<List<SubscribedSubredditData>> getAllFavoriteSubscribedSubreddits() {
        return mAllFavoriteSubscribedSubreddits;
    }

    public void insert(SubscribedSubredditData subscribedSubredditData) {
        new insertAsyncTask(mSubscribedSubredditDao).execute(subscribedSubredditData);
    }

    private static class insertAsyncTask extends AsyncTask<SubscribedSubredditData, Void, Void> {

        private final SubscribedSubredditDao mAsyncTaskDao;

        insertAsyncTask(SubscribedSubredditDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SubscribedSubredditData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
