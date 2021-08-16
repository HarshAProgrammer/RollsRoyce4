package com.rackluxury.rolex.reddit.asynctasks;

import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;
import com.rackluxury.rolex.reddit.subscribeduser.SubscribedUserData;

public class CheckIsFollowingUser {
    public static void checkIsFollowingUser(Executor executor, Handler handler,
                                            RedditDataRoomDatabase redditDataRoomDatabase, String username,
                                            String accountName, CheckIsFollowingUserListener checkIsFollowingUserListener) {
        executor.execute(() -> {
            SubscribedUserData subscribedUserData = redditDataRoomDatabase.subscribedUserDao().getSubscribedUser(username, accountName);
            handler.post(() -> {
                if (subscribedUserData != null) {
                    checkIsFollowingUserListener.isSubscribed();
                } else {
                    checkIsFollowingUserListener.isNotSubscribed();
                }
            });
        });
    }

    public interface CheckIsFollowingUserListener {
        void isSubscribed();

        void isNotSubscribed();
    }
}
