package com.rackluxury.rolex.reddit;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import com.rackluxury.rolex.reddit.postfilter.PostFilter;
import com.rackluxury.rolex.reddit.readpost.ReadPost;
import com.rackluxury.rolex.reddit.subscribedsubreddit.SubscribedSubredditData;

public class FetchPostFilterReadPostsAndConcatenatedSubredditNames {
    public interface FetchPostFilterAndReadPostsListener {
        void success(PostFilter postFilter, ArrayList<ReadPost> readPostList);
    }

    public interface FetchPostFilterAndConcatenatecSubredditNamesListener {
        void success(PostFilter postFilter, String concatenatedSubredditNames);
    }

    public static void fetchPostFilterAndReadPosts(com.rackluxury.rolex.reddit.RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                                   Handler handler, String accountName, int postFilterUsage,
                                                   String nameOfUsage, FetchPostFilterAndReadPostsListener fetchPostFilterAndReadPostsListener) {
        executor.execute(() -> {
            List<PostFilter> postFilters = redditDataRoomDatabase.postFilterDao().getValidPostFilters(postFilterUsage, nameOfUsage);
            PostFilter mergedPostFilter = PostFilter.mergePostFilter(postFilters);
            if (accountName != null) {
                ArrayList<ReadPost> readPosts = (ArrayList<ReadPost>) redditDataRoomDatabase.readPostDao().getAllReadPosts(accountName);
                handler.post(() -> fetchPostFilterAndReadPostsListener.success(mergedPostFilter, readPosts));
            } else {
                handler.post(() -> fetchPostFilterAndReadPostsListener.success(mergedPostFilter, null));
            }
        });
    }

    public static void fetchPostFilterAndConcatenatedSubredditNames(com.rackluxury.rolex.reddit.RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                                                    Handler handler, int postFilterUsage, String nameOfUsage,
                                                                    FetchPostFilterAndConcatenatecSubredditNamesListener fetchPostFilterAndConcatenatecSubredditNamesListener) {
        executor.execute(() -> {
            List<PostFilter> postFilters = redditDataRoomDatabase.postFilterDao().getValidPostFilters(postFilterUsage, nameOfUsage);
            PostFilter mergedPostFilter = PostFilter.mergePostFilter(postFilters);
            List<SubscribedSubredditData> anonymousSubscribedSubreddits = redditDataRoomDatabase.subscribedSubredditDao().getAllSubscribedSubredditsList("-");
            if (anonymousSubscribedSubreddits != null && !anonymousSubscribedSubreddits.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (SubscribedSubredditData s : anonymousSubscribedSubreddits) {
                    stringBuilder.append(s.getName()).append("+");
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                handler.post(() -> fetchPostFilterAndConcatenatecSubredditNamesListener.success(mergedPostFilter, stringBuilder.toString()));
            } else {
                handler.post(() -> fetchPostFilterAndConcatenatecSubredditNamesListener.success(mergedPostFilter, null));
            }
        });
    }
}
