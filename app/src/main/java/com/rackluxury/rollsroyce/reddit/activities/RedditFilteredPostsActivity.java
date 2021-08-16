package com.rackluxury.rollsroyce.reddit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.reddit.ActivityToolbarInterface;
import com.rackluxury.rollsroyce.reddit.FragmentCommunicator;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.reddit.MarkPostAsReadInterface;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RecyclerViewContentScrollingInterface;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.SortType;
import com.rackluxury.rollsroyce.reddit.SortTypeSelectionCallback;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.FilteredThingFABMoreOptionsBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.PostLayoutBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.SearchPostSortTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.SortTimeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.SortTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.UserThingSortTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.events.SwitchAccountEvent;
import com.rackluxury.rollsroyce.reddit.fragments.PostFragment;
import com.rackluxury.rollsroyce.reddit.post.Post;
import com.rackluxury.rollsroyce.reddit.post.PostDataSource;
import com.rackluxury.rollsroyce.reddit.postfilter.PostFilter;
import com.rackluxury.rollsroyce.reddit.readpost.InsertReadPost;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;

public class RedditFilteredPostsActivity extends BaseActivity implements SortTypeSelectionCallback,
        PostLayoutBottomSheetFragment.PostLayoutSelectionCallback, ActivityToolbarInterface,
        MarkPostAsReadInterface, FilteredThingFABMoreOptionsBottomSheetFragment.FABOptionSelectionCallback,
        RecyclerViewContentScrollingInterface {

    public static final String EXTRA_NAME = "ESN";
    public static final String EXTRA_QUERY = "EQ";
    public static final String EXTRA_TRENDING_SOURCE = "ETS";
    public static final String EXTRA_FILTER = "EF";
    public static final String EXTRA_CONTAIN_FLAIR = "ECF";
    public static final String EXTRA_POST_TYPE = "EPT";
    public static final String EXTRA_USER_WHERE = "EUW";

    private static final String IS_IN_LAZY_MODE_STATE = "IILMS";
    private static final String FRAGMENT_OUT_STATE = "FOS";
    private static final int CUSTOMIZE_POST_FILTER_ACTIVITY_REQUEST_CODE = 1000;

    @BindView(R.id.coordinator_layout_filtered_thing_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_filtered_posts_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout_filtered_posts_activity)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_filtered_posts_activity)
    Toolbar toolbar;
    @BindView(R.id.fab_filtered_thing_activity)
    FloatingActionButton fab;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("post_layout")
    SharedPreferences mPostLayoutSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    private boolean isInLazyMode = false;
    private String mAccessToken;
    private String mAccountName;
    private String name;
    private String userWhere;
    private int postType;
    private PostFragment mFragment;
    private Menu mMenu;
    private AppBarLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filtered_thing);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            Slidr.attach(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();

            if (isChangeStatusBarIconColor()) {
                addOnOffsetChangedListener(appBarLayout);
            }

            if (isImmersiveInterface()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    coordinatorLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                } else {
                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
                adjustToolbar(toolbar);

                int navBarHeight = getNavBarHeight();
                if (navBarHeight > 0) {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    params.bottomMargin += navBarHeight;
                    fab.setLayoutParams(params);
                }
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarGoToTop(toolbar);

        params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

        name = getIntent().getStringExtra(EXTRA_NAME);
        postType = getIntent().getIntExtra(EXTRA_POST_TYPE, PostDataSource.TYPE_FRONT_PAGE);
        int filter = getIntent().getIntExtra(EXTRA_FILTER, -1000);
        PostFilter postFilter = new PostFilter();
        switch (filter) {
            case Post.NSFW_TYPE:
                postFilter.onlyNSFW = true;
                break;
            case Post.TEXT_TYPE:
                postFilter.containTextType = true;
                postFilter.containLinkType = false;
                postFilter.containImageType = false;
                postFilter.containGifType = false;
                postFilter.containVideoType = false;
                postFilter.containGalleryType = false;
                break;
            case Post.LINK_TYPE:
                postFilter.containTextType = false;
                postFilter.containLinkType = true;
                postFilter.containImageType = false;
                postFilter.containGifType = false;
                postFilter.containVideoType = false;
                postFilter.containGalleryType = false;
                break;
            case Post.IMAGE_TYPE:
                postFilter.containTextType = false;
                postFilter.containLinkType = false;
                postFilter.containImageType = true;
                postFilter.containGifType = false;
                postFilter.containVideoType = false;
                postFilter.containGalleryType = false;
                break;
            case Post.GIF_TYPE:
                postFilter.containTextType = false;
                postFilter.containLinkType = false;
                postFilter.containImageType = false;
                postFilter.containGifType = true;
                postFilter.containVideoType = false;
                postFilter.containGalleryType = false;
                break;
            case Post.VIDEO_TYPE:
                postFilter.containTextType = false;
                postFilter.containLinkType = false;
                postFilter.containImageType = false;
                postFilter.containGifType = false;
                postFilter.containVideoType = true;
                postFilter.containGalleryType = false;
                break;
            case Post.GALLERY_TYPE:
                postFilter.containTextType = false;
                postFilter.containLinkType = false;
                postFilter.containImageType = false;
                postFilter.containGifType = false;
                postFilter.containVideoType = false;
                postFilter.containGalleryType = true;
                break;
        }

        String flair = getIntent().getStringExtra(EXTRA_CONTAIN_FLAIR);
        if (flair != null) {
            postFilter.containFlairs = flair;
        }

        if (postType == PostDataSource.TYPE_USER) {
            userWhere = getIntent().getStringExtra(EXTRA_USER_WHERE);
            if (userWhere != null && !PostDataSource.USER_WHERE_SUBMITTED.equals(userWhere) && mMenu != null) {
                mMenu.findItem(R.id.action_sort_filtered_thing_activity).setVisible(false);
            }
        }

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);

        if (savedInstanceState != null) {
            isInLazyMode = savedInstanceState.getBoolean(IS_IN_LAZY_MODE_STATE);
            mFragment = (PostFragment) getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_OUT_STATE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_filtered_posts_activity, mFragment).commit();
            bindView(postFilter, false);
        } else {
            bindView(postFilter, true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mFragment != null) {
            return ((FragmentCommunicator) mFragment).handleKeyDown(keyCode) || super.onKeyDown(keyCode, event);
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
        applyAppBarLayoutAndToolbarTheme(appBarLayout, toolbar);
        applyFABTheme(fab);
    }

    private void bindView(PostFilter postFilter, boolean initializeFragment) {
        switch (postType) {
            case PostDataSource.TYPE_FRONT_PAGE:
                getSupportActionBar().setTitle(R.string.home);
                break;
            case PostDataSource.TYPE_SEARCH:
                getSupportActionBar().setTitle(R.string.search);
                break;
            case PostDataSource.TYPE_SUBREDDIT:
                if (name.equals("popular") || name.equals("all")) {
                    getSupportActionBar().setTitle(name.substring(0, 1).toUpperCase() + name.substring(1));
                } else {
                    String subredditNamePrefixed = "r/" + name;
                    getSupportActionBar().setTitle(subredditNamePrefixed);
                }
                break;
            case PostDataSource.TYPE_MULTI_REDDIT:
                String multiRedditName;
                if (name.endsWith("/")) {
                    multiRedditName = name.substring(0, name.length() - 1);
                    multiRedditName = multiRedditName.substring(multiRedditName.lastIndexOf("/") + 1);
                } else {
                    multiRedditName = name.substring(name.lastIndexOf("/") + 1);
                }
                getSupportActionBar().setTitle(multiRedditName);
                break;
            case PostDataSource.TYPE_USER:
                String usernamePrefixed = "u/" + name;
                getSupportActionBar().setTitle(usernamePrefixed);
                break;
        }

        if (initializeFragment) {
            mFragment = new PostFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(PostFragment.EXTRA_POST_TYPE, postType);
            bundle.putParcelable(PostFragment.EXTRA_FILTER, postFilter);
            bundle.putString(PostFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
            bundle.putString(PostFragment.EXTRA_ACCOUNT_NAME, mAccountName);
            if (postType == PostDataSource.TYPE_USER) {
                bundle.putString(PostFragment.EXTRA_USER_NAME, name);
                bundle.putString(PostFragment.EXTRA_USER_WHERE, userWhere);
            } else if (postType == PostDataSource.TYPE_SUBREDDIT || postType == PostDataSource.TYPE_MULTI_REDDIT) {
                bundle.putString(PostFragment.EXTRA_NAME, name);
            } else if (postType == PostDataSource.TYPE_SEARCH) {
                bundle.putString(PostFragment.EXTRA_NAME, name);
                bundle.putString(PostFragment.EXTRA_QUERY, getIntent().getStringExtra(EXTRA_QUERY));
                bundle.putString(PostFragment.EXTRA_TRENDING_SOURCE, getIntent().getStringExtra(EXTRA_TRENDING_SOURCE));
            }
            mFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_filtered_posts_activity, mFragment).commit();
        }

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, CustomizePostFilterActivity.class);
            if (mFragment != null) {
                intent.putExtra(CustomizePostFilterActivity.EXTRA_POST_FILTER, mFragment.getPostFilter());
            }
            startActivityForResult(intent, CUSTOMIZE_POST_FILTER_ACTIVITY_REQUEST_CODE);
        });

        if (mAccessToken != null) {
            fab.setOnLongClickListener(view -> {
                FilteredThingFABMoreOptionsBottomSheetFragment filteredThingFABMoreOptionsBottomSheetFragment
                        = new FilteredThingFABMoreOptionsBottomSheetFragment();
                filteredThingFABMoreOptionsBottomSheetFragment.show(getSupportFragmentManager(), filteredThingFABMoreOptionsBottomSheetFragment.getTag());
                return true;
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filtered_posts_activity, menu);
        applyMenuItemTheme(menu);
        mMenu = menu;
        MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_filtered_thing_activity);
        if (isInLazyMode) {
            lazyModeItem.setTitle(R.string.action_stop_lazy_mode);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
            collapsingToolbarLayout.setLayoutParams(params);
        } else {
            lazyModeItem.setTitle(R.string.action_start_lazy_mode);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            collapsingToolbarLayout.setLayoutParams(params);
        }

        if (userWhere != null && !PostDataSource.USER_WHERE_SUBMITTED.equals(userWhere)) {
            mMenu.findItem(R.id.action_sort_filtered_thing_activity).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sort_filtered_thing_activity:
                switch (postType) {
                    case PostDataSource.TYPE_FRONT_PAGE:
                        SortTypeBottomSheetFragment bestSortTypeBottomSheetFragment = new SortTypeBottomSheetFragment();
                        Bundle bestBundle = new Bundle();
                        bestBundle.putBoolean(SortTypeBottomSheetFragment.EXTRA_NO_BEST_TYPE, false);
                        bestSortTypeBottomSheetFragment.setArguments(bestBundle);
                        bestSortTypeBottomSheetFragment.show(getSupportFragmentManager(), bestSortTypeBottomSheetFragment.getTag());
                        break;
                    case PostDataSource.TYPE_SEARCH:
                        SearchPostSortTypeBottomSheetFragment searchPostSortTypeBottomSheetFragment = new SearchPostSortTypeBottomSheetFragment();
                        Bundle searchBundle = new Bundle();
                        searchPostSortTypeBottomSheetFragment.setArguments(searchBundle);
                        searchPostSortTypeBottomSheetFragment.show(getSupportFragmentManager(), searchPostSortTypeBottomSheetFragment.getTag());
                        break;
                    case PostDataSource.TYPE_SUBREDDIT:
                        if (name.equals("popular") || name.equals("all")) {
                            SortTypeBottomSheetFragment popularAndAllSortTypeBottomSheetFragment = new SortTypeBottomSheetFragment();
                            Bundle popularBundle = new Bundle();
                            popularBundle.putBoolean(SortTypeBottomSheetFragment.EXTRA_NO_BEST_TYPE, true);
                            popularAndAllSortTypeBottomSheetFragment.setArguments(popularBundle);
                            popularAndAllSortTypeBottomSheetFragment.show(getSupportFragmentManager(), popularAndAllSortTypeBottomSheetFragment.getTag());
                        } else {
                            SortTypeBottomSheetFragment subredditSortTypeBottomSheetFragment = new SortTypeBottomSheetFragment();
                            Bundle subredditSheetBundle = new Bundle();
                            subredditSheetBundle.putBoolean(SortTypeBottomSheetFragment.EXTRA_NO_BEST_TYPE, true);
                            subredditSortTypeBottomSheetFragment.setArguments(subredditSheetBundle);
                            subredditSortTypeBottomSheetFragment.show(getSupportFragmentManager(), subredditSortTypeBottomSheetFragment.getTag());
                        }
                        break;
                    case PostDataSource.TYPE_MULTI_REDDIT:
                        SortTypeBottomSheetFragment multiRedditSortTypeBottomSheetFragment = new SortTypeBottomSheetFragment();
                        Bundle multiRedditBundle = new Bundle();
                        multiRedditBundle.putBoolean(SortTypeBottomSheetFragment.EXTRA_NO_BEST_TYPE, true);
                        multiRedditSortTypeBottomSheetFragment.setArguments(multiRedditBundle);
                        multiRedditSortTypeBottomSheetFragment.show(getSupportFragmentManager(), multiRedditSortTypeBottomSheetFragment.getTag());
                        break;
                    case PostDataSource.TYPE_USER:
                        UserThingSortTypeBottomSheetFragment userThingSortTypeBottomSheetFragment = new UserThingSortTypeBottomSheetFragment();
                        userThingSortTypeBottomSheetFragment.show(getSupportFragmentManager(), userThingSortTypeBottomSheetFragment.getTag());
                }
                return true;
            case R.id.action_refresh_filtered_thing_activity:
                if (mMenu != null) {
                    mMenu.findItem(R.id.action_lazy_mode_filtered_thing_activity).setTitle(R.string.action_start_lazy_mode);
                }
                if (mFragment instanceof FragmentCommunicator) {
                    ((FragmentCommunicator) mFragment).refresh();
                }
                return true;
            case R.id.action_lazy_mode_filtered_thing_activity:
                MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_filtered_thing_activity);
                if (isInLazyMode) {
                    ((FragmentCommunicator) mFragment).stopLazyMode();
                    isInLazyMode = false;
                    lazyModeItem.setTitle(R.string.action_start_lazy_mode);
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    collapsingToolbarLayout.setLayoutParams(params);
                } else {
                    if (((FragmentCommunicator) mFragment).startLazyMode()) {
                        isInLazyMode = true;
                        lazyModeItem.setTitle(R.string.action_stop_lazy_mode);
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                        collapsingToolbarLayout.setLayoutParams(params);
                    }
                }
                return true;
            case R.id.action_change_post_layout_filtered_post_activity:
                PostLayoutBottomSheetFragment postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();
                postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CUSTOMIZE_POST_FILTER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (mFragment != null) {
                mFragment.changePostFilter(data.getParcelableExtra(CustomizePostFilterActivity.RETURN_EXTRA_POST_FILTER));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_OUT_STATE, mFragment);
        outState.putBoolean(IS_IN_LAZY_MODE_STATE, isInLazyMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void sortTypeSelected(SortType sortType) {
        mFragment.changeSortType(sortType);
    }

    @Override
    public void postLayoutSelected(int postLayout) {
        if (mFragment != null) {
            switch (postType) {
                case PostDataSource.TYPE_FRONT_PAGE:
                    mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_FRONT_PAGE_POST, postLayout).apply();
                    break;
                case PostDataSource.TYPE_SUBREDDIT:
                    mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_SUBREDDIT_POST_BASE + name, postLayout).apply();
                    break;
                case PostDataSource.TYPE_USER:
                    mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_USER_POST_BASE + name, postLayout).apply();
                    break;
                case PostDataSource.TYPE_SEARCH:
                    mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_SEARCH_POST, postLayout).apply();
            }
            ((FragmentCommunicator) mFragment).changePostLayout(postLayout);
        }
    }

    @Override
    public void sortTypeSelected(String sortType) {
        SortTimeBottomSheetFragment sortTimeBottomSheetFragment = new SortTimeBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SortTimeBottomSheetFragment.EXTRA_SORT_TYPE, sortType);
        sortTimeBottomSheetFragment.setArguments(bundle);
        sortTimeBottomSheetFragment.show(getSupportFragmentManager(), sortTimeBottomSheetFragment.getTag());
    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
    }

    @Override
    public void onLongPress() {
        if (mFragment != null) {
            mFragment.goBackToTop();
        }
    }

    @Override
    public void markPostAsRead(Post post) {
        InsertReadPost.insertReadPost(mRedditDataRoomDatabase, mExecutor, mAccountName, post.getId());
    }

    @Override
    public void fabOptionSelected(int option) {
        if (option == FilteredThingFABMoreOptionsBottomSheetFragment.FAB_OPTION_FILTER) {
            Intent intent = new Intent(this, CustomizePostFilterActivity.class);
            if (mFragment != null) {
                intent.putExtra(CustomizePostFilterActivity.EXTRA_POST_FILTER, mFragment.getPostFilter());
            }
            startActivityForResult(intent, CUSTOMIZE_POST_FILTER_ACTIVITY_REQUEST_CODE);
        } else if (option == FilteredThingFABMoreOptionsBottomSheetFragment.FAB_OPTION_HIDE_READ_POSTS) {
            if (mFragment != null) {
                mFragment.hideReadPosts();
            }
        }
    }

    @Override
    public void contentScrollUp() {
        fab.show();
    }

    @Override
    public void contentScrollDown() {
        fab.hide();
    }
}
