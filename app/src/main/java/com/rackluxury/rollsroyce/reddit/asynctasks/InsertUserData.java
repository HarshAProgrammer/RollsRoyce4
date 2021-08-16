package com.rackluxury.rollsroyce.reddit.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.user.UserData;

public class InsertUserData {

    public static void insertUserData(Executor executor, Handler handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                      UserData userData, InsertUserDataListener insertUserDataListener) {
        executor.execute(() -> {
            redditDataRoomDatabase.userDao().insert(userData);
            if (insertUserDataListener != null) {
                handler.post(insertUserDataListener::insertSuccess);
            }
        });
    }

    public interface InsertUserDataListener {
        void insertSuccess();
    }
}
