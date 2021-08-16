package com.rackluxury.rolex.reddit.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.android.material.appbar.AppBarLayout;
import com.r0adkll.slidr.Slidr;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rolex.reddit.Infinity;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;
import com.rackluxury.rolex.reddit.adapters.SearchActivityRecyclerViewAdapter;
import com.rackluxury.rolex.reddit.adapters.SubredditAutocompleteRecyclerViewAdapter;
import com.rackluxury.rolex.reddit.apis.RedditAPI;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rolex.reddit.events.SwitchAccountEvent;
import com.rackluxury.rolex.reddit.recentsearchquery.DeleteRecentSearchQuery;
import com.rackluxury.rolex.reddit.recentsearchquery.RecentSearchQuery;
import com.rackluxury.rolex.reddit.recentsearchquery.RecentSearchQueryViewModel;
import com.rackluxury.rolex.reddit.subreddit.ParseSubredditData;
import com.rackluxury.rolex.reddit.subreddit.SubredditData;
import com.rackluxury.rolex.reddit.utils.APIUtils;
import com.rackluxury.rolex.reddit.utils.SharedPreferencesUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RedditSearchActivity extends BaseActivity {

    static final String EXTRA_QUERY = "EQ";
    static final String EXTRA_SUBREDDIT_NAME = "ESN";
    static final String EXTRA_SUBREDDIT_IS_USER = "ESIU";
    static final String EXTRA_SEARCH_ONLY_SUBREDDITS = "ESOS";
    static final String EXTRA_SEARCH_ONLY_USERS = "ESOU";
    static final String EXTRA_RETURN_SUBREDDIT_NAME = "ERSN";
    static final String EXTRA_RETURN_SUBREDDIT_ICON_URL = "ERSIU";
    static final String RETURN_EXTRA_SELECTED_SUBREDDIT_NAMES = "RESSN";
    static final String RETURN_EXTRA_SELECTED_USERNAMES = "RESU";
    static final String EXTRA_RETURN_USER_NAME = "ERUN";
    static final String EXTRA_RETURN_USER_ICON_URL = "ERUIU";
    static final String EXTRA_IS_MULTI_SELECTION = "EIMS";
    static final int SUICIDE_PREVENTION_ACTIVITY_REQUEST_CODE = 101;

    private static final String SUBREDDIT_NAME_STATE = "SNS";
    private static final String SUBREDDIT_IS_USER_STATE = "SIUS";

    private static final int SUBREDDIT_SELECTION_REQUEST_CODE = 0;
    private static final int SUBREDDIT_SEARCH_REQUEST_CODE = 1;
    private static final int USER_SEARCH_REQUEST_CODE = 2;

    @BindView(R.id.coordinator_layout_search_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_search_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view_search_activity)
    SimpleSearchView simpleSearchView;
    @BindView(R.id.subreddit_name_relative_layout_search_activity)
    RelativeLayout subredditNameRelativeLayout;
    @BindView(R.id.search_in_text_view_search_activity)
    TextView searchInTextView;
    @BindView(R.id.subreddit_name_text_view_search_activity)
    TextView subredditNameTextView;
    @BindView(R.id.divider_search_activity)
    View divider;
    @BindView(R.id.recycler_view_search_activity)
    RecyclerView recyclerView;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    @Named("nsfw_and_spoiler")
    SharedPreferences mNsfwAndSpoilerSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    private String mAccountName;
    private String mAccessToken;
    private String query;
    private String subredditName;
    private boolean subredditIsUser;
    private boolean searchOnlySubreddits;
    private boolean searchOnlyUsers;
    private SearchActivityRecyclerViewAdapter adapter;
    private SubredditAutocompleteRecyclerViewAdapter subredditAutocompleteRecyclerViewAdapter;
    private Call<String> subredditAutocompleteCall;
    RecentSearchQueryViewModel mRecentSearchQueryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            Slidr.attach(this);
        }

        setSupportActionBar(toolbar);

        searchOnlySubreddits = getIntent().getBooleanExtra(EXTRA_SEARCH_ONLY_SUBREDDITS, false);
        searchOnlyUsers = getIntent().getBooleanExtra(EXTRA_SEARCH_ONLY_USERS, false);

        if (searchOnlySubreddits) {
            simpleSearchView.setHint(getText(R.string.search_only_subreddits_hint));
        } else if (searchOnlyUsers) {
            simpleSearchView.setHint(getText(R.string.search_only_users_hint));
        }

        simpleSearchView.setOnSearchViewListener(new SimpleSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                finish();
            }

            @Override
            public void onSearchViewShownAnimation() {

            }

            @Override
            public void onSearchViewClosedAnimation() {

            }
        });

        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);
        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        boolean nsfw = mNsfwAndSpoilerSharedPreferences.getBoolean((mAccountName == null ? "" : mAccountName) + SharedPreferencesUtils.NSFW_BASE, false);

        subredditAutocompleteRecyclerViewAdapter = new SubredditAutocompleteRecyclerViewAdapter(this,
                mCustomThemeWrapper, subredditData -> {
                    Intent intent = new Intent(RedditSearchActivity.this, RedditViewSubredditDetailActivity.class);
                    intent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY, subredditData.getName());
                    startActivity(intent);
                    finish();
                });

        simpleSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    if (subredditAutocompleteCall != null) {
                        subredditAutocompleteCall.cancel();
                    }
                    subredditAutocompleteCall = mOauthRetrofit.create(RedditAPI.class).subredditAutocomplete(APIUtils.getOAuthHeader(mAccessToken),
                            newText, nsfw);
                    subredditAutocompleteCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful()) {
                                ParseSubredditData.parseSubredditListingData(response.body(), nsfw, new ParseSubredditData.ParseSubredditListingDataListener() {
                                    @Override
                                    public void onParseSubredditListingDataSuccess(ArrayList<SubredditData> subredditData, String after) {
                                        subredditAutocompleteRecyclerViewAdapter.setSubreddits(subredditData);
                                        recyclerView.setAdapter(subredditAutocompleteRecyclerViewAdapter);
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
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                return false;
            }
        });

        if (savedInstanceState != null) {
            subredditName = savedInstanceState.getString(SUBREDDIT_NAME_STATE);
            subredditIsUser = savedInstanceState.getBoolean(SUBREDDIT_IS_USER_STATE);

            if (subredditName == null) {
                subredditNameTextView.setText(R.string.all_subreddits);
            } else {
                subredditNameTextView.setText(subredditName);
            }
            bindView();
        } else {
            query = getIntent().getStringExtra(EXTRA_QUERY);
            bindView();
        }

        if (searchOnlySubreddits || searchOnlyUsers) {
            subredditNameRelativeLayout.setVisibility(View.GONE);
        } else {
            subredditNameRelativeLayout.setOnClickListener(view -> {
                Intent intent = new Intent(this, RedditSubredditSelectionActivity.class);
                intent.putExtra(RedditSubredditSelectionActivity.EXTRA_EXTRA_CLEAR_SELECTION, true);
                startActivityForResult(intent, SUBREDDIT_SELECTION_REQUEST_CODE);
            });
        }

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SUBREDDIT_NAME)) {
            subredditName = intent.getStringExtra(EXTRA_SUBREDDIT_NAME);
            subredditNameTextView.setText(subredditName);
            subredditIsUser = intent.getBooleanExtra(EXTRA_SUBREDDIT_IS_USER, false);
        }
    }

    private void bindView() {
        if (mAccountName != null) {
            adapter = new SearchActivityRecyclerViewAdapter(this, mCustomThemeWrapper, new SearchActivityRecyclerViewAdapter.ItemOnClickListener() {
                @Override
                public void onClick(String query) {
                    search(query);
                }

                @Override
                public void onDelete(RecentSearchQuery recentSearchQuery) {
                    DeleteRecentSearchQuery.deleteRecentSearchQueryListener(mRedditDataRoomDatabase, recentSearchQuery, () -> {});
                }
            });
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            if (mSharedPreferences.getBoolean(SharedPreferencesUtils.ENABLE_SEARCH_HISTORY, true)) {
                mRecentSearchQueryViewModel = new ViewModelProvider(this,
                        new RecentSearchQueryViewModel.Factory(mRedditDataRoomDatabase, mAccountName))
                        .get(RecentSearchQueryViewModel.class);

                mRecentSearchQueryViewModel.getAllRecentSearchQueries().observe(this, recentSearchQueries -> {
                    if (recentSearchQueries != null && !recentSearchQueries.isEmpty()) {
                        divider.setVisibility(View.VISIBLE);
                    } else {
                        divider.setVisibility(View.GONE);
                    }
                    adapter.setRecentSearchQueries(recentSearchQueries);
                });
            }
        }
    }

    private void search(String query) {
        if (query.equalsIgnoreCase("suicide") && mSharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_SUICIDE_PREVENTION_ACTIVITY, true)) {
            Intent intent = new Intent(this, SuicidePreventionActivity.class);
            intent.putExtra(SuicidePreventionActivity.EXTRA_QUERY, query);
            startActivityForResult(intent, SUICIDE_PREVENTION_ACTIVITY_REQUEST_CODE);
        } else {
            openSearchResult(query);
        }
    }

    private void openSearchResult(String query) {
        if (searchOnlySubreddits) {
            Intent intent = new Intent(RedditSearchActivity.this, RedditSearchSubredditsResultActivity.class);
            intent.putExtra(RedditSearchSubredditsResultActivity.EXTRA_QUERY, query);
            intent.putExtra(RedditSearchSubredditsResultActivity.EXTRA_IS_MULTI_SELECTION, getIntent().getBooleanExtra(EXTRA_IS_MULTI_SELECTION, false));
            startActivityForResult(intent, SUBREDDIT_SEARCH_REQUEST_CODE);
        } else if (searchOnlyUsers) {
            Intent intent = new Intent(this, RedditSearchUsersResultActivity.class);
            intent.putExtra(RedditSearchUsersResultActivity.EXTRA_QUERY, query);
            intent.putExtra(RedditSearchUsersResultActivity.EXTRA_IS_MULTI_SELECTION, getIntent().getBooleanExtra(EXTRA_IS_MULTI_SELECTION, false));
            startActivityForResult(intent, USER_SEARCH_REQUEST_CODE);
        } else {
            Intent intent = new Intent(RedditSearchActivity.this, SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.EXTRA_QUERY, query);
            if (subredditName != null) {
                if (subredditIsUser) {
                    intent.putExtra(SearchResultActivity.EXTRA_SUBREDDIT_NAME, "u_" + subredditName);
                } else {
                    intent.putExtra(SearchResultActivity.EXTRA_SUBREDDIT_NAME, subredditName);
                }
            }
            startActivity(intent);
            finish();
        }
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
        simpleSearchView.setSearchBackground(new ColorDrawable(mCustomThemeWrapper.getColorPrimary()));
        int toolbarPrimaryTextAndIconColorColor = mCustomThemeWrapper.getToolbarPrimaryTextAndIconColor();
        simpleSearchView.setIconsColor(toolbarPrimaryTextAndIconColorColor);
        simpleSearchView.setTextColor(toolbarPrimaryTextAndIconColorColor);
        simpleSearchView.setBackIconColor(toolbarPrimaryTextAndIconColorColor);
        simpleSearchView.setHintTextColor(mCustomThemeWrapper.getToolbarPrimaryTextAndIconColor());
        int colorAccent = mCustomThemeWrapper.getColorAccent();
        searchInTextView.setTextColor(colorAccent);
        subredditNameTextView.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        divider.setBackgroundColor(mCustomThemeWrapper.getDividerColor());
    }

    @Override
    protected void onStart() {
        super.onStart();
        simpleSearchView.showSearch(false);
        simpleSearchView.getSearchEditText().requestFocus();

        if (query != null) {
            simpleSearchView.getSearchEditText().setText(query);
            simpleSearchView.getSearchEditText().setSelection(query.length());
            query = null;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(simpleSearchView.getSearchEditText().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (simpleSearchView.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == SUBREDDIT_SELECTION_REQUEST_CODE) {
                subredditName = data.getStringExtra(RedditSubredditSelectionActivity.EXTRA_RETURN_SUBREDDIT_NAME);
                subredditIsUser = data.getBooleanExtra(RedditSubredditSelectionActivity.EXTRA_RETURN_SUBREDDIT_IS_USER, false);

                if (subredditName == null) {
                    subredditNameTextView.setText(R.string.all_subreddits);
                } else {
                    subredditNameTextView.setText(subredditName);
                }
            } else if (requestCode == SUBREDDIT_SEARCH_REQUEST_CODE) {
                Intent returnIntent = new Intent();
                if (getIntent().getBooleanExtra(EXTRA_IS_MULTI_SELECTION, false)) {
                    returnIntent.putStringArrayListExtra(RETURN_EXTRA_SELECTED_SUBREDDIT_NAMES, data.getStringArrayListExtra(RedditSearchSubredditsResultActivity.RETURN_EXTRA_SELECTED_SUBREDDIT_NAMES));
                } else {
                    String name = data.getStringExtra(RedditSearchSubredditsResultActivity.EXTRA_RETURN_SUBREDDIT_NAME);
                    String iconUrl = data.getStringExtra(RedditSearchSubredditsResultActivity.EXTRA_RETURN_SUBREDDIT_ICON_URL);
                    returnIntent.putExtra(EXTRA_RETURN_SUBREDDIT_NAME, name);
                    returnIntent.putExtra(EXTRA_RETURN_SUBREDDIT_ICON_URL, iconUrl);
                }
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else if (requestCode == USER_SEARCH_REQUEST_CODE) {
                Intent returnIntent = new Intent();
                if (getIntent().getBooleanExtra(EXTRA_IS_MULTI_SELECTION, false)) {
                    returnIntent.putStringArrayListExtra(RETURN_EXTRA_SELECTED_USERNAMES, data.getStringArrayListExtra(RedditSearchUsersResultActivity.RETURN_EXTRA_SELECTED_USERNAMES));
                } else {
                    String username = data.getStringExtra(RedditSearchUsersResultActivity.EXTRA_RETURN_USER_NAME);
                    String iconUrl = data.getStringExtra(RedditSearchUsersResultActivity.EXTRA_RETURN_USER_ICON_URL);
                    returnIntent.putExtra(EXTRA_RETURN_USER_NAME, username);
                    returnIntent.putExtra(EXTRA_RETURN_USER_ICON_URL, iconUrl);
                }
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else if (requestCode == SUICIDE_PREVENTION_ACTIVITY_REQUEST_CODE) {
                openSearchResult(data.getStringExtra(SuicidePreventionActivity.EXTRA_RETURN_QUERY));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_activity, menu);
        simpleSearchView.setMenuItem(menu.findItem(R.id.action_search_search_activity));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SUBREDDIT_NAME_STATE, subredditName);
        outState.putBoolean(SUBREDDIT_IS_USER_STATE, subredditIsUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
    }
}
