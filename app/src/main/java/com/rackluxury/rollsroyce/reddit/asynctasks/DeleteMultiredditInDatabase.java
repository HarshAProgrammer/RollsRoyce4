package com.rackluxury.rollsroyce.reddit.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class DeleteMultiredditInDatabase {

    public static void deleteMultiredditInDatabase(Executor executor, Handler handler,
                                                   RedditDataRoomDatabase redditDataRoomDatabase,
                                                   String accountName, String multipath,
                                                   DeleteMultiredditInDatabaseListener deleteMultiredditInDatabaseListener) {
        executor.execute(() -> {
            redditDataRoomDatabase.multiRedditDao().deleteMultiReddit(multipath, accountName);
            handler.post(deleteMultiredditInDatabaseListener::success);
        });
    }
    public interface DeleteMultiredditInDatabaseListener {
        void success();
    }
}
