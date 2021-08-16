package com.rackluxury.rollsroyce.reddit.asynctasks;

import android.content.SharedPreferences;
import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.account.AccountDao;

public class SwitchToAnonymousMode {
    public static void switchToAnonymousMode(RedditDataRoomDatabase redditDataRoomDatabase, SharedPreferences currentAccountSharedPreferences,
                                             Executor executor, Handler handler, boolean removeCurrentAccount,
                                             SwitchToAnonymousAccountAsyncTaskListener switchToAnonymousAccountAsyncTaskListener) {
        executor.execute(() -> {
            AccountDao accountDao = redditDataRoomDatabase.accountDao();
            if (removeCurrentAccount) {
                accountDao.deleteCurrentAccount();
            }
            accountDao.markAllAccountsNonCurrent();

            currentAccountSharedPreferences.edit().clear().apply();

            handler.post(switchToAnonymousAccountAsyncTaskListener::logoutSuccess);
        });
    }

    public interface SwitchToAnonymousAccountAsyncTaskListener {
        void logoutSuccess();
    }
}
