package com.rackluxury.rolex.reddit.readpost;

import java.util.concurrent.Executor;

import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;

public class InsertReadPost {
    public static void insertReadPost(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                      String username, String postId) {
        executor.execute(() -> {
            ReadPostDao readPostDao = redditDataRoomDatabase.readPostDao();
            if (readPostDao.getReadPostsCount() > 500) {
                readPostDao.deleteOldestReadPosts(username);
            }
            if (username != null && !username.equals("")) {
                readPostDao.insert(new ReadPost(username, postId));
            }
        });
    }
}
