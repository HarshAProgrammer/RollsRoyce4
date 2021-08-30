package com.rackluxury.rollsroyce.reddit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import com.rackluxury.rollsroyce.reddit.ActivityToolbarInterface;
import com.rackluxury.rollsroyce.reddit.AppBarStateChangeListener;
import com.rackluxury.rollsroyce.reddit.DeleteThing;
import com.rackluxury.rollsroyce.reddit.FragmentCommunicator;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.reddit.MarkPostAsReadInterface;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RecyclerViewContentScrollingInterface;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.SortType;
import com.rackluxury.rollsroyce.reddit.SortTypeSelectionCallback;
import com.rackluxury.rollsroyce.reddit.adapters.SubredditAutocompleteRecyclerViewAdapter;
import com.rackluxury.rollsroyce.reddit.apis.RedditAPI;
import com.rackluxury.rollsroyce.reddit.asynctasks.AddSubredditOrUserToMultiReddit;
import com.rackluxury.rollsroyce.reddit.asynctasks.CheckIsFollowingUser;
import com.rackluxury.rollsroyce.reddit.asynctasks.SwitchAccount;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.FABMoreOptionsBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.PostLayoutBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.PostTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.RandomBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.SortTimeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.UserThingSortTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.events.ChangeNSFWEvent;
import com.rackluxury.rollsroyce.reddit.events.GoBackToMainPageEvent;
import com.rackluxury.rollsroyce.reddit.events.SwitchAccountEvent;
import com.rackluxury.rollsroyce.reddit.fragments.CommentsListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.PostFragment;
import com.rackluxury.rollsroyce.reddit.message.ReadMessage;
import com.rackluxury.rollsroyce.reddit.multireddit.MultiReddit;
import com.rackluxury.rollsroyce.reddit.post.Post;
import com.rackluxury.rollsroyce.reddit.post.PostDataSource;
import com.rackluxury.rollsroyce.reddit.readpost.InsertReadPost;
import com.rackluxury.rollsroyce.reddit.subreddit.ParseSubredditData;
import com.rackluxury.rollsroyce.reddit.subreddit.SubredditData;
import com.rackluxury.rollsroyce.reddit.user.BlockUser;
import com.rackluxury.rollsroyce.reddit.user.FetchUserData;
import com.rackluxury.rollsroyce.reddit.user.UserDao;
import com.rackluxury.rollsroyce.reddit.user.UserData;
import com.rackluxury.rollsroyce.reddit.user.UserFollowing;
import com.rackluxury.rollsroyce.reddit.user.UserViewModel;
import com.rackluxury.rollsroyce.reddit.utils.APIUtils;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.rollsroyce.reddit.utils.Utils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RedditViewUserDetailActivity extends BaseActivity implements SortTypeSelectionCallback,
        PostTypeBottomSheetFragment.PostTypeSelectionCallback, PostLayoutBottomSheetFragment.PostLayoutSelectionCallback,
        ActivityToolbarInterface, FABMoreOptionsBottomSheetFragment.FABOptionSelectionCallback,
        RandomBottomSheetFragment.RandomOptionSelectionCallback, MarkPostAsReadInterface, RecyclerViewContentScrollingInterface {

    public static final String EXTRA_USER_NAME_KEY = "EUNK";
    public static final String EXTRA_MESSAGE_FULLNAME = "ENF";
    public static final String EXTRA_NEW_ACCOUNT_NAME = "ENAN";
    public static final int GIVE_AWARD_REQUEST_CODE = 200;
    public static final int EDIT_COMMENT_REQUEST_CODE = 300;
    public static final int ADD_TO_MULTIREDDIT_REQUEST_CODE = 400;

    private static final String FETCH_USER_INFO_STATE = "FSIS";
    private static final String IS_IN_LAZY_MODE_STATE = "IILMS";
    private static final String MESSAGE_FULLNAME_STATE = "MFS";
    private static final String NEW_ACCOUNT_NAME_STATE = "NANS";

    @BindView(R.id.coordinator_layout_view_user_detail_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.view_pager_view_user_detail_activity)
    ViewPager2 viewPager2;
    @BindView(R.id.appbar_layout_view_user_detail)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_view_user_detail_activity)
    Toolbar toolbar;
    @BindView(R.id.toolbar_linear_layout_view_user_detail_activity)
    LinearLayout linearLayout;
    @BindView(R.id.tab_layout_view_user_detail_activity)
    TabLayout tabLayout;
    @BindView(R.id.collapsing_toolbar_layout_view_user_detail_activity)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.banner_image_view_view_user_detail_activity)
    GifImageView bannerImageView;
    @BindView(R.id.icon_gif_image_view_view_user_detail_activity)
    GifImageView iconGifImageView;
    @BindView(R.id.user_name_text_view_view_user_detail_activity)
    TextView userNameTextView;
    @BindView(R.id.subscribe_user_chip_view_user_detail_activity)
    Chip subscribeUserChip;
    @BindView(R.id.karma_text_view_view_user_detail_activity)
    TextView karmaTextView;
    @BindView(R.id.cakeday_text_view_view_user_detail_activity)
    TextView cakedayTextView;
    @BindView(R.id.description_text_view_view_user_detail_activity)
    TextView descriptionTextView;
    @BindView(R.id.bottom_app_bar_bottom_app_bar)
    BottomAppBar bottomNavigationView;
    @BindView(R.id.linear_layout_bottom_app_bar)
    LinearLayout linearLayoutBottomAppBar;
    @BindView(R.id.option_1_bottom_app_bar)
    ImageView option1BottomAppBar;
    @BindView(R.id.option_2_bottom_app_bar)
    ImageView option2BottomAppBar;
    @BindView(R.id.option_3_bottom_app_bar)
    ImageView option3BottomAppBar;
    @BindView(R.id.option_4_bottom_app_bar)
    ImageView option4BottomAppBar;
    @BindView(R.id.fab_view_user_detail_activity)
    FloatingActionButton fab;
    @Inject
    @Named("no_oauth")
    Retrofit mRetrofit;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("sort_type")
    SharedPreferences mSortTypeSharedPreferences;
    @Inject
    @Named("post_layout")
    SharedPreferences mPostLayoutSharedPreferences;
    @Inject
    @Named("nsfw_and_spoiler")
    SharedPreferences mNsfwAndSpoilerSharedPreferences;
    @Inject
    @Named("bottom_app_bar")
    SharedPreferences mBottomAppBarSharedPreference;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    public UserViewModel userViewModel;
    private FragmentManager fragmentManager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private RequestManager glide;
    private Menu mMenu;
    private AppBarLayout.LayoutParams params;
    private UserThingSortTypeBottomSheetFragment userThingSortTypeBottomSheetFragment;
    private SortTimeBottomSheetFragment sortTimeBottomSheetFragment;
    private PostLayoutBottomSheetFragment postLayoutBottomSheetFragment;
    private Call<String> subredditAutocompleteCall;
    private String mAccessToken;
    private String mAccountName;
    private String username;
    private boolean subscriptionReady = false;
    private boolean mFetchUserInfoSuccess = false;
    private boolean isInLazyMode = false;
    private int expandedTabTextColor;
    private int expandedTabBackgroundColor;
    private int expandedTabIndicatorColor;
    private int collapsedTabTextColor;
    private int collapsedTabBackgroundColor;
    private int collapsedTabIndicatorColor;
    private int unsubscribedColor;
    private int subscribedColor;
    private int fabOption;
    private boolean showToast = false;
    private boolean showBottomAppBar;
    private boolean lockBottomAppBar;
    private String mMessageFullname;
    private String mNewAccountName;
    private SlidrInterface mSlidrInterface;
    //private MaterialAlertDialogBuilder nsfwWarningBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);
        setTransparentStatusBarAfterToolbarCollapsed();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_user_detail);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            mSlidrInterface = Slidr.attach(this);
        }

        username = getIntent().getStringExtra(EXTRA_USER_NAME_KEY);

        fragmentManager = getSupportFragmentManager();

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);

        if (savedInstanceState == null) {
            mMessageFullname = getIntent().getStringExtra(EXTRA_MESSAGE_FULLNAME);
            mNewAccountName = getIntent().getStringExtra(EXTRA_NEW_ACCOUNT_NAME);
        } else {
            mFetchUserInfoSuccess = savedInstanceState.getBoolean(FETCH_USER_INFO_STATE);
            isInLazyMode = savedInstanceState.getBoolean(IS_IN_LAZY_MODE_STATE);
            mMessageFullname = savedInstanceState.getString(MESSAGE_FULLNAME_STATE);
            mNewAccountName = savedInstanceState.getString(NEW_ACCOUNT_NAME_STATE);
        }

        checkNewAccountAndInitializeViewPager();

        fetchUserInfo();

        params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

        Resources resources = getResources();

        adjustToolbar(toolbar);

        String title = "u/" + username;
        userNameTextView.setText(title);
        toolbar.setTitle(title);

        setSupportActionBar(toolbar);
        setToolbarGoToTop(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            if (isImmersiveInterface()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    coordinatorLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                } else {
                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }

                int navBarHeight = getNavBarHeight();
                if (navBarHeight > 0) {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    params.bottomMargin += navBarHeight;
                    fab.setLayoutParams(params);
                    bottomNavigationView.setPadding(0, 0, 0, navBarHeight);
                }
                showToast = true;
            }

            View decorView = window.getDecorView();
            if (isChangeStatusBarIconColor()) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        if (state == State.COLLAPSED) {
                            decorView.setSystemUiVisibility(getSystemVisibilityToolbarCollapsed());
                            tabLayout.setTabTextColors(collapsedTabTextColor, collapsedTabTextColor);
                            tabLayout.setSelectedTabIndicatorColor(collapsedTabIndicatorColor);
                            tabLayout.setBackgroundColor(collapsedTabBackgroundColor);
                        } else if (state == State.EXPANDED) {
                            decorView.setSystemUiVisibility(getSystemVisibilityToolbarExpanded());
                            tabLayout.setTabTextColors(expandedTabTextColor, expandedTabTextColor);
                            tabLayout.setSelectedTabIndicatorColor(expandedTabIndicatorColor);
                            tabLayout.setBackgroundColor(expandedTabBackgroundColor);
                        }
                    }
                });
            } else {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        if (state == State.COLLAPSED) {
                            tabLayout.setTabTextColors(collapsedTabTextColor, collapsedTabTextColor);
                            tabLayout.setSelectedTabIndicatorColor(collapsedTabIndicatorColor);
                            tabLayout.setBackgroundColor(collapsedTabBackgroundColor);
                        } else if (state == State.EXPANDED) {
                            tabLayout.setTabTextColors(expandedTabTextColor, expandedTabTextColor);
                            tabLayout.setSelectedTabIndicatorColor(expandedTabIndicatorColor);
                            tabLayout.setBackgroundColor(expandedTabBackgroundColor);
                        }
                    }
                });
            }
        } else {
            appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                @Override
                public void onStateChanged(AppBarLayout appBarLayout, State state) {
                    if (state == State.EXPANDED) {
                        tabLayout.setTabTextColors(expandedTabTextColor, expandedTabTextColor);
                        tabLayout.setSelectedTabIndicatorColor(expandedTabIndicatorColor);
                        tabLayout.setBackgroundColor(expandedTabBackgroundColor);
                    } else if (state == State.COLLAPSED) {
                        tabLayout.setTabTextColors(collapsedTabTextColor, collapsedTabTextColor);
                        tabLayout.setSelectedTabIndicatorColor(collapsedTabIndicatorColor);
                        tabLayout.setBackgroundColor(collapsedTabBackgroundColor);
                    }
                }
            });
        }

        showBottomAppBar = mSharedPreferences.getBoolean(SharedPreferencesUtils.BOTTOM_APP_BAR_KEY, true);
        lockBottomAppBar = mSharedPreferences.getBoolean(SharedPreferencesUtils.LOCK_BOTTOM_APP_BAR, false);

        glide = Glide.with(this);
        Locale locale = getResources().getConfiguration().locale;

        userViewModel = new ViewModelProvider(this, new UserViewModel.Factory(getApplication(), mRedditDataRoomDatabase, username))
                .get(UserViewModel.class);
        userViewModel.getUserLiveData().observe(this, userData -> {
            if (userData != null) {
                if (userData.getBanner().equals("")) {
                    bannerImageView.setOnClickListener(null);
                } else {
                    glide.load(userData.getBanner()).into(bannerImageView);
                    bannerImageView.setOnClickListener(view -> {
                        Intent intent = new Intent(this, ViewImageOrGifActivity.class);
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_IMAGE_URL_KEY, userData.getBanner());
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, username + "-banner.jpg");
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, username);
                        startActivity(intent);
                    });
                }

                if (userData.getIconUrl().equals("")) {
                    glide.load(getDrawable(R.drawable.subreddit_default_icon))
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(216, 0)))
                            .into(iconGifImageView);
                    iconGifImageView.setOnClickListener(null);
                } else {
                    glide.load(userData.getIconUrl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(216, 0)))
                            .error(glide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(216, 0))))
                            .into(iconGifImageView);

                    iconGifImageView.setOnClickListener(view -> {
                        Intent intent = new Intent(this, ViewImageOrGifActivity.class);
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_IMAGE_URL_KEY, userData.getIconUrl());
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, username + "-icon.jpg");
                        intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, username);
                        startActivity(intent);
                    });
                }

                if (userData.isCanBeFollowed()) {
                    subscribeUserChip.setVisibility(View.VISIBLE);
                    subscribeUserChip.setOnClickListener(view -> {
                        if (mAccessToken == null) {
                            Toast.makeText(RedditViewUserDetailActivity.this, R.string.login_first, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (subscriptionReady) {
                            subscriptionReady = false;
                            if (resources.getString(R.string.follow).contentEquals(subscribeUserChip.getText())) {
                                UserFollowing.followUser(mOauthRetrofit, mRetrofit, mAccessToken,
                                        username, mAccountName, mRedditDataRoomDatabase, new UserFollowing.UserFollowingListener() {
                                            @Override
                                            public void onUserFollowingSuccess() {
                                                subscribeUserChip.setText(R.string.unfollow);
                                                subscribeUserChip.setChipBackgroundColor(ColorStateList.valueOf(subscribedColor));
                                                showMessage(R.string.followed, false);
                                                subscriptionReady = true;
                                            }

                                            @Override
                                            public void onUserFollowingFail() {
                                                showMessage(R.string.follow_failed, false);
                                                subscriptionReady = true;
                                            }
                                        });
                            } else {
                                UserFollowing.unfollowUser(mOauthRetrofit, mRetrofit, mAccessToken,
                                        username, mAccountName, mRedditDataRoomDatabase, new UserFollowing.UserFollowingListener() {
                                            @Override
                                            public void onUserFollowingSuccess() {
                                                subscribeUserChip.setText(R.string.follow);
                                                subscribeUserChip.setChipBackgroundColor(ColorStateList.valueOf(unsubscribedColor));
                                                showMessage(R.string.unfollowed, false);
                                                subscriptionReady = true;
                                            }

                                            @Override
                                            public void onUserFollowingFail() {
                                                showMessage(R.string.unfollow_failed, false);
                                                subscriptionReady = true;
                                            }
                                        });
                            }
                        }
                    });

                    CheckIsFollowingUser.checkIsFollowingUser(mExecutor, new Handler(), mRedditDataRoomDatabase,
                            username, mAccountName, new CheckIsFollowingUser.CheckIsFollowingUserListener() {
                        @Override
                        public void isSubscribed() {
                            subscribeUserChip.setText(R.string.unfollow);
                            subscribeUserChip.setChipBackgroundColor(ColorStateList.valueOf(subscribedColor));
                            subscriptionReady = true;
                        }

                        @Override
                        public void isNotSubscribed() {
                            subscribeUserChip.setText(R.string.follow);
                            subscribeUserChip.setChipBackgroundColor(ColorStateList.valueOf(unsubscribedColor));
                            subscriptionReady = true;
                        }
                    });
                } else {
                    subscribeUserChip.setVisibility(View.GONE);
                }

                String userFullName = "u/" + userData.getName();
                userNameTextView.setText(userFullName);
                if (!title.equals(userFullName)) {
                    getSupportActionBar().setTitle(userFullName);
                }
                String karma = getString(R.string.karma_info_user_detail, userData.getTotalKarma(), userData.getLinkKarma(), userData.getCommentKarma());
                karmaTextView.setText(karma);
                cakedayTextView.setText(getString(R.string.cakeday_info, new SimpleDateFormat("MMM d, yyyy",
                        locale).format(userData.getCakeday())));

                if (userData.getDescription() == null || userData.getDescription().equals("")) {
                    descriptionTextView.setVisibility(View.GONE);
                } else {
                    descriptionTextView.setVisibility(View.VISIBLE);
                    descriptionTextView.setText(userData.getDescription());
                }

                /*if (userData.isNSFW()) {
                    if (nsfwWarningBuilder == null
                            && !mNsfwAndSpoilerSharedPreferences.getBoolean((mAccountName == null ? "" : mAccountName) + SharedPreferencesUtils.NSFW_BASE, false)) {
                        nsfwWarningBuilder = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                                .setTitle(R.string.warning)
                                .setMessage(R.string.this_user_has_nsfw_content)
                                .setPositiveButton(R.string.leave, (dialogInterface, i)
                                        -> {
                                    finish();
                                })
                                .setNegativeButton(R.string.dismiss, null);
                        nsfwWarningBuilder.show();
                    }
                }*/
            }
        });

        userThingSortTypeBottomSheetFragment = new UserThingSortTypeBottomSheetFragment();
        sortTimeBottomSheetFragment = new SortTimeBottomSheetFragment();
        postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (sectionsPagerAdapter != null) {
            return sectionsPagerAdapter.handleKeyDown(keyCode) || super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return mSharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return mCustomThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        coordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        collapsingToolbarLayout.setContentScrimColor(mCustomThemeWrapper.getColorPrimary());
        applyAppBarLayoutAndToolbarTheme(appBarLayout, toolbar);
        expandedTabTextColor = mCustomThemeWrapper.getTabLayoutWithExpandedCollapsingToolbarTextColor();
        expandedTabIndicatorColor = mCustomThemeWrapper.getTabLayoutWithExpandedCollapsingToolbarTabIndicator();
        expandedTabBackgroundColor = mCustomThemeWrapper.getTabLayoutWithExpandedCollapsingToolbarTabBackground();
        collapsedTabTextColor = mCustomThemeWrapper.getTabLayoutWithCollapsedCollapsingToolbarTextColor();
        collapsedTabIndicatorColor = mCustomThemeWrapper.getTabLayoutWithCollapsedCollapsingToolbarTabIndicator();
        collapsedTabBackgroundColor = mCustomThemeWrapper.getTabLayoutWithCollapsedCollapsingToolbarTabBackground();
        linearLayout.setBackgroundColor(expandedTabBackgroundColor);
        unsubscribedColor = mCustomThemeWrapper.getUnsubscribed();
        subscribedColor = mCustomThemeWrapper.getSubscribed();
        userNameTextView.setTextColor(mCustomThemeWrapper.getUsername());
        karmaTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        cakedayTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        bottomNavigationView.setBackgroundTint(ColorStateList.valueOf(mCustomThemeWrapper.getBottomAppBarBackgroundColor()));
        int bottomAppBarIconColor = mCustomThemeWrapper.getBottomAppBarIconColor();
        option2BottomAppBar.setColorFilter(bottomAppBarIconColor, android.graphics.PorterDuff.Mode.SRC_IN);
        option1BottomAppBar.setColorFilter(bottomAppBarIconColor, android.graphics.PorterDuff.Mode.SRC_IN);
        option3BottomAppBar.setColorFilter(bottomAppBarIconColor, android.graphics.PorterDuff.Mode.SRC_IN);
        option4BottomAppBar.setColorFilter(bottomAppBarIconColor, android.graphics.PorterDuff.Mode.SRC_IN);
        applyFABTheme(fab);
        descriptionTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        subscribeUserChip.setTextColor(mCustomThemeWrapper.getChipTextColor());
        applyTabLayoutTheme(tabLayout);
    }

    private void checkNewAccountAndInitializeViewPager() {
        if (mNewAccountName != null) {
            if (mAccountName == null || !mAccountName.equals(mNewAccountName)) {
                SwitchAccount.switchAccount(mRedditDataRoomDatabase, mCurrentAccountSharedPreferences,
                        mExecutor, new Handler(), mNewAccountName, newAccount -> {
                            EventBus.getDefault().post(new SwitchAccountEvent(getClass().getName()));
                            Toast.makeText(this, R.string.account_switched, Toast.LENGTH_SHORT).show();

                            mNewAccountName = null;
                            if (newAccount != null) {
                                mAccessToken = newAccount.getAccessToken();
                                mAccountName = newAccount.getAccountName();
                            }

                            initializeViewPager();
                        });
            } else {
                initializeViewPager();
            }
        } else {
            initializeViewPager();
        }
    }

    private void initializeViewPager() {
        sectionsPagerAdapter = new SectionsPagerAdapter(this);
        viewPager2.setAdapter(sectionsPagerAdapter);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setUserInputEnabled(!mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_SWIPING_BETWEEN_TABS, false));
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.posts);
                    break;
                case 1:
                    tab.setText(R.string.comments);
                    break;
            }
        }).attach();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    unlockSwipeRightToGoBack();
                } else {
                    lockSwipeRightToGoBack();
                }

                if (mAccessToken != null) {
                    if (showBottomAppBar) {
                        bottomNavigationView.performShow();
                    }
                }
                fab.show();

                sectionsPagerAdapter.displaySortTypeInToolbar();
            }
        });

        fixViewPager2Sensitivity(viewPager2);

        if (mAccessToken != null) {
            if (mMessageFullname != null) {
                ReadMessage.readMessage(mOauthRetrofit, mAccessToken, mMessageFullname, new ReadMessage.ReadMessageListener() {
                    @Override
                    public void readSuccess() {
                        mMessageFullname = null;
                    }

                    @Override
                    public void readFailed() {

                    }
                });
            }

            if (showBottomAppBar) {
                int optionCount = mBottomAppBarSharedPreference.getInt(SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_COUNT, 4);
                int option1 = mBottomAppBarSharedPreference.getInt(SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_1, SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_HOME);
                int option2 = mBottomAppBarSharedPreference.getInt(SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_2, SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_SUBSCRIPTIONS);

                bottomNavigationView.setVisibility(View.VISIBLE);

                if (optionCount == 2) {
                    linearLayoutBottomAppBar.setWeightSum(3);
                    option1BottomAppBar.setVisibility(View.GONE);
                    option3BottomAppBar.setVisibility(View.GONE);

                    option2BottomAppBar.setImageResource(getBottomAppBarOptionDrawableResource(option1));
                    option4BottomAppBar.setImageResource(getBottomAppBarOptionDrawableResource(option2));

                    option2BottomAppBar.setOnClickListener(view -> {
                        bottomAppBarOptionAction(option1);
                    });

                    option4BottomAppBar.setOnClickListener(view -> {
                        bottomAppBarOptionAction(option2);
                    });
                } else {
                    int option3 = mBottomAppBarSharedPreference.getInt(SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_3, SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_INBOX);
                    int option4 = mBottomAppBarSharedPreference.getInt(SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_4, SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_PROFILE);

                    option1BottomAppBar.setImageResource(getBottomAppBarOptionDrawableResource(option1));
                    option2BottomAppBar.setImageResource(getBottomAppBarOptionDrawableResource(option2));
                    option3BottomAppBar.setImageResource(getBottomAppBarOptionDrawableResource(option3));
                    option4BottomAppBar.setImageResource(getBottomAppBarOptionDrawableResource(option4));

                    option1BottomAppBar.setOnClickListener(view -> {
                        bottomAppBarOptionAction(option1);
                    });

                    option2BottomAppBar.setOnClickListener(view -> {
                        bottomAppBarOptionAction(option2);
                    });

                    option3BottomAppBar.setOnClickListener(view -> {
                        bottomAppBarOptionAction(option3);
                    });

                    option4BottomAppBar.setOnClickListener(view -> {
                        bottomAppBarOptionAction(option4);
                    });
                }
            } else {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                lp.setAnchorId(View.NO_ID);
                lp.gravity = Gravity.END | Gravity.BOTTOM;
                fab.setLayoutParams(lp);
            }
        } else {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            lp.setAnchorId(View.NO_ID);
            lp.gravity = Gravity.END | Gravity.BOTTOM;
            fab.setLayoutParams(lp);
            bottomNavigationView.setVisibility(View.GONE);
        }
        fabOption = mBottomAppBarSharedPreference.getInt(SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB, SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_SUBMIT_POSTS);
        switch (fabOption) {
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_REFRESH:
                fab.setImageResource(R.drawable.ic_refresh_24dp);
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_CHANGE_SORT_TYPE:
                fab.setImageResource(R.drawable.ic_sort_toolbar_24dp);
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_CHANGE_POST_LAYOUT:
                fab.setImageResource(R.drawable.ic_post_layout_24dp);
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_SEARCH:
                fab.setImageResource(R.drawable.ic_search_black_24dp);
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_GO_TO_SUBREDDIT:
                fab.setImageResource(R.drawable.ic_subreddit_24dp);
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_GO_TO_USER:
                fab.setImageResource(R.drawable.ic_user_24dp);
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_RANDOM:
                fab.setImageResource(R.drawable.ic_random_24dp);
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_HIDE_READ_POSTS:
                if (mAccessToken == null) {
                    fab.setImageResource(R.drawable.ic_filter_24dp);
                    fabOption = SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_FILTER_POSTS;
                } else {
                    fab.setImageResource(R.drawable.ic_hide_read_posts_24dp);
                }
                break;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_FILTER_POSTS:
                fab.setImageResource(R.drawable.ic_filter_24dp);
                break;
            default:
                if (mAccessToken == null) {
                    fab.setImageResource(R.drawable.ic_filter_24dp);
                    fabOption = SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_FILTER_POSTS;
                } else {
                    fab.setImageResource(R.drawable.ic_add_day_night_24dp);
                }
                break;
        }
        fab.setOnClickListener(view -> {
            switch (fabOption) {
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_REFRESH: {
                    if (sectionsPagerAdapter != null) {
                        sectionsPagerAdapter.refresh();
                    }
                    break;
                }
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_CHANGE_SORT_TYPE: {
                    userThingSortTypeBottomSheetFragment.show(getSupportFragmentManager(), userThingSortTypeBottomSheetFragment.getTag());
                    break;
                }
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_CHANGE_POST_LAYOUT: {
                    postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
                    break;
                }
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_SEARCH: {
                    Intent intent = new Intent(this, RedditSearchActivity.class);
                    startActivity(intent);
                    break;
                }
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_GO_TO_SUBREDDIT:
                    goToSubreddit();
                    break;
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_GO_TO_USER:
                    goToUser();
                    break;
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_RANDOM:
                    random();
                    break;
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_HIDE_READ_POSTS:
                    if (sectionsPagerAdapter != null) {
                        sectionsPagerAdapter.hideReadPosts();
                    }
                    break;
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_FILTER_POSTS:
                    if (sectionsPagerAdapter != null) {
                        sectionsPagerAdapter.filterPosts();
                    }
                    break;
                default:
                    PostTypeBottomSheetFragment postTypeBottomSheetFragment = new PostTypeBottomSheetFragment();
                    postTypeBottomSheetFragment.show(getSupportFragmentManager(), postTypeBottomSheetFragment.getTag());
                    break;
            }
        });
        fab.setOnLongClickListener(view -> {
            FABMoreOptionsBottomSheetFragment fabMoreOptionsBottomSheetFragment = new FABMoreOptionsBottomSheetFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(FABMoreOptionsBottomSheetFragment.EXTRA_ANONYMOUS_MODE, mAccessToken == null);
            fabMoreOptionsBottomSheetFragment.setArguments(bundle);
            fabMoreOptionsBottomSheetFragment.show(getSupportFragmentManager(), fabMoreOptionsBottomSheetFragment.getTag());
            return true;
        });

        fab.setVisibility(View.VISIBLE);
    }

    private void bottomAppBarOptionAction(int option) {
        switch (option) {
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_HOME: {
                EventBus.getDefault().post(new GoBackToMainPageEvent());
                break;
            }
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_SUBSCRIPTIONS: {
                Intent intent = new Intent(this, RedditSubscribedThingListingActivity.class);
                startActivity(intent);
                break;
            }
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_INBOX: {
                Intent intent = new Intent(this, RedditInboxActivity.class);
                startActivity(intent);
                break;
            }
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_PROFILE: {
                Intent intent = new Intent(this, RedditViewUserDetailActivity.class);
                intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, mAccountName);
                startActivity(intent);
                break;
            }
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_MULTIREDDITS: {
                Intent intent = new Intent(this, RedditSubscribedThingListingActivity.class);
                intent.putExtra(RedditSubscribedThingListingActivity.EXTRA_SHOW_MULTIREDDITS, true);
                startActivity(intent);
                break;
            }
        }
    }

    private int getBottomAppBarOptionDrawableResource(int option) {
        switch (option) {
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_HOME:
                return R.drawable.ic_home_black_24dp;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_SUBSCRIPTIONS:
                return R.drawable.ic_subscritptions_bottom_app_bar_24dp;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_INBOX:
                return R.drawable.ic_inbox_24dp;
            case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_OPTION_MULTIREDDITS:
                return R.drawable.ic_multi_reddit_24dp;
            default:
                return R.drawable.ic_account_circle_24dp;
        }
    }

    private void fetchUserInfo() {
        if (!mFetchUserInfoSuccess) {
            FetchUserData.fetchUserData(mRetrofit, username, new FetchUserData.FetchUserDataListener() {
                @Override
                public void onFetchUserDataSuccess(UserData userData, int inboxCount) {
                    new InsertUserDataAsyncTask(mRedditDataRoomDatabase.userDao(), userData,
                            () -> mFetchUserInfoSuccess = true).execute();
                }

                @Override
                public void onFetchUserDataFailed() {
                    showMessage(R.string.cannot_fetch_user_info, true);
                    mFetchUserInfoSuccess = false;
                }
            });
        }
    }

    public void deleteComment(String fullName) {
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                .setTitle(R.string.delete_this_comment)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.delete, (dialogInterface, i)
                        -> DeleteThing.delete(mOauthRetrofit, fullName, mAccessToken, new DeleteThing.DeleteThingListener() {
                    @Override
                    public void deleteSuccess() {
                        Toast.makeText(RedditViewUserDetailActivity.this, R.string.delete_post_success, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void deleteFailed() {
                        Toast.makeText(RedditViewUserDetailActivity.this, R.string.delete_post_failed, Toast.LENGTH_SHORT).show();
                    }
                }))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_user_detail_activity, menu);
        applyMenuItemTheme(menu);
        mMenu = menu;
        MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_view_user_detail_activity);
        if (isInLazyMode) {
            lazyModeItem.setTitle(R.string.action_stop_lazy_mode);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            collapsingToolbarLayout.setLayoutParams(params);
        } else {
            lazyModeItem.setTitle(R.string.action_start_lazy_mode);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
            collapsingToolbarLayout.setLayoutParams(params);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_sort_view_user_detail_activity) {
            userThingSortTypeBottomSheetFragment.show(getSupportFragmentManager(), userThingSortTypeBottomSheetFragment.getTag());
            return true;
        } else if (itemId == R.id.action_search_view_user_detail_activity) {
            Intent intent = new Intent(this, RedditSearchActivity.class);
            intent.putExtra(RedditSearchActivity.EXTRA_SUBREDDIT_NAME, username);
            intent.putExtra(RedditSearchActivity.EXTRA_SUBREDDIT_IS_USER, true);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_refresh_view_user_detail_activity) {
            if (mMenu != null) {
                mMenu.findItem(R.id.action_lazy_mode_view_user_detail_activity).setTitle(R.string.action_start_lazy_mode);
            }
            sectionsPagerAdapter.refresh();
            mFetchUserInfoSuccess = false;
            fetchUserInfo();
            return true;
        } else if (itemId == R.id.action_lazy_mode_view_user_detail_activity) {
            MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_view_user_detail_activity);
            if (isInLazyMode) {
                isInLazyMode = false;
                sectionsPagerAdapter.stopLazyMode();
                lazyModeItem.setTitle(R.string.action_start_lazy_mode);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);
                collapsingToolbarLayout.setLayoutParams(params);
            } else {
                isInLazyMode = true;
                if (sectionsPagerAdapter.startLazyMode()) {
                    lazyModeItem.setTitle(R.string.action_stop_lazy_mode);
                    appBarLayout.setExpanded(false);
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                    collapsingToolbarLayout.setLayoutParams(params);
                } else {
                    isInLazyMode = false;
                }
            }
            return true;
        } else if (itemId == R.id.action_change_post_layout_view_user_detail_activity) {
            postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
            return true;
        } else if (itemId == R.id.action_share_view_user_detail_activity) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.reddit.com/user/" + username);
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } else {
                Toast.makeText(this, R.string.no_app, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_send_private_message_view_user_detail_activity) {
            if (mAccessToken == null) {
                Toast.makeText(this, R.string.login_first, Toast.LENGTH_SHORT).show();
                return true;
            }

            Intent pmIntent = new Intent(this, SendPrivateMessageActivity.class);
            pmIntent.putExtra(SendPrivateMessageActivity.EXTRA_RECIPIENT_USERNAME, username);
            startActivity(pmIntent);
            return true;
        } else if (itemId == R.id.action_add_to_multireddit_view_user_detail_activity) {
            if (mAccessToken == null) {
                Toast.makeText(this, R.string.login_first, Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(this, MultiredditSelectionActivity.class);
            startActivityForResult(intent, ADD_TO_MULTIREDDIT_REQUEST_CODE);
        } else if (itemId == R.id.action_add_to_post_filter_view_user_detail_activity) {
            Intent intent = new Intent(this, RedditPostFilterPreferenceActivity.class);
            intent.putExtra(RedditPostFilterPreferenceActivity.EXTRA_USER_NAME, username);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_report_view_user_detail_activity) {
            Intent reportIntent = new Intent(this, RedditLinkResolverActivity.class);
            reportIntent.setData(Uri.parse("https://www.reddithelp.com/en/categories/rules-reporting/account-and-community-restrictions/what-should-i-do-if-i-see-something-i"));
            startActivity(reportIntent);
            return true;
        } else if (itemId == R.id.action_block_user_view_user_detail_activity) {
            if (mAccessToken == null) {
                Toast.makeText(this, R.string.login_first, Toast.LENGTH_SHORT).show();
                return true;
            }

            new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                    .setTitle(R.string.block_user)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.yes, (dialogInterface, i)
                            -> BlockUser.blockUser(mOauthRetrofit, mAccessToken, username, new BlockUser.BlockUserListener() {
                        @Override
                        public void success() {
                            Toast.makeText(RedditViewUserDetailActivity.this, R.string.block_user_success, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failed() {
                            Toast.makeText(RedditViewUserDetailActivity.this, R.string.block_user_failed, Toast.LENGTH_SHORT).show();
                        }
                    }))
                    .setNegativeButton(R.string.no, null)
                    .show();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GIVE_AWARD_REQUEST_CODE) {
                Toast.makeText(this, R.string.give_award_success, Toast.LENGTH_SHORT).show();
                int position = data.getIntExtra(GiveAwardActivity.EXTRA_RETURN_ITEM_POSITION, 0);
                String newAwardsHTML = data.getStringExtra(GiveAwardActivity.EXTRA_RETURN_NEW_AWARDS);
                if (sectionsPagerAdapter != null) {
                    sectionsPagerAdapter.giveAward(newAwardsHTML, position);
                }
            } else if (requestCode == EDIT_COMMENT_REQUEST_CODE) {
                if (data != null) {
                    if (sectionsPagerAdapter != null) {
                        sectionsPagerAdapter.editComment(
                                data.getStringExtra(RedditEditCommentActivity.EXTRA_EDITED_COMMENT_CONTENT),
                                data.getExtras().getInt(RedditEditCommentActivity.EXTRA_EDITED_COMMENT_POSITION));
                    }
                }
            } else if (requestCode == ADD_TO_MULTIREDDIT_REQUEST_CODE) {
                if (data != null) {
                    MultiReddit multiReddit = data.getParcelableExtra(MultiredditSelectionActivity.EXTRA_RETURN_MULTIREDDIT);
                    if (multiReddit != null) {
                        AddSubredditOrUserToMultiReddit.addSubredditOrUserToMultiReddit(mOauthRetrofit,
                                mAccessToken, multiReddit.getPath(), "u_" + username,
                                new AddSubredditOrUserToMultiReddit.AddSubredditOrUserToMultiRedditListener() {
                                    @Override
                                    public void success() {
                                        Toast.makeText(RedditViewUserDetailActivity.this,
                                                getString(R.string.add_subreddit_or_user_to_multireddit_success, username, multiReddit.getDisplayName()), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void failed(int code) {
                                        Toast.makeText(RedditViewUserDetailActivity.this,
                                                getString(R.string.add_subreddit_or_user_to_multireddit_failed, username, multiReddit.getDisplayName()), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }

                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FETCH_USER_INFO_STATE, mFetchUserInfoSuccess);
        outState.putBoolean(IS_IN_LAZY_MODE_STATE, isInLazyMode);
        outState.putString(MESSAGE_FULLNAME_STATE, mMessageFullname);
        outState.putString(NEW_ACCOUNT_NAME_STATE, mNewAccountName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void showMessage(int resId, boolean retry) {
        if (showToast) {
            Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
        } else {
            if (retry) {
                Snackbar.make(coordinatorLayout, resId, Snackbar.LENGTH_SHORT).setAction(R.string.retry,
                        view -> fetchUserInfo()).show();
            } else {
                Snackbar.make(coordinatorLayout, resId, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void sortTypeSelected(SortType sortType) {
        sectionsPagerAdapter.changeSortType(sortType);
    }

    @Override
    public void sortTypeSelected(String sortType) {
        Bundle bundle = new Bundle();
        bundle.putString(SortTimeBottomSheetFragment.EXTRA_SORT_TYPE, sortType);
        sortTimeBottomSheetFragment.setArguments(bundle);
        sortTimeBottomSheetFragment.show(getSupportFragmentManager(), sortTimeBottomSheetFragment.getTag());
    }

    @Override
    public void postLayoutSelected(int postLayout) {
        sectionsPagerAdapter.changePostLayout(postLayout);
    }

    @Override
    public void fabOptionSelected(int option) {
        switch (option) {
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_SUBMIT_POST:
                PostTypeBottomSheetFragment postTypeBottomSheetFragment = new PostTypeBottomSheetFragment();
                postTypeBottomSheetFragment.show(getSupportFragmentManager(), postTypeBottomSheetFragment.getTag());
                break;
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_REFRESH:
                if (sectionsPagerAdapter != null) {
                    sectionsPagerAdapter.refresh();
                }
                break;
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_CHANGE_SORT_TYPE:
                userThingSortTypeBottomSheetFragment.show(getSupportFragmentManager(), userThingSortTypeBottomSheetFragment.getTag());
                break;
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_CHANGE_POST_LAYOUT:
                postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
                break;
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_SEARCH:
                Intent intent = new Intent(this, RedditSearchActivity.class);
                startActivity(intent);
                break;
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_GO_TO_SUBREDDIT: {
                goToSubreddit();
                break;
            }
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_GO_TO_USER: {
                goToUser();
                break;
            }
            case FABMoreOptionsBottomSheetFragment.FAB_RANDOM: {
                random();
                break;
            }
            case FABMoreOptionsBottomSheetFragment.FAB_HIDE_READ_POSTS: {
                if (sectionsPagerAdapter != null) {
                    sectionsPagerAdapter.hideReadPosts();
                }
                break;
            }
            case FABMoreOptionsBottomSheetFragment.FAB_FILTER_POSTS: {
                if (sectionsPagerAdapter != null) {
                    sectionsPagerAdapter.filterPosts();
                }
                break;
            }
        }
    }

    private void goToSubreddit() {
        View rootView = getLayoutInflater().inflate(R.layout.dialog_go_to_thing_edit_text, coordinatorLayout, false);
        TextInputEditText thingEditText = rootView.findViewById(R.id.text_input_edit_text_go_to_thing_edit_text);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_go_to_thing_edit_text);
        thingEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        SubredditAutocompleteRecyclerViewAdapter adapter = new SubredditAutocompleteRecyclerViewAdapter(
                this, mCustomThemeWrapper, subredditData -> {
            if (imm != null) {
                imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
            }
            Intent intent = new Intent(RedditViewUserDetailActivity.this, RedditViewSubredditDetailActivity.class);
            intent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY, subredditData.getName());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        thingEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                }
                Intent subredditIntent = new Intent(this, RedditViewSubredditDetailActivity.class);
                subredditIntent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY, thingEditText.getText().toString());
                startActivity(subredditIntent);
                return true;
            }
            return false;
        });

        boolean nsfw = mNsfwAndSpoilerSharedPreferences.getBoolean((mAccountName == null ? "" : mAccountName) + SharedPreferencesUtils.NSFW_BASE, false);
        thingEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (subredditAutocompleteCall != null) {
                    subredditAutocompleteCall.cancel();
                }
                subredditAutocompleteCall = mOauthRetrofit.create(RedditAPI.class).subredditAutocomplete(APIUtils.getOAuthHeader(mAccessToken),
                        editable.toString(), nsfw);
                subredditAutocompleteCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            ParseSubredditData.parseSubredditListingData(response.body(), nsfw, new ParseSubredditData.ParseSubredditListingDataListener() {
                                @Override
                                public void onParseSubredditListingDataSuccess(ArrayList<SubredditData> subredditData, String after) {
                                    adapter.setSubreddits(subredditData);
                                }

                                @Override
                                public void onParseSubredditListingDataFail() {

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                    }
                });
            }
        });
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                .setTitle(R.string.go_to_subreddit)
                .setView(rootView)
                .setPositiveButton(R.string.ok, (dialogInterface, i)
                        -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                    }
                    Intent subredditIntent = new Intent(this, RedditViewSubredditDetailActivity.class);
                    subredditIntent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY, thingEditText.getText().toString());
                    startActivity(subredditIntent);
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                    }
                })
                .setOnDismissListener(dialogInterface -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                    }
                })
                .show();
    }

    private void goToUser() {
        View rootView = getLayoutInflater().inflate(R.layout.dialog_go_to_thing_edit_text, coordinatorLayout, false);
        TextInputEditText thingEditText = rootView.findViewById(R.id.text_input_edit_text_go_to_thing_edit_text);
        thingEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        thingEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                }
                Intent userIntent = new Intent(this, RedditViewUserDetailActivity.class);
                userIntent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, thingEditText.getText().toString());
                startActivity(userIntent);
                return true;
            }
            return false;
        });
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                .setTitle(R.string.go_to_user)
                .setView(rootView)
                .setPositiveButton(R.string.ok, (dialogInterface, i)
                        -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                    }
                    Intent userIntent = new Intent(this, RedditViewUserDetailActivity.class);
                    userIntent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, thingEditText.getText().toString());
                    startActivity(userIntent);
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                    }
                })
                .setOnDismissListener(dialogInterface -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(thingEditText.getWindowToken(), 0);
                    }
                })
                .show();
    }

    private void random() {
        RandomBottomSheetFragment randomBottomSheetFragment = new RandomBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(RandomBottomSheetFragment.EXTRA_IS_NSFW, !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean((mAccountName == null ? "" : mAccountName) + SharedPreferencesUtils.NSFW_BASE, false));
        randomBottomSheetFragment.setArguments(bundle);
        randomBottomSheetFragment.show(getSupportFragmentManager(), randomBottomSheetFragment.getTag());
    }

    @Override
    public void randomOptionSelected(int option) {
        Intent intent = new Intent(this, FetchRandomSubredditOrPostActivity.class);
        intent.putExtra(FetchRandomSubredditOrPostActivity.EXTRA_RANDOM_OPTION, option);
        startActivity(intent);
    }

    @Override
    public void contentScrollUp() {
        if (mAccessToken != null) {
            if (showBottomAppBar && !lockBottomAppBar) {
                bottomNavigationView.performShow();
            }
            if (!(showBottomAppBar && lockBottomAppBar)) {
                fab.show();
            }
        }
    }

    @Override
    public void contentScrollDown() {
        if (mAccessToken != null) {
            if (!(showBottomAppBar && lockBottomAppBar)) {
                fab.hide();
            }
            if (showBottomAppBar && !lockBottomAppBar) {
                bottomNavigationView.performHide();
            }
        }
    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        if (!getClass().getName().equals(event.excludeActivityClassName)) {
            finish();
        }
    }

    @Subscribe
    public void onChangeNSFWEvent(ChangeNSFWEvent changeNSFWEvent) {
        sectionsPagerAdapter.changeNSFW(changeNSFWEvent.nsfw);
    }

    @Subscribe
    public void goBackToMainPageEvent(GoBackToMainPageEvent event) {
        finish();
    }

    @Override
    public void onLongPress() {
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.goBackToTop();
        }
    }

    @Override
    public void displaySortType() {
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.displaySortTypeInToolbar();
        }
    }

    @Override
    public void markPostAsRead(Post post) {
        InsertReadPost.insertReadPost(mRedditDataRoomDatabase, mExecutor, mAccountName, post.getId());
    }

    @Override
    public void postTypeSelected(int postType) {
        Intent intent;
        switch (postType) {
            case PostTypeBottomSheetFragment.TYPE_TEXT:
                intent = new Intent(this, RedditPostTextActivity.class);
                startActivity(intent);
                break;
            case PostTypeBottomSheetFragment.TYPE_LINK:
                intent = new Intent(this, RedditPostLinkActivity.class);
                startActivity(intent);
                break;
            case PostTypeBottomSheetFragment.TYPE_IMAGE:
                intent = new Intent(this, RedditPostImageActivity.class);
                startActivity(intent);
                break;
            case PostTypeBottomSheetFragment.TYPE_VIDEO:
                intent = new Intent(this, RedditPostVideoActivity.class);
                startActivity(intent);
                break;
            case PostTypeBottomSheetFragment.TYPE_GALLERY:
                intent = new Intent(this, RedditPostGalleryActivity.class);
                startActivity(intent);
        }
    }

    private static class InsertUserDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private final UserDao userDao;
        private final UserData subredditData;
        private final InsertUserDataAsyncTaskListener insertUserDataAsyncTaskListener;
        InsertUserDataAsyncTask(UserDao userDao, UserData userData,
                                InsertUserDataAsyncTaskListener insertUserDataAsyncTaskListener) {
            this.userDao = userDao;
            this.subredditData = userData;
            this.insertUserDataAsyncTaskListener = insertUserDataAsyncTaskListener;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            userDao.insert(subredditData);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            insertUserDataAsyncTaskListener.insertSuccess();
        }

        interface InsertUserDataAsyncTaskListener {
            void insertSuccess();
        }
    }

    private class SectionsPagerAdapter extends FragmentStateAdapter {

        SectionsPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                PostFragment fragment = new PostFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(PostFragment.EXTRA_POST_TYPE, PostDataSource.TYPE_USER);
                bundle.putString(PostFragment.EXTRA_USER_NAME, username);
                bundle.putString(PostFragment.EXTRA_USER_WHERE, PostDataSource.USER_WHERE_SUBMITTED);
                bundle.putString(PostFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                bundle.putString(PostFragment.EXTRA_ACCOUNT_NAME, mAccountName);
                fragment.setArguments(bundle);
                return fragment;
            }
            CommentsListingFragment fragment = new CommentsListingFragment();
            Bundle bundle = new Bundle();
            bundle.putString(CommentsListingFragment.EXTRA_USERNAME, username);
            bundle.putString(CommentsListingFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
            bundle.putString(CommentsListingFragment.EXTRA_ACCOUNT_NAME, mAccountName);
            bundle.putBoolean(CommentsListingFragment.EXTRA_ARE_SAVED_COMMENTS, false);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @Nullable
        private Fragment getCurrentFragment() {
            if (viewPager2 == null || fragmentManager == null) {
                return null;
            }
            return fragmentManager.findFragmentByTag("f" + viewPager2.getCurrentItem());
        }

        public boolean handleKeyDown(int keyCode) {
            if (viewPager2.getCurrentItem() == 0) {
                Fragment fragment = getCurrentFragment();
                if (fragment instanceof PostFragment) {
                    return ((PostFragment) fragment).handleKeyDown(keyCode);
                }
            }
            return false;
        }

        public void refresh() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).refresh();
            } else if (fragment instanceof CommentsListingFragment) {
                ((CommentsListingFragment) fragment).refresh();
            }
        }

        boolean startLazyMode() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof FragmentCommunicator) {
                return ((FragmentCommunicator) fragment).startLazyMode();
            }
            return false;
        }

        void stopLazyMode() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof FragmentCommunicator) {
                ((FragmentCommunicator) fragment).stopLazyMode();
            }
        }

        public void changeSortType(SortType sortType) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).changeSortType(sortType);
                Utils.displaySortTypeInToolbar(sortType, toolbar);
            } else if (fragment instanceof CommentsListingFragment) {
                mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TYPE_USER_COMMENT, sortType.getType().name()).apply();
                if(sortType.getTime() != null) {
                    mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TIME_USER_COMMENT, sortType.getTime().name()).apply();
                }
                ((CommentsListingFragment) fragment).changeSortType(sortType);
                Utils.displaySortTypeInToolbar(sortType, toolbar);
            }
        }

        public void changeNSFW(boolean nsfw) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).changeNSFW(nsfw);
            }
        }

        void changePostLayout(int postLayout) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).changePostLayout(postLayout);
            }
        }

        void goBackToTop() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).goBackToTop();
            } else if (fragment instanceof CommentsListingFragment) {
                ((CommentsListingFragment) fragment).goBackToTop();
            }
        }

        void displaySortTypeInToolbar() {
            if (fragmentManager != null) {
                Fragment fragment = fragmentManager.findFragmentByTag("f" + viewPager2.getCurrentItem());
                if (fragment instanceof PostFragment) {
                    SortType sortType = ((PostFragment) fragment).getSortType();
                    Utils.displaySortTypeInToolbar(sortType, toolbar);
                } else if (fragment instanceof CommentsListingFragment) {
                    SortType sortType = ((CommentsListingFragment) fragment).getSortType();
                    Utils.displaySortTypeInToolbar(sortType, toolbar);
                }
            }
        }

        void giveAward(String awardsHTML, int position) {
            if (fragmentManager != null) {
                Fragment fragment = fragmentManager.findFragmentByTag("f1");
                if (fragment instanceof CommentsListingFragment) {
                    ((CommentsListingFragment) fragment).giveAward(awardsHTML, position);
                }
            }
        }

        void editComment(String commentMarkdown, int position) {
            if (fragmentManager != null) {
                Fragment fragment = fragmentManager.findFragmentByTag("f1");
                if (fragment instanceof CommentsListingFragment) {
                    ((CommentsListingFragment) fragment).editComment(commentMarkdown, position);
                }
            }
        }

        void hideReadPosts() {
            if (fragmentManager != null) {
                Fragment fragment = fragmentManager.findFragmentByTag("f0");
                if (fragment instanceof PostFragment) {
                    ((PostFragment) fragment).hideReadPosts();
                }
            }
        }

        void filterPosts() {
            if (fragmentManager != null) {
                Fragment fragment = fragmentManager.findFragmentByTag("f0");
                if (fragment instanceof PostFragment) {
                    ((PostFragment) fragment).filterPosts();
                }
            }
        }
    }

    private void lockSwipeRightToGoBack() {
        if (mSlidrInterface != null) {
            mSlidrInterface.lock();
        }
    }

    private void unlockSwipeRightToGoBack() {
        if (mSlidrInterface != null) {
            mSlidrInterface.unlock();
        }
    }
}
