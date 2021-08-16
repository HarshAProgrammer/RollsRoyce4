package com.rackluxury.rollsroyce.reddit.postfilter;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;

public class DeletePostFilterUsage {
    public static void deletePostFilterUsage(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                             PostFilterUsage postFilterUsage) {
        executor.execute(() -> redditDataRoomDatabase.postFilterUsageDao().deletePostFilterUsage(postFilterUsage));
    }
}
