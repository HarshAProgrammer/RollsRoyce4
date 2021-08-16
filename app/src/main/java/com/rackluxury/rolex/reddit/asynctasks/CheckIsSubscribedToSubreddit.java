package com.rackluxury.rolex.reddit.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;
import com.rackluxury.rolex.reddit.account.Account;
import com.rackluxury.rolex.reddit.subscribedsubreddit.SubscribedSubredditData;

public class CheckIsSubscribedToSubreddit {

    public static void checkIsSubscribedToSubreddit(Executor executor, Handler handler, RedditDataRoomDatabase redditDataRoomDatabase,
                                                    String subredditName, String accountName,
                                                    CheckIsSubscribedToSubredditListener checkIsSubscribedToSubredditListener) {
        executor.execute(() -> {
            if (accountName == null) {
                if (!redditDataRoomDatabase.accountDao().isAnonymousAccountInserted()) {
                    redditDataRoomDatabase.accountDao().insert(Account.getAnonymousAccount());
                }
            }
            SubscribedSubredditData subscribedSubredditData = redditDataRoomDatabase.subscribedSubredditDao().getSubscribedSubreddit(subredditName, accountName == null ? "-" : accountName);
            handler.post(() -> {
                if (subscribedSubredditData != null) {
                    checkIsSubscribedToSubredditListener.isSubscribed();
                } else {
                    checkIsSubscribedToSubredditListener.isNotSubscribed();
                }
            });
        });
    }

    public interface CheckIsSubscribedToSubredditListener {
        void isSubscribed();

        void isNotSubscribed();
    }
}
