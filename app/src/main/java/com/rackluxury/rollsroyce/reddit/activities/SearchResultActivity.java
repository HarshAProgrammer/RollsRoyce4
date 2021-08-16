package com.rackluxury.rollsroyce.reddit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.reddit.ActivityToolbarInterface;
import com.rackluxury.rollsroyce.reddit.FragmentCommunicator;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RecyclerViewContentScrollingInterface;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.SortType;
import com.rackluxury.rollsroyce.reddit.SortTypeSelectionCallback;
import com.rackluxury.rollsroyce.reddit.adapters.SubredditAutocompleteRecyclerViewAdapter;
import com.rackluxury.rollsroyce.reddit.apis.RedditAPI;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.FABMoreOptionsBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.PostLayoutBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.PostTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.RandomBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.SearchPostSortTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.SearchUserAndSubredditSortTypeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.SortTimeBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.events.ChangeNSFWEvent;
import com.rackluxury.rollsroyce.reddit.events.SwitchAccountEvent;
import com.rackluxury.rollsroyce.reddit.fragments.PostFragment;
import com.rackluxury.rollsroyce.reddit.fragments.SubredditListingFragment;
import com.rackluxury.rollsroyce.reddit.fragments.UserListingFragment;
import com.rackluxury.rollsroyce.reddit.post.PostDataSource;
import com.rackluxury.rollsroyce.reddit.recentsearchquery.InsertRecentSearchQuery;
import com.rackluxury.rollsroyce.reddit.subreddit.ParseSubredditData;
import com.rackluxury.rollsroyce.reddit.subreddit.SubredditData;
import com.rackluxury.rollsroyce.reddit.utils.APIUtils;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.rollsroyce.reddit.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchResultActivity extends BaseActivity implements SortTypeSelectionCallback,
        PostLayoutBottomSheetFragment.PostLayoutSelectionCallback, ActivityToolbarInterface,
        FABMoreOptionsBottomSheetFragment.FABOptionSelectionCallback, RandomBottomSheetFragment.RandomOptionSelectionCallback,
        PostTypeBottomSheetFragment.PostTypeSelectionCallback, RecyclerViewContentScrollingInterface {

    static final String EXTRA_QUERY = "EQ";
    static final String EXTRA_TRENDING_SOURCE = "ETS";
    static final String EXTRA_SUBREDDIT_NAME = "ESN";

    private static final String INSERT_SEARCH_QUERY_SUCCESS_STATE = "ISQSS";
    @BindView(R.id.coordinator_layout_search_result_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_search_result_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_search_result_activity)
    Toolbar toolbar;
    @BindView(R.id.tab_layout_search_result_activity)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_search_result_activity)
    ViewPager2 viewPager2;
    @BindView(R.id.fab_search_result_activity)
    FloatingActionButton fab;
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
    @Named("bottom_app_bar")
    SharedPreferences bottomAppBarSharedPreference;
    @Inject
    @Named("nsfw_and_spoiler")
    SharedPreferences mNsfwAndSpoilerSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    private Call<String> subredditAutocompleteCall;
    private String mAccessToken;
    private String mAccountName;
    private String mQuery;
    private String mSubredditName;
    private boolean mInsertSearchQuerySuccess;
    private FragmentManager fragmentManager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private SlidrInterface mSlidrInterface;
    private int fabOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            mSlidrInterface = Slidr.attach(this);
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

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        String query = intent.getStringExtra(EXTRA_QUERY);

        mSubredditName = intent.getStringExtra(EXTRA_SUBREDDIT_NAME);

        if (query != null) {
            mQuery = query;
            setTitle(query);
        }

        fragmentManager = getSupportFragmentManager();

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);

        if (savedInstanceState != null) {
            mInsertSearchQuerySuccess = savedInstanceState.getBoolean(INSERT_SEARCH_QUERY_SUCCESS_STATE);
        }
        bindView(savedInstanceState);
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
        applyAppBarLayoutAndToolbarTheme(appBarLayout, toolbar);
        applyTabLayoutTheme(tabLayout);
        applyFABTheme(fab);
    }

    private void bindView(Bundle savedInstanceState) {
        sectionsPagerAdapter = new SectionsPagerAdapter(this);
        viewPager2.setAdapter(sectionsPagerAdapter);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setUserInputEnabled(!mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_SWIPING_BETWEEN_TABS, false));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                fab.show();
                sectionsPagerAdapter.displaySortTypeInToolbar();
                if (position == 0) {
                    unlockSwipeRightToGoBack();
                } else {
                    lockSwipeRightToGoBack();
                }
            }
        });
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.posts);
                    break;
                case 1:
                    tab.setText(R.string.subreddits);
                    break;
                case 2:
                    tab.setText(R.string.users);
                    break;
            }
        }).attach();
        fixViewPager2Sensitivity(viewPager2);

        if (savedInstanceState == null) {
            viewPager2.setCurrentItem(Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.DEFAULT_SEARCH_RESULT_TAB, "0")), false);
        }

        fabOption = bottomAppBarSharedPreference.getInt(SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB, SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_SUBMIT_POSTS);
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
                    fabOption = SharedPreferencesUtils.MAIN_ACTIVITY_BOTTOM_APP_BAR_FAB_FILTER_POSTS;
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
                    fabOption = SharedPreferencesUtils.MAIN_ACTIVITY_BOTTOM_APP_BAR_FAB_FILTER_POSTS;
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
                    SearchPostSortTypeBottomSheetFragment searchPostSortTypeBottomSheetFragment = new SearchPostSortTypeBottomSheetFragment();
                    searchPostSortTypeBottomSheetFragment.show(getSupportFragmentManager(), searchPostSortTypeBottomSheetFragment.getTag());
                    break;
                }
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_CHANGE_POST_LAYOUT: {
                    PostLayoutBottomSheetFragment postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();
                    postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
                    break;
                }
                case SharedPreferencesUtils.OTHER_ACTIVITIES_BOTTOM_APP_BAR_FAB_SEARCH: {
                    Intent intent = new Intent(this, RedditSearchActivity.class);
                    if (mSubredditName != null && !mSubredditName.equals("")) {
                        intent.putExtra(RedditSearchActivity.EXTRA_SUBREDDIT_NAME, mSubredditName);
                    }
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

        if (mAccountName != null && mSharedPreferences.getBoolean(SharedPreferencesUtils.ENABLE_SEARCH_HISTORY, true) && !mInsertSearchQuerySuccess && mQuery != null) {
            InsertRecentSearchQuery.insertRecentSearchQueryListener(mRedditDataRoomDatabase, mAccountName,
                    mQuery, () -> mInsertSearchQuerySuccess = true);
        }
    }

    private void displaySortTypeBottomSheetFragment() {
        switch (viewPager2.getCurrentItem()) {
            case 0: {
                SearchPostSortTypeBottomSheetFragment searchPostSortTypeBottomSheetFragment = new SearchPostSortTypeBottomSheetFragment();
                searchPostSortTypeBottomSheetFragment.show(getSupportFragmentManager(), searchPostSortTypeBottomSheetFragment.getTag());
                break;
            }
            case 1:
            case 2:
                SearchUserAndSubredditSortTypeBottomSheetFragment searchUserAndSubredditSortTypeBottomSheetFragment
                        = new SearchUserAndSubredditSortTypeBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(SearchUserAndSubredditSortTypeBottomSheetFragment.EXTRA_FRAGMENT_POSITION, viewPager2.getCurrentItem());
                searchUserAndSubredditSortTypeBottomSheetFragment.setArguments(bundle);
                searchUserAndSubredditSortTypeBottomSheetFragment.show(getSupportFragmentManager(), searchUserAndSubredditSortTypeBottomSheetFragment.getTag());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_activity, menu);
        applyMenuItemTheme(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_sort_search_result_activity:
                displaySortTypeBottomSheetFragment();
                return true;
            case R.id.action_search_search_result_activity:
                Intent intent = new Intent(this, RedditSearchActivity.class);
                if (mSubredditName != null && !mSubredditName.equals("")) {
                    intent.putExtra(RedditSearchActivity.EXTRA_SUBREDDIT_NAME, mSubredditName);
                }
                intent.putExtra(RedditSearchActivity.EXTRA_QUERY, mQuery);
                finish();
                startActivity(intent);
                return true;
            case R.id.action_refresh_search_result_activity:
                if (sectionsPagerAdapter != null) {
                    sectionsPagerAdapter.refresh();
                }
                return true;
            case R.id.action_change_post_layout_search_result_activity:
                PostLayoutBottomSheetFragment postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();
                postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(INSERT_SEARCH_QUERY_SUCCESS_STATE, mInsertSearchQuerySuccess);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void sortTypeSelected(SortType sortType) {
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.changeSortType(sortType);
        }

    }

    @Override
    public void sortTypeSelected(String sortType) {
        Bundle bundle = new Bundle();
        bundle.putString(SortTimeBottomSheetFragment.EXTRA_SORT_TYPE, sortType);
        SortTimeBottomSheetFragment sortTimeBottomSheetFragment = new SortTimeBottomSheetFragment();
        sortTimeBottomSheetFragment.setArguments(bundle);
        sortTimeBottomSheetFragment.show(getSupportFragmentManager(), sortTimeBottomSheetFragment.getTag());
    }

    @Override
    public void searchUserAndSubredditSortTypeSelected(SortType sortType, int fragmentPosition) {
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.changeSortType(sortType, fragmentPosition);
        }

    }

    @Override
    public void postLayoutSelected(int postLayout) {
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.changePostLayout(postLayout);
        }

    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
    }

    @Subscribe
    public void onChangeNSFWEvent(ChangeNSFWEvent changeNSFWEvent) {
        if (sectionsPagerAdapter != null) {
            sectionsPagerAdapter.changeNSFW(changeNSFWEvent.nsfw);
        }
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
                displaySortTypeBottomSheetFragment();
                break;
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_CHANGE_POST_LAYOUT:
                PostLayoutBottomSheetFragment postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();
                postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());
                break;
            case FABMoreOptionsBottomSheetFragment.FAB_OPTION_SEARCH:
                Intent intent = new Intent(this, RedditSearchActivity.class);
                if (mSubredditName != null && !mSubredditName.equals("")) {
                    intent.putExtra(RedditSearchActivity.EXTRA_SUBREDDIT_NAME, mSubredditName);
                }
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
            Intent intent = new Intent(SearchResultActivity.this, RedditViewSubredditDetailActivity.class);
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

    @Override
    public void contentScrollUp() {
        fab.show();
    }

    @Override
    public void contentScrollDown() {
        fab.hide();
    }

    @Override
    public void randomOptionSelected(int option) {
        Intent intent = new Intent(this, FetchRandomSubredditOrPostActivity.class);
        intent.putExtra(FetchRandomSubredditOrPostActivity.EXTRA_RANDOM_OPTION, option);
        startActivity(intent);
    }

    private class SectionsPagerAdapter extends FragmentStateAdapter {

        public SectionsPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: {
                    PostFragment mFragment = new PostFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(PostFragment.EXTRA_POST_TYPE, PostDataSource.TYPE_SEARCH);
                    bundle.putString(PostFragment.EXTRA_NAME, mSubredditName);
                    bundle.putString(PostFragment.EXTRA_QUERY, mQuery);
                    bundle.putString(PostFragment.EXTRA_TRENDING_SOURCE, getIntent().getStringExtra(EXTRA_TRENDING_SOURCE));
                    bundle.putString(PostFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                    bundle.putString(PostFragment.EXTRA_ACCOUNT_NAME, mAccountName);
                    mFragment.setArguments(bundle);
                    return mFragment;
                }
                case 1: {
                    SubredditListingFragment mFragment = new SubredditListingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(SubredditListingFragment.EXTRA_QUERY, mQuery);
                    bundle.putBoolean(SubredditListingFragment.EXTRA_IS_GETTING_SUBREDDIT_INFO, false);
                    bundle.putString(SubredditListingFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                    bundle.putString(SubredditListingFragment.EXTRA_ACCOUNT_NAME, mAccountName);
                    mFragment.setArguments(bundle);
                    return mFragment;
                }
                default: {
                    UserListingFragment mFragment = new UserListingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(UserListingFragment.EXTRA_QUERY, mQuery);
                    bundle.putBoolean(UserListingFragment.EXTRA_IS_GETTING_USER_INFO, false);
                    bundle.putString(UserListingFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                    bundle.putString(UserListingFragment.EXTRA_ACCOUNT_NAME, mAccountName);
                    mFragment.setArguments(bundle);
                    return mFragment;
                }
            }
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

        void changeSortType(SortType sortType) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).changeSortType(sortType);
                displaySortTypeInToolbar();
            }
        }

        void changeSortType(SortType sortType, int fragmentPosition) {
            Fragment fragment = fragmentManager.findFragmentByTag("f" + fragmentPosition);
            if (fragment instanceof SubredditListingFragment) {
                ((SubredditListingFragment) fragment).changeSortType(sortType);
            } else if (fragment instanceof UserListingFragment) {
                ((UserListingFragment) fragment).changeSortType(sortType);
            }
            displaySortTypeInToolbar();
        }

        public void refresh() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof FragmentCommunicator) {
                ((FragmentCommunicator) fragment).refresh();
            }
        }

        void changeNSFW(boolean nsfw) {
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
            } else if (fragment instanceof SubredditListingFragment) {
                ((SubredditListingFragment) fragment).goBackToTop();
            } else if (fragment instanceof UserListingFragment) {
                ((UserListingFragment) fragment).goBackToTop();
            }
        }

        void displaySortTypeInToolbar() {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof PostFragment) {
                SortType sortType = ((PostFragment) fragment).getSortType();
                Utils.displaySortTypeInToolbar(sortType, toolbar);
            } else if (fragment instanceof SubredditListingFragment) {
                SortType sortType = ((SubredditListingFragment) fragment).getSortType();
                Utils.displaySortTypeInToolbar(sortType, toolbar);
            } else if (fragment instanceof UserListingFragment) {
                SortType sortType = ((UserListingFragment) fragment).getSortType();
                Utils.displaySortTypeInToolbar(sortType, toolbar);
            }
        }

        void filterPosts() {
            Fragment fragment = fragmentManager.findFragmentByTag("f0");
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).filterPosts();
            }
        }

        void hideReadPosts() {
            Fragment fragment = fragmentManager.findFragmentByTag("f0");
            if (fragment instanceof PostFragment) {
                ((PostFragment) fragment).hideReadPosts();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
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
