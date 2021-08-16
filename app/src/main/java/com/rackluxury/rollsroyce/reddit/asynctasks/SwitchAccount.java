package com.rackluxury.rollsroyce.reddit.asynctasks;

import android.content.SharedPreferences;
import android.os.Handler;

import java.util.concurrent.Executor;

import com.rackluxury.rollsroyce.reddit.account.Account;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

public class SwitchAccount {
    public static void switchAccount(RedditDataRoomDatabase redditDataRoomDatabase,
                                     SharedPreferences currentAccountSharedPreferences, Executor executor,
                                     Handler handler, String newAccountName,
                                     SwitchAccountListener switchAccountListener) {
        executor.execute(() -> {
            redditDataRoomDatabase.accountDao().markAllAccountsNonCurrent();
            redditDataRoomDatabase.accountDao().markAccountCurrent(newAccountName);
            Account account = redditDataRoomDatabase.accountDao().getCurrentAccount();
            currentAccountSharedPreferences.edit()
                    .putString(SharedPreferencesUtils.ACCESS_TOKEN, account.getAccessToken())
                    .putString(SharedPreferencesUtils.ACCOUNT_NAME, account.getAccountName())
                    .putString(SharedPreferencesUtils.ACCOUNT_IMAGE_URL, account.getProfileImageUrl()).apply();
            handler.post(() -> switchAccountListener.switched(account));
        });

    }

    public interface SwitchAccountListener {
        void switched(Account account);
    }
}
