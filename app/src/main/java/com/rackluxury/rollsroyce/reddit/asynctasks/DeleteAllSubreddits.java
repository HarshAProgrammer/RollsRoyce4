package com.rackluxury.rollsroyce.reddit.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class DeleteAllSubreddits {

    public static void deleteAllSubreddits(Executor executor, Handler handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                           DeleteAllSubredditsAsyncTaskListener deleteAllSubredditsAsyncTaskListener) {
        executor.execute(() -> {
            redditDataRoomDatabase.subredditDao().deleteAllSubreddits();
            handler.post(deleteAllSubredditsAsyncTaskListener::success);
        });
    }

    public interface DeleteAllSubredditsAsyncTaskListener {
        void success();
    }
}
