package com.rackluxury.rollsroyce.reddit.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class DeleteAllUsers {

    public static void deleteAllUsers(Executor executor, Handler handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                      DeleteAllUsersListener deleteAllUsersListener) {
        executor.execute(() -> {
            redditDataRoomDatabase.userDao().deleteAllUsers();
            handler.post(deleteAllUsersListener::success);
        });
    }

    public interface DeleteAllUsersListener {
        void success();
    }
}
