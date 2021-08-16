package com.rackluxury.rollsroyce.reddit.postfilter;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class DeletePostFilter {
    public static void deletePostFilter(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor, PostFilter postFilter) {
        executor.execute(() -> redditDataRoomDatabase.postFilterDao().deletePostFilter(postFilter));
    }
}
