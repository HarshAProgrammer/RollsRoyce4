package com.rackluxury.rollsroyce.reddit.readpost;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

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
