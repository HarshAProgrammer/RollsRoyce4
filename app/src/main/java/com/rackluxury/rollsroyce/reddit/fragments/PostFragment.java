package com.rackluxury.rollsroyce.reddit.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import im.ene.toro.exoplayer.ExoCreator;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.media.VolumeInfo;
import com.rackluxury.rollsroyce.reddit.ActivityToolbarInterface;
import com.rackluxury.rollsroyce.reddit.FetchPostFilterReadPostsAndConcatenatedSubredditNames;
import com.rackluxury.rollsroyce.reddit.FragmentCommunicator;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.reddit.NetworkState;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RecyclerViewContentScrollingInterface;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.SortType;
import com.rackluxury.rollsroyce.reddit.activities.BaseActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditFilteredPostsActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewSubredditDetailActivity;
import com.rackluxury.rollsroyce.reddit.adapters.PostRecyclerViewAdapter;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.customviews.CustomToroContainer;
import com.rackluxury.rollsroyce.reddit.events.ChangeAutoplayNsfwVideosEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeCompactLayoutToolbarHiddenByDefaultEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeDataSavingModeEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeDefaultLinkPostLayoutEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeDefaultPostLayoutEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeDisableImagePreviewEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeEnableSwipeActionSwitchEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeHidePostFlairEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeHidePostTypeEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeHideSubredditAndUserPrefixEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeHideTheNumberOfAwardsEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeHideTheNumberOfCommentsEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeHideTheNumberOfVotesEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeLongPressToHideToolbarInCompactLayoutEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeMuteAutoplayingVideosEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeMuteNSFWVideoEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeNSFWBlurEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeNetworkStatusEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeOnlyDisablePreviewInVideoAndGifPostsEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangePostLayoutEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeRememberMutingOptionInPostFeedEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeSavePostFeedScrolledPositionEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeShowAbsoluteNumberOfVotesEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeShowElapsedTimeEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeSpoilerBlurEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeStartAutoplayVisibleAreaOffsetEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeSwipeActionEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeSwipeActionThresholdEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeTimeFormatEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeVibrateWhenActionTriggeredEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeVideoAutoplayEvent;
import com.rackluxury.rollsroyce.reddit.events.ChangeVoteButtonsPositionEvent;
import com.rackluxury.rollsroyce.reddit.events.NeedForPostListFromPostFragmentEvent;
import com.rackluxury.rollsroyce.reddit.events.PostUpdateEventToPostList;
import com.rackluxury.rollsroyce.reddit.events.ProvidePostListToViewPostDetailActivityEvent;
import com.rackluxury.rollsroyce.reddit.events.ShowDividerInCompactLayoutPreferenceEvent;
import com.rackluxury.rollsroyce.reddit.events.ShowThumbnailOnTheRightInCompactLayoutEvent;
import com.rackluxury.rollsroyce.reddit.post.Post;
import com.rackluxury.rollsroyce.reddit.post.PostDataSource;
import com.rackluxury.rollsroyce.reddit.post.PostViewModel;
import com.rackluxury.rollsroyce.reddit.postfilter.PostFilter;
import com.rackluxury.rollsroyce.reddit.postfilter.PostFilterUsage;
import com.rackluxury.rollsroyce.reddit.readpost.ReadPost;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.rollsroyce.reddit.utils.Utils;
import retrofit2.Retrofit;

import static im.ene.toro.media.PlaybackInfo.INDEX_UNSET;
import static im.ene.toro.media.PlaybackInfo.TIME_UNSET;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment implements FragmentCommunicator {

    public static final String EXTRA_NAME = "EN";
    public static final String EXTRA_USER_NAME = "EUN";
    public static final String EXTRA_USER_WHERE = "EUW";
    public static final String EXTRA_QUERY = "EQ";
    public static final String EXTRA_TRENDING_SOURCE = "ETS";
    public static final String EXTRA_POST_TYPE = "EPT";
    public static final String EXTRA_FILTER = "EF";
    public static final String EXTRA_ACCESS_TOKEN = "EAT";
    public static final String EXTRA_ACCOUNT_NAME = "EAN";
    public static final String EXTRA_DISABLE_READ_POSTS = "EDRP";

    private static final String IS_IN_LAZY_MODE_STATE = "IILMS";
    private static final String RECYCLER_VIEW_POSITION_STATE = "RVPS";
    private static final String READ_POST_LIST_STATE = "RPLS";
    private static final String HIDE_READ_POSTS_INDEX_STATE = "HRPIS";
    private static final String POST_FILTER_STATE = "PFS";
    private static final String CONCATENATED_SUBREDDIT_NAMES_STATE = "CSNS";
    private static final String POST_FRAGMENT_ID_STATE = "PFIS";

    @BindView(R.id.swipe_refresh_layout_post_fragment)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_post_fragment)
    CustomToroContainer mPostRecyclerView;
    @BindView(R.id.fetch_post_info_linear_layout_post_fragment)
    LinearLayout mFetchPostInfoLinearLayout;
    @BindView(R.id.fetch_post_info_image_view_post_fragment)
    ImageView mFetchPostInfoImageView;
    @BindView(R.id.fetch_post_info_text_view_post_fragment)
    TextView mFetchPostInfoTextView;
    PostViewModel mPostViewModel;
    @Inject
    @Named("no_oauth")
    Retrofit mRetrofit;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    @Named("gfycat")
    Retrofit mGfycatRetrofit;
    @Inject
    @Named("redgifs")
    Retrofit mRedgifsRetrofit;
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
    @Named("post_history")
    SharedPreferences mPostHistorySharedPreferences;
    @Inject
    @Named("post_feed_scrolled_position_cache")
    SharedPreferences mPostFeedScrolledPositionSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    ExoCreator mExoCreator;
    @Inject
    Executor mExecutor;
    private RequestManager mGlide;
    private AppCompatActivity activity;
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private long postFragmentId;
    private int postType;
    private boolean isInLazyMode = false;
    private boolean isLazyModePaused = false;
    private boolean hasPost = false;
    private boolean isShown = false;
    private boolean savePostFeedScrolledPosition;
    private boolean rememberMutingOptionInPostFeed;
    private Boolean masterMutingOption;
    private PostRecyclerViewAdapter mAdapter;
    private RecyclerView.SmoothScroller smoothScroller;
    private Window window;
    private Handler lazyModeHandler;
    private LazyModeRunnable lazyModeRunnable;
    private CountDownTimer resumeLazyModeCountDownTimer;
    private float lazyModeInterval;
    private String accountName;
    private String subredditName;
    private String username;
    private String query;
    private String trendingSource;
    private String where;
    private String multiRedditPath;
    private String concatenatedSubredditNames;
    private int maxPosition = -1;
    private int postLayout;
    private SortType sortType;
    private PostFilter postFilter;
    private ColorDrawable backgroundSwipeRight;
    private ColorDrawable backgroundSwipeLeft;
    private Drawable drawableSwipeRight;
    private Drawable drawableSwipeLeft;
    private int swipeLeftAction;
    private int swipeRightAction;
    private boolean vibrateWhenActionTriggered;
    private float swipeActionThreshold;
    private ItemTouchHelper touchHelper;
    private ArrayList<ReadPost> readPosts;
    private Unbinder unbinder;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        isShown = true;
        if (mPostRecyclerView.getAdapter() != null) {
            ((PostRecyclerViewAdapter) mPostRecyclerView.getAdapter()).setCanStartActivity(true);
        }
        if (isInLazyMode) {
            resumeLazyMode(false);
        }
        if (mAdapter != null && mPostRecyclerView != null) {
            mPostRecyclerView.onWindowVisibilityChanged(View.VISIBLE);
        }
    }

    private boolean scrollPostsByCount(int count) {
        if (mLinearLayoutManager != null) {
            int pos = mLinearLayoutManager.findFirstVisibleItemPosition();
            int targetPosition = pos + count;
            mLinearLayoutManager.scrollToPositionWithOffset(targetPosition, 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleKeyDown(int keyCode) {
        boolean volumeKeysNavigatePosts = mSharedPreferences.getBoolean(SharedPreferencesUtils.VOLUME_KEYS_NAVIGATE_POSTS, false);
        if (volumeKeysNavigatePosts) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    return scrollPostsByCount(-1);
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    return scrollPostsByCount(1);
            }
        }
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        unbinder = ButterKnife.bind(this, rootView);

        EventBus.getDefault().register(this);

        applyTheme();

        lazyModeHandler = new Handler();

        lazyModeInterval = Float.parseFloat(mSharedPreferences.getString(SharedPreferencesUtils.LAZY_MODE_INTERVAL_KEY, "2.5"));

        smoothScroller = new LinearSmoothScroller(activity) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        window = activity.getWindow();

        Resources resources = getResources();

        if ((activity instanceof BaseActivity && ((BaseActivity) activity).isImmersiveInterface())) {
            mPostRecyclerView.setPadding(0, 0, 0, ((BaseActivity) activity).getNavBarHeight());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && mSharedPreferences.getBoolean(SharedPreferencesUtils.IMMERSIVE_INTERFACE_KEY, true)) {
            int navBarResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (navBarResourceId > 0) {
                mPostRecyclerView.setPadding(0, 0, 0, resources.getDimensionPixelSize(navBarResourceId));
            }
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int windowWidth = displayMetrics.widthPixels;

        mGlide = Glide.with(activity);

        lazyModeRunnable = new LazyModeRunnable() {

            @Override
            public void run() {
                if (isInLazyMode && !isLazyModePaused && mAdapter != null) {
                    int nPosts = mAdapter.getItemCount();
                    if (getCurrentPosition() == -1) {
                        if (mLinearLayoutManager != null) {
                            setCurrentPosition(mAdapter.getNextItemPositionWithoutBeingHidden(mLinearLayoutManager.findFirstVisibleItemPosition()));
                        } else {
                            int[] into = new int[2];
                            setCurrentPosition(mAdapter.getNextItemPositionWithoutBeingHidden(mStaggeredGridLayoutManager.findFirstVisibleItemPositions(into)[1]));
                        }
                    }

                    if (getCurrentPosition() != RecyclerView.NO_POSITION && nPosts > getCurrentPosition()) {
                        incrementCurrentPosition();
                        smoothScroller.setTargetPosition(mAdapter.getNextItemPositionWithoutBeingHidden(getCurrentPosition()));
                        if (mLinearLayoutManager != null) {
                            mLinearLayoutManager.startSmoothScroll(smoothScroller);
                        } else {
                            mStaggeredGridLayoutManager.startSmoothScroll(smoothScroller);
                        }
                    }
                }
                lazyModeHandler.postDelayed(this, (long) (lazyModeInterval * 1000));
            }
        };

        resumeLazyModeCountDownTimer = new CountDownTimer((long) (lazyModeInterval * 1000), (long) (lazyModeInterval * 1000)) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                resumeLazyMode(true);
            }
        };

        mSwipeRefreshLayout.setEnabled(mSharedPreferences.getBoolean(SharedPreferencesUtils.PULL_TO_REFRESH, true));
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);

        int recyclerViewPosition = 0;
        int hideReadPostsIndex = 0;
        if (savedInstanceState != null) {
            recyclerViewPosition = savedInstanceState.getInt(RECYCLER_VIEW_POSITION_STATE);

            isInLazyMode = savedInstanceState.getBoolean(IS_IN_LAZY_MODE_STATE);
            readPosts = savedInstanceState.getParcelableArrayList(READ_POST_LIST_STATE);
            hideReadPostsIndex = savedInstanceState.getInt(HIDE_READ_POSTS_INDEX_STATE, 0);
            postFilter = savedInstanceState.getParcelable(POST_FILTER_STATE);
            concatenatedSubredditNames = savedInstanceState.getString(CONCATENATED_SUBREDDIT_NAMES_STATE);
            postFragmentId = savedInstanceState.getLong(POST_FRAGMENT_ID_STATE);
        } else {
            postFilter = getArguments().getParcelable(EXTRA_FILTER);
            postFragmentId = System.currentTimeMillis() + new Random().nextInt(1000);
        }

        mPostRecyclerView.setOnTouchListener((view, motionEvent) -> {
            if (isInLazyMode) {
                pauseLazyMode(true);
            }
            return false;
        });

        if (activity instanceof RecyclerViewContentScrollingInterface) {
            mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        ((RecyclerViewContentScrollingInterface) activity).contentScrollDown();
                    } else if (dy < 0) {
                        ((RecyclerViewContentScrollingInterface) activity).contentScrollUp();
                    }
                }
            });
        }

        postType = getArguments().getInt(EXTRA_POST_TYPE);

        String accessToken = getArguments().getString(EXTRA_ACCESS_TOKEN);
        accountName = getArguments().getString(EXTRA_ACCOUNT_NAME);
        int defaultPostLayout = Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.DEFAULT_POST_LAYOUT_KEY, "0"));
        savePostFeedScrolledPosition = mSharedPreferences.getBoolean(SharedPreferencesUtils.SAVE_FRONT_PAGE_SCROLLED_POSITION, false);
        rememberMutingOptionInPostFeed = mSharedPreferences.getBoolean(SharedPreferencesUtils.REMEMBER_MUTING_OPTION_IN_POST_FEED, false);
        Locale locale = getResources().getConfiguration().locale;

        int usage;
        String nameOfUsage;

        if (postType == PostDataSource.TYPE_SEARCH) {
            subredditName = getArguments().getString(EXTRA_NAME);
            query = getArguments().getString(EXTRA_QUERY);
            trendingSource = getArguments().getString(EXTRA_TRENDING_SOURCE);
            if (savedInstanceState == null) {
                postFragmentId += query.hashCode();
            }

            usage = PostFilterUsage.SEARCH_TYPE;
            nameOfUsage = PostFilterUsage.NO_USAGE;

            String sort = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TYPE_SEARCH_POST, SortType.Type.RELEVANCE.name());
            String sortTime = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TIME_SEARCH_POST, SortType.Time.ALL.name());
            sortType = new SortType(SortType.Type.valueOf(sort), SortType.Time.valueOf(sortTime));
            postLayout = mPostLayoutSharedPreferences.getInt(SharedPreferencesUtils.POST_LAYOUT_SEARCH_POST, defaultPostLayout);

            mAdapter = new PostRecyclerViewAdapter(activity, this, mExecutor, mOauthRetrofit, mRetrofit, mGfycatRetrofit,
                    mRedgifsRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, locale,
                    windowWidth, accessToken, accountName, postType, postLayout, true,
                    mSharedPreferences, mNsfwAndSpoilerSharedPreferences, mPostHistorySharedPreferences,
                    mExoCreator, new PostRecyclerViewAdapter.Callback() {
                @Override
                public void retryLoadingMore() {
                    mPostViewModel.retryLoadingMore();
                }

                @Override
                public void typeChipClicked(int filter) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_QUERY, query);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_TRENDING_SOURCE, trendingSource);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, filter);
                    startActivity(intent);
                }

                @Override
                public void flairChipClicked(String flair) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_QUERY, query);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_TRENDING_SOURCE, trendingSource);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_CONTAIN_FLAIR, flair);
                    startActivity(intent);
                }

                @Override
                public void nsfwChipClicked() {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_QUERY, query);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_TRENDING_SOURCE, trendingSource);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, Post.NSFW_TYPE);
                    startActivity(intent);
                }

                @Override
                public void currentlyBindItem(int position) {
                    if (maxPosition < position) {
                        maxPosition = position;
                    }
                }

                @Override
                public void delayTransition() {
                    TransitionManager.beginDelayedTransition(mPostRecyclerView, new AutoTransition());
                }
            });
        } else if (postType == PostDataSource.TYPE_SUBREDDIT) {
            subredditName = getArguments().getString(EXTRA_NAME);
            if (savedInstanceState == null) {
                postFragmentId += subredditName.hashCode();
            }

            usage = PostFilterUsage.SUBREDDIT_TYPE;
            nameOfUsage = subredditName;

            String sort;
            String sortTime = null;

            sort = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TYPE_SUBREDDIT_POST_BASE + subredditName,
                    mSharedPreferences.getString(SharedPreferencesUtils.SUBREDDIT_DEFAULT_SORT_TYPE, SortType.Type.HOT.name()));
            if (sort.equals(SortType.Type.CONTROVERSIAL.name()) || sort.equals(SortType.Type.TOP.name())) {
                sortTime = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TIME_SUBREDDIT_POST_BASE + subredditName,
                        mSharedPreferences.getString(SharedPreferencesUtils.SUBREDDIT_DEFAULT_SORT_TIME, SortType.Time.ALL.name()));
            }
            boolean displaySubredditName = subredditName != null && (subredditName.equals("popular") || subredditName.equals("all"));
            postLayout = mPostLayoutSharedPreferences.getInt(SharedPreferencesUtils.POST_LAYOUT_SUBREDDIT_POST_BASE + subredditName, defaultPostLayout);

            if (sortTime != null) {
                sortType = new SortType(SortType.Type.valueOf(sort), SortType.Time.valueOf(sortTime));
            } else {
                sortType = new SortType(SortType.Type.valueOf(sort));
            }

            mAdapter = new PostRecyclerViewAdapter(activity, this, mExecutor, mOauthRetrofit, mRetrofit, mGfycatRetrofit,
                    mRedgifsRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, locale,
                    windowWidth, accessToken, accountName, postType, postLayout, displaySubredditName,
                    mSharedPreferences, mNsfwAndSpoilerSharedPreferences, mPostHistorySharedPreferences,
                    mExoCreator, new PostRecyclerViewAdapter.Callback() {
                @Override
                public void retryLoadingMore() {
                    mPostViewModel.retryLoadingMore();
                }

                @Override
                public void typeChipClicked(int filter) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, filter);
                    startActivity(intent);
                }

                @Override
                public void flairChipClicked(String flair) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_CONTAIN_FLAIR, flair);
                    startActivity(intent);
                }

                @Override
                public void nsfwChipClicked() {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, Post.NSFW_TYPE);
                    startActivity(intent);
                }

                @Override
                public void currentlyBindItem(int position) {
                    if (maxPosition < position) {
                        maxPosition = position;
                    }
                }

                @Override
                public void delayTransition() {
                    TransitionManager.beginDelayedTransition(mPostRecyclerView, new AutoTransition());
                }
            });
        } else if (postType == PostDataSource.TYPE_MULTI_REDDIT) {
            multiRedditPath = getArguments().getString(EXTRA_NAME);
            if (savedInstanceState == null) {
                postFragmentId += multiRedditPath.hashCode();
            }

            usage = PostFilterUsage.MULTIREDDIT_TYPE;
            nameOfUsage = multiRedditPath;

            String sort;
            String sortTime = null;

            sort = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TYPE_MULTI_REDDIT_POST_BASE + multiRedditPath,
                    SortType.Type.HOT.name());
            if (sort.equals(SortType.Type.CONTROVERSIAL.name()) || sort.equals(SortType.Type.TOP.name())) {
                sortTime = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TIME_MULTI_REDDIT_POST_BASE + multiRedditPath,
                        SortType.Time.ALL.name());
            }
            postLayout = mPostLayoutSharedPreferences.getInt(SharedPreferencesUtils.POST_LAYOUT_MULTI_REDDIT_POST_BASE + multiRedditPath,
                    defaultPostLayout);

            if (sortTime != null) {
                sortType = new SortType(SortType.Type.valueOf(sort), SortType.Time.valueOf(sortTime));
            } else {
                sortType = new SortType(SortType.Type.valueOf(sort));
            }

            mAdapter = new PostRecyclerViewAdapter(activity, this, mExecutor, mOauthRetrofit, mRetrofit, mGfycatRetrofit,
                    mRedgifsRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, locale,
                    windowWidth, accessToken, accountName, postType, postLayout, true,
                    mSharedPreferences, mNsfwAndSpoilerSharedPreferences, mPostHistorySharedPreferences,
                    mExoCreator, new PostRecyclerViewAdapter.Callback() {
                @Override
                public void retryLoadingMore() {
                    mPostViewModel.retryLoadingMore();
                }

                @Override
                public void typeChipClicked(int filter) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, multiRedditPath);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, filter);
                    startActivity(intent);
                }

                @Override
                public void flairChipClicked(String flair) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, multiRedditPath);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_CONTAIN_FLAIR, flair);
                    startActivity(intent);
                }

                @Override
                public void nsfwChipClicked() {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, multiRedditPath);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, Post.NSFW_TYPE);
                    startActivity(intent);
                }

                @Override
                public void currentlyBindItem(int position) {
                    if (maxPosition < position) {
                        maxPosition = position;
                    }
                }

                @Override
                public void delayTransition() {
                    TransitionManager.beginDelayedTransition(mPostRecyclerView, new AutoTransition());
                }
            });
        } else if (postType == PostDataSource.TYPE_USER) {
            username = getArguments().getString(EXTRA_USER_NAME);
            where = getArguments().getString(EXTRA_USER_WHERE);
            if (savedInstanceState == null) {
                postFragmentId += username.hashCode();
            }

            usage = PostFilterUsage.USER_TYPE;
            nameOfUsage = username;

            String sort = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TYPE_USER_POST_BASE + username,
                    mSharedPreferences.getString(SharedPreferencesUtils.USER_DEFAULT_SORT_TYPE, SortType.Type.NEW.name()));
            if (sort.equals(SortType.Type.CONTROVERSIAL.name()) || sort.equals(SortType.Type.TOP.name())) {
                String sortTime = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TIME_USER_POST_BASE + username,
                        mSharedPreferences.getString(SharedPreferencesUtils.USER_DEFAULT_SORT_TIME, SortType.Time.ALL.name()));
                sortType = new SortType(SortType.Type.valueOf(sort), SortType.Time.valueOf(sortTime));
            } else {
                sortType = new SortType(SortType.Type.valueOf(sort));
            }
            postLayout = mPostLayoutSharedPreferences.getInt(SharedPreferencesUtils.POST_LAYOUT_USER_POST_BASE + username, defaultPostLayout);

            mAdapter = new PostRecyclerViewAdapter(activity, this, mExecutor, mOauthRetrofit, mRetrofit, mGfycatRetrofit,
                    mRedgifsRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, locale,
                    windowWidth, accessToken, accountName, postType, postLayout, true,
                    mSharedPreferences, mNsfwAndSpoilerSharedPreferences, mPostHistorySharedPreferences,
                    mExoCreator, new PostRecyclerViewAdapter.Callback() {
                @Override
                public void retryLoadingMore() {
                    mPostViewModel.retryLoadingMore();
                }

                @Override
                public void typeChipClicked(int filter) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, username);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_USER_WHERE, where);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, filter);
                    startActivity(intent);
                }

                @Override
                public void flairChipClicked(String flair) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, username);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_USER_WHERE, where);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_CONTAIN_FLAIR, flair);
                    startActivity(intent);
                }

                @Override
                public void nsfwChipClicked() {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, username);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_USER_WHERE, where);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, Post.NSFW_TYPE);
                    startActivity(intent);
                }

                @Override
                public void currentlyBindItem(int position) {
                    if (maxPosition < position) {
                        maxPosition = position;
                    }
                }

                @Override
                public void delayTransition() {
                    TransitionManager.beginDelayedTransition(mPostRecyclerView, new AutoTransition());
                }
            });
        } else if (postType == PostDataSource.TYPE_ANONYMOUS_FRONT_PAGE) {
            usage = PostFilterUsage.HOME_TYPE;
            nameOfUsage = PostFilterUsage.NO_USAGE;

            String sort = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TYPE_BEST_POST, SortType.Type.BEST.name());
            if (sort.equals(SortType.Type.CONTROVERSIAL.name()) || sort.equals(SortType.Type.TOP.name())) {
                String sortTime = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TIME_BEST_POST, SortType.Time.ALL.name());
                sortType = new SortType(SortType.Type.valueOf(sort), SortType.Time.valueOf(sortTime));
            } else {
                sortType = new SortType(SortType.Type.valueOf(sort));
            }

            postLayout = mPostLayoutSharedPreferences.getInt(SharedPreferencesUtils.POST_LAYOUT_FRONT_PAGE_POST, defaultPostLayout);

            mAdapter = new PostRecyclerViewAdapter(activity, this, mExecutor, mOauthRetrofit, mRetrofit, mGfycatRetrofit,
                    mRedgifsRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, locale,
                    windowWidth, accessToken, accountName, postType, postLayout, true,
                    mSharedPreferences, mNsfwAndSpoilerSharedPreferences, mPostHistorySharedPreferences,
                    mExoCreator, new PostRecyclerViewAdapter.Callback() {
                @Override
                public void retryLoadingMore() {
                    mPostViewModel.retryLoadingMore();
                }

                @Override
                public void typeChipClicked(int filter) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, filter);
                    startActivity(intent);
                }

                @Override
                public void flairChipClicked(String flair) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_CONTAIN_FLAIR, flair);
                    startActivity(intent);
                }

                @Override
                public void nsfwChipClicked() {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, Post.NSFW_TYPE);
                    startActivity(intent);
                }

                @Override
                public void currentlyBindItem(int position) {
                    if (maxPosition < position) {
                        maxPosition = position;
                    }
                }

                @Override
                public void delayTransition() {
                    TransitionManager.beginDelayedTransition(mPostRecyclerView, new AutoTransition());
                }
            });
        } else {
            usage = PostFilterUsage.HOME_TYPE;
            nameOfUsage = PostFilterUsage.NO_USAGE;

            String sort = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TYPE_BEST_POST, SortType.Type.BEST.name());
            if (sort.equals(SortType.Type.CONTROVERSIAL.name()) || sort.equals(SortType.Type.TOP.name())) {
                String sortTime = mSortTypeSharedPreferences.getString(SharedPreferencesUtils.SORT_TIME_BEST_POST, SortType.Time.ALL.name());
                sortType = new SortType(SortType.Type.valueOf(sort), SortType.Time.valueOf(sortTime));
            } else {
                sortType = new SortType(SortType.Type.valueOf(sort));
            }
            postLayout = mPostLayoutSharedPreferences.getInt(SharedPreferencesUtils.POST_LAYOUT_FRONT_PAGE_POST, defaultPostLayout);

            mAdapter = new PostRecyclerViewAdapter(activity, this, mExecutor, mOauthRetrofit, mRetrofit, mGfycatRetrofit,
                    mRedgifsRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, locale,
                    windowWidth, accessToken, accountName, postType, postLayout, true,
                    mSharedPreferences, mNsfwAndSpoilerSharedPreferences, mPostHistorySharedPreferences,
                    mExoCreator, new PostRecyclerViewAdapter.Callback() {
                @Override
                public void retryLoadingMore() {
                    mPostViewModel.retryLoadingMore();
                }

                @Override
                public void typeChipClicked(int filter) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, filter);
                    startActivity(intent);
                }

                @Override
                public void flairChipClicked(String flair) {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_CONTAIN_FLAIR, flair);
                    startActivity(intent);
                }

                @Override
                public void nsfwChipClicked() {
                    Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
                    intent.putExtra(RedditFilteredPostsActivity.EXTRA_FILTER, Post.NSFW_TYPE);
                    startActivity(intent);
                }

                @Override
                public void currentlyBindItem(int position) {
                    if (maxPosition < position) {
                        maxPosition = position;
                    }
                }

                @Override
                public void delayTransition() {
                    TransitionManager.beginDelayedTransition(mPostRecyclerView, new AutoTransition());
                }
            });
        }

        int nColumns = getNColumns(resources);
        if (nColumns == 1) {
            mLinearLayoutManager = new LinearLayoutManager(activity);
            mPostRecyclerView.setLayoutManager(mLinearLayoutManager);
        } else {
            mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(nColumns, StaggeredGridLayoutManager.VERTICAL);
            mPostRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
            StaggeredGridLayoutManagerItemOffsetDecoration itemDecoration =
                    new StaggeredGridLayoutManagerItemOffsetDecoration(activity, R.dimen.staggeredLayoutManagerItemOffset, nColumns);
            mPostRecyclerView.addItemDecoration(itemDecoration);
            windowWidth /= 2;
        }

        if (recyclerViewPosition > 0) {
            mPostRecyclerView.scrollToPosition(recyclerViewPosition);
        }

        mAdapter.setHideReadPostsIndex(hideReadPostsIndex);

        if (activity instanceof ActivityToolbarInterface) {
            ((ActivityToolbarInterface) activity).displaySortType();
        }

        if (accessToken != null && !accessToken.equals("")) {
            if (mPostHistorySharedPreferences.getBoolean(accountName + SharedPreferencesUtils.MARK_POSTS_AS_READ_BASE, false) && readPosts == null) {
                if (getArguments().getBoolean(EXTRA_DISABLE_READ_POSTS, false)) {
                    if (postFilter == null) {
                        FetchPostFilterReadPostsAndConcatenatedSubredditNames.fetchPostFilterAndReadPosts(mRedditDataRoomDatabase, mExecutor,
                                new Handler(), null, usage, nameOfUsage, (postFilter, readPostList) -> {
                                    if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                        this.postFilter = postFilter;
                                        postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean(accountName + SharedPreferencesUtils.NSFW_BASE, false);
                                        initializeAndBindPostViewModel(accessToken);
                                    }
                                });
                    } else {
                        initializeAndBindPostViewModel(accessToken);
                    }
                } else {
                    FetchPostFilterReadPostsAndConcatenatedSubredditNames.fetchPostFilterAndReadPosts(mRedditDataRoomDatabase, mExecutor,
                            new Handler(), accountName, usage, nameOfUsage, (postFilter, readPostList) -> {
                                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                    if (this.postFilter == null) {
                                        this.postFilter = postFilter;
                                        postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean(accountName + SharedPreferencesUtils.NSFW_BASE, false);
                                    }
                                    this.readPosts = readPostList;
                                    initializeAndBindPostViewModel(accessToken);
                                }
                            });
                }
            } else {
                if (postFilter == null) {
                    FetchPostFilterReadPostsAndConcatenatedSubredditNames.fetchPostFilterAndReadPosts(mRedditDataRoomDatabase, mExecutor,
                            new Handler(), null, usage, nameOfUsage, (postFilter, readPostList) -> {
                                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                    this.postFilter = postFilter;
                                    postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean(accountName + SharedPreferencesUtils.NSFW_BASE, false);
                                    initializeAndBindPostViewModel(accessToken);
                                }
                            });
                } else {
                    initializeAndBindPostViewModel(accessToken);
                }
            }
        } else {
            if (postFilter == null) {
                if (postType == PostDataSource.TYPE_ANONYMOUS_FRONT_PAGE) {
                    if (concatenatedSubredditNames == null) {
                        FetchPostFilterReadPostsAndConcatenatedSubredditNames.fetchPostFilterAndConcatenatedSubredditNames(mRedditDataRoomDatabase, mExecutor, new Handler(), usage, nameOfUsage,
                                (postFilter, concatenatedSubredditNames) -> {
                                    if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                        this.postFilter = postFilter;
                                        postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean(SharedPreferencesUtils.NSFW_BASE, false);
                                        this.concatenatedSubredditNames = concatenatedSubredditNames;
                                        if (concatenatedSubredditNames == null) {
                                            showErrorView(R.string.anonymous_front_page_no_subscriptions);
                                        } else {
                                            initializeAndBindPostViewModelForAnonymous(concatenatedSubredditNames);
                                        }
                                    }
                                });
                    } else {
                        initializeAndBindPostViewModelForAnonymous(concatenatedSubredditNames);
                    }
                } else {
                    FetchPostFilterReadPostsAndConcatenatedSubredditNames.fetchPostFilterAndReadPosts(mRedditDataRoomDatabase, mExecutor,
                            new Handler(), null, usage, nameOfUsage, (postFilter, readPostList) -> {
                                if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                    this.postFilter = postFilter;
                                    postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean(SharedPreferencesUtils.NSFW_BASE, false);
                                    initializeAndBindPostViewModelForAnonymous(null);
                                }
                            });
                }
            } else {
                if (postType == PostDataSource.TYPE_ANONYMOUS_FRONT_PAGE) {
                    if (concatenatedSubredditNames == null) {
                        FetchPostFilterReadPostsAndConcatenatedSubredditNames.fetchPostFilterAndConcatenatedSubredditNames(mRedditDataRoomDatabase, mExecutor, new Handler(), usage, nameOfUsage,
                                (postFilter, concatenatedSubredditNames) -> {
                                    if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                                        this.postFilter = postFilter;
                                        postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean(SharedPreferencesUtils.NSFW_BASE, false);
                                        this.concatenatedSubredditNames = concatenatedSubredditNames;
                                        if (concatenatedSubredditNames == null) {
                                            showErrorView(R.string.anonymous_front_page_no_subscriptions);
                                        } else {
                                            initializeAndBindPostViewModelForAnonymous(concatenatedSubredditNames);
                                        }
                                    }
                                });
                    } else {
                        initializeAndBindPostViewModelForAnonymous(concatenatedSubredditNames);
                    }
                } else {
                    initializeAndBindPostViewModelForAnonymous(null);
                }
            }
        }

        vibrateWhenActionTriggered = mSharedPreferences.getBoolean(SharedPreferencesUtils.VIBRATE_WHEN_ACTION_TRIGGERED, true);
        swipeActionThreshold = Float.parseFloat(mSharedPreferences.getString(SharedPreferencesUtils.SWIPE_ACTION_THRESHOLD, "0.3"));
        swipeRightAction = Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.SWIPE_RIGHT_ACTION, "1"));
        swipeLeftAction = Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.SWIPE_LEFT_ACTION, "0"));
        initializeSwipeActionDrawable();

        touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            boolean exceedThreshold = false;

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (!(viewHolder instanceof PostRecyclerViewAdapter.PostBaseViewHolder) &&
                        !(viewHolder instanceof PostRecyclerViewAdapter.PostCompactBaseViewHolder)) {
                    return makeMovementFlags(0, 0);
                }
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(0, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (touchHelper != null) {
                    touchHelper.attachToRecyclerView(null);
                    touchHelper.attachToRecyclerView(mPostRecyclerView);
                    if (mAdapter != null) {
                        mAdapter.onItemSwipe(viewHolder, direction, swipeLeftAction, swipeRightAction);
                    }
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                int horizontalOffset = (int) Utils.convertDpToPixel(16, activity);
                if (dX > 0) {
                    if (dX > (itemView.getRight() - itemView.getLeft()) * swipeActionThreshold) {
                        if (!exceedThreshold) {
                            exceedThreshold = true;
                            if (vibrateWhenActionTriggered) {
                                viewHolder.itemView.setHapticFeedbackEnabled(true);
                                viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            }
                        }
                        backgroundSwipeRight.setBounds(0, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else {
                        exceedThreshold = false;
                        backgroundSwipeRight.setBounds(0, 0, 0, 0);
                    }

                    drawableSwipeRight.setBounds(itemView.getLeft() + ((int) dX) - horizontalOffset - drawableSwipeRight.getIntrinsicWidth(),
                            (itemView.getBottom() + itemView.getTop() - drawableSwipeRight.getIntrinsicHeight()) / 2,
                            itemView.getLeft() + ((int) dX) - horizontalOffset,
                            (itemView.getBottom() + itemView.getTop() + drawableSwipeRight.getIntrinsicHeight()) / 2);
                    backgroundSwipeRight.draw(c);
                    drawableSwipeRight.draw(c);
                } else if (dX < 0) {
                    if (-dX > (itemView.getRight() - itemView.getLeft()) * swipeActionThreshold) {
                        if (!exceedThreshold) {
                            exceedThreshold = true;
                            if (vibrateWhenActionTriggered) {
                                viewHolder.itemView.setHapticFeedbackEnabled(true);
                                viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            }
                        }
                        backgroundSwipeLeft.setBounds(0, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else {
                        exceedThreshold = false;
                        backgroundSwipeLeft.setBounds(0, 0, 0, 0);
                    }
                    drawableSwipeLeft.setBounds(itemView.getRight() + ((int) dX) + horizontalOffset,
                            (itemView.getBottom() + itemView.getTop() - drawableSwipeLeft.getIntrinsicHeight()) / 2,
                            itemView.getRight() + ((int) dX) + horizontalOffset + drawableSwipeLeft.getIntrinsicWidth(),
                            (itemView.getBottom() + itemView.getTop() + drawableSwipeLeft.getIntrinsicHeight()) / 2);
                    backgroundSwipeLeft.draw(c);
                    drawableSwipeLeft.draw(c);
                }
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return swipeActionThreshold;
            }
        });

        if (nColumns == 1 && mSharedPreferences.getBoolean(SharedPreferencesUtils.ENABLE_SWIPE_ACTION, false)) {
            touchHelper.attachToRecyclerView(mPostRecyclerView);
        }
        mPostRecyclerView.setAdapter(mAdapter);
        mPostRecyclerView.setCacheManager(mAdapter);
        mPostRecyclerView.setPlayerInitializer(order -> {
            VolumeInfo volumeInfo = new VolumeInfo(true, 0f);
            return new PlaybackInfo(INDEX_UNSET, TIME_UNSET, volumeInfo);
        });

        return rootView;
    }

    private int getNColumns(Resources resources) {
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            switch (postLayout) {
                case SharedPreferencesUtils.POST_LAYOUT_CARD_2:
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_PORTRAIT_CARD_LAYOUT_2, "1"));
                case SharedPreferencesUtils.POST_LAYOUT_COMPACT:
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_PORTRAIT_COMPACT_LAYOUT, "1"));
                case SharedPreferencesUtils.POST_LAYOUT_GALLERY:
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_PORTRAIT_GALLERY_LAYOUT, "2"));
                default:
                    if (getResources().getBoolean(R.bool.isTabletReddit)) {
                        return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_PORTRAIT, "2"));
                    }
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_PORTRAIT, "1"));
            }
        } else {
            switch (postLayout) {
                case SharedPreferencesUtils.POST_LAYOUT_CARD_2:
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_LANDSCAPE_CARD_LAYOUT_2, "2"));
                case SharedPreferencesUtils.POST_LAYOUT_COMPACT:
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_LANDSCAPE_COMPACT_LAYOUT, "2"));
                case SharedPreferencesUtils.POST_LAYOUT_GALLERY:
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_LANDSCAPE_GALLERY_LAYOUT, "2"));
                default:
                    return Integer.parseInt(mSharedPreferences.getString(SharedPreferencesUtils.NUMBER_OF_COLUMNS_IN_POST_FEED_LANDSCAPE, "2"));
            }
        }
    }

    private void initializeAndBindPostViewModel(String accessToken) {
        if (postType == PostDataSource.TYPE_SEARCH) {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), accessToken == null ? mRetrofit : mOauthRetrofit, accessToken,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, subredditName, query, trendingSource,
                    postType, sortType, postFilter, readPosts)).get(PostViewModel.class);
        } else if (postType == PostDataSource.TYPE_SUBREDDIT) {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), accessToken == null ? mRetrofit : mOauthRetrofit, accessToken,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, subredditName, postType, sortType,
                    postFilter, readPosts)).get(PostViewModel.class);
        } else if (postType == PostDataSource.TYPE_MULTI_REDDIT) {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), accessToken == null ? mRetrofit : mOauthRetrofit, accessToken,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, multiRedditPath, postType, sortType,
                    postFilter, readPosts)).get(PostViewModel.class);
        } else if (postType == PostDataSource.TYPE_USER) {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), accessToken == null ? mRetrofit : mOauthRetrofit, accessToken,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, username, postType, sortType, postFilter,
                    where, readPosts)).get(PostViewModel.class);
        } else {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), mOauthRetrofit, accessToken,
                    accountName, mSharedPreferences, mPostFeedScrolledPositionSharedPreferences,
                    postType, sortType, postFilter, readPosts)).get(PostViewModel.class);
        }

        bindPostViewModel();
    }

    private void initializeAndBindPostViewModelForAnonymous(String concatenatedSubredditNames) {
        //For anonymous user
        if (postType == PostDataSource.TYPE_SEARCH) {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), mRetrofit, null,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, subredditName, query, trendingSource,
                    postType, sortType, postFilter, readPosts)).get(PostViewModel.class);
        } else if (postType == PostDataSource.TYPE_SUBREDDIT) {
            mPostViewModel = new ViewModelProvider(this, new PostViewModel.Factory(mExecutor,
                    new Handler(), mRetrofit, null,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, subredditName, postType, sortType,
                    postFilter, readPosts)).get(PostViewModel.class);
        } else if (postType == PostDataSource.TYPE_MULTI_REDDIT) {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), mRetrofit, null,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, multiRedditPath, postType, sortType, postFilter,
                    readPosts)).get(PostViewModel.class);
        } else if (postType == PostDataSource.TYPE_USER) {
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), mRetrofit, null,
                    accountName, mSharedPreferences,
                    mPostFeedScrolledPositionSharedPreferences, username, postType, sortType, postFilter,
                    where, readPosts)).get(PostViewModel.class);
        } else {
            //Anonymous Front Page
            mPostViewModel = new ViewModelProvider(PostFragment.this, new PostViewModel.Factory(mExecutor,
                    new Handler(), mRetrofit,
                    mSharedPreferences, concatenatedSubredditNames, postType, sortType, postFilter)).get(PostViewModel.class);
        }

        bindPostViewModel();
    }

    private void bindPostViewModel() {
        mPostViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> mAdapter.submitList(posts));

        mPostViewModel.hasPost().observe(getViewLifecycleOwner(), hasPost -> {
            this.hasPost = hasPost;
            mSwipeRefreshLayout.setRefreshing(false);
            if (hasPost) {
                mFetchPostInfoLinearLayout.setVisibility(View.GONE);
            } else {
                if (isInLazyMode) {
                    stopLazyMode();
                }

                mFetchPostInfoLinearLayout.setOnClickListener(null);
                showErrorView(R.string.no_posts);
            }
        });

        mPostViewModel.getInitialLoadingState().observe(getViewLifecycleOwner(), networkState -> {
            if (networkState.getStatus().equals(NetworkState.Status.SUCCESS)) {
                mSwipeRefreshLayout.setRefreshing(false);
            } else if (networkState.getStatus().equals(NetworkState.Status.FAILED)) {
                mSwipeRefreshLayout.setRefreshing(false);
                mFetchPostInfoLinearLayout.setOnClickListener(view -> refresh());
                showErrorView(R.string.load_posts_error);
            } else {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mPostViewModel.getPaginationNetworkState().observe(getViewLifecycleOwner(), networkState -> mAdapter.setNetworkState(networkState));
    }

    public void changeSortType(SortType sortType) {
        if (mPostViewModel != null) {
            if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SAVE_SORT_TYPE, true)) {
                switch (postType) {
                    case PostDataSource.TYPE_FRONT_PAGE:
                        mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TYPE_BEST_POST, sortType.getType().name()).apply();
                        if (sortType.getTime() != null) {
                            mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TIME_BEST_POST, sortType.getTime().name()).apply();
                        }
                        break;
                    case PostDataSource.TYPE_SUBREDDIT:
                        mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TYPE_SUBREDDIT_POST_BASE + subredditName, sortType.getType().name()).apply();
                        if (sortType.getTime() != null) {
                            mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TIME_SUBREDDIT_POST_BASE + subredditName, sortType.getTime().name()).apply();
                        }
                        break;
                    case PostDataSource.TYPE_USER:
                        mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TYPE_USER_POST_BASE + username, sortType.getType().name()).apply();
                        if (sortType.getTime() != null) {
                            mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TIME_USER_POST_BASE + username, sortType.getTime().name()).apply();
                        }
                        break;
                    case PostDataSource.TYPE_SEARCH:
                        mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TYPE_SEARCH_POST, sortType.getType().name()).apply();
                        if (sortType.getTime() != null) {
                            mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TIME_SEARCH_POST, sortType.getTime().name()).apply();
                        }
                        break;
                    case PostDataSource.TYPE_MULTI_REDDIT:
                        mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TYPE_MULTI_REDDIT_POST_BASE + multiRedditPath,
                                sortType.getType().name()).apply();
                        if (sortType.getTime() != null) {
                            mSortTypeSharedPreferences.edit().putString(SharedPreferencesUtils.SORT_TIME_MULTI_REDDIT_POST_BASE + multiRedditPath,
                                    sortType.getTime().name()).apply();
                        }
                        break;
                }
            }
            if (mFetchPostInfoLinearLayout.getVisibility() != View.GONE) {
                mFetchPostInfoLinearLayout.setVisibility(View.GONE);
                mGlide.clear(mFetchPostInfoImageView);
            }
            mAdapter.removeFooter();
            hasPost = false;
            if (isInLazyMode) {
                stopLazyMode();
            }
            this.sortType = sortType;
            mPostViewModel.changeSortType(sortType);
        }
    }

    private void initializeSwipeActionDrawable() {
        if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
            backgroundSwipeRight = new ColorDrawable(mCustomThemeWrapper.getDownvoted());
            drawableSwipeRight = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.ic_arrow_downward_black_24dp, null);
        } else {
            backgroundSwipeRight = new ColorDrawable(mCustomThemeWrapper.getUpvoted());
            drawableSwipeRight = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.ic_arrow_upward_black_24dp, null);
        }

        if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
            backgroundSwipeLeft = new ColorDrawable(mCustomThemeWrapper.getUpvoted());
            drawableSwipeLeft = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.ic_arrow_upward_black_24dp, null);
        } else {
            backgroundSwipeLeft = new ColorDrawable(mCustomThemeWrapper.getDownvoted());
            drawableSwipeLeft = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.ic_arrow_downward_black_24dp, null);
        }
    }

    public long getPostFragmentId() {
        return postFragmentId;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_IN_LAZY_MODE_STATE, isInLazyMode);
        outState.putParcelableArrayList(READ_POST_LIST_STATE, readPosts);
        if (mAdapter != null) {
            outState.putInt(HIDE_READ_POSTS_INDEX_STATE, mAdapter.getHideReadPostsIndex());
        }
        if (mLinearLayoutManager != null) {
            outState.putInt(RECYCLER_VIEW_POSITION_STATE, mLinearLayoutManager.findFirstVisibleItemPosition());
        } else if (mStaggeredGridLayoutManager != null) {
            int[] into = new int[mStaggeredGridLayoutManager.getSpanCount()];
            outState.putInt(RECYCLER_VIEW_POSITION_STATE,
                    mStaggeredGridLayoutManager.findFirstVisibleItemPositions(into)[0]);
        }
        outState.putParcelable(POST_FILTER_STATE, postFilter);
        outState.putString(CONCATENATED_SUBREDDIT_NAMES_STATE, concatenatedSubredditNames);
        outState.putLong(POST_FRAGMENT_ID_STATE, postFragmentId);
    }

    @Override
    public void onStop() {
        super.onStop();
        saveCache();
    }

    private void saveCache() {
        if (savePostFeedScrolledPosition && postType == PostDataSource.TYPE_FRONT_PAGE && sortType != null && sortType.getType() == SortType.Type.BEST && mAdapter != null) {
            Post currentPost = mAdapter.getItemByPosition(maxPosition);
            if (currentPost != null) {
                String accountNameForCache = accountName == null ? SharedPreferencesUtils.FRONT_PAGE_SCROLLED_POSITION_ANONYMOUS : accountName;
                String key = accountNameForCache + SharedPreferencesUtils.FRONT_PAGE_SCROLLED_POSITION_FRONT_PAGE_BASE;
                String value = currentPost.getFullName();
                mPostFeedScrolledPositionSharedPreferences.edit().putString(key, value).apply();
            }
        }
    }

    @Override
    public void refresh() {
        if (mPostViewModel != null) {
            mAdapter.removeFooter();
            mFetchPostInfoLinearLayout.setVisibility(View.GONE);
            hasPost = false;
            if (isInLazyMode) {
                stopLazyMode();
            }
            saveCache();
            mPostViewModel.refresh();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showErrorView(int stringResId) {
        if (activity != null && isAdded()) {
            mSwipeRefreshLayout.setRefreshing(false);
            mFetchPostInfoLinearLayout.setVisibility(View.VISIBLE);
            mFetchPostInfoTextView.setText(stringResId);
            mGlide.load(R.drawable.error_image).into(mFetchPostInfoImageView);
        }
    }

    @Override
    public void changeNSFW(boolean nsfw) {
        postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && nsfw;
        if (mPostViewModel != null) {
            mPostViewModel.changePostFilter(postFilter);
        }
    }

    @Override
    public boolean startLazyMode() {
        if (!hasPost) {
            Toast.makeText(activity, R.string.no_posts_no_lazy_mode, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mAdapter != null && mAdapter.isAutoplay()) {
            mAdapter.setAutoplay(false);
            refreshAdapter();
        }

        isInLazyMode = true;
        isLazyModePaused = false;

        lazyModeInterval = Float.parseFloat(mSharedPreferences.getString(SharedPreferencesUtils.LAZY_MODE_INTERVAL_KEY, "2.5"));
        lazyModeHandler.postDelayed(lazyModeRunnable, (long) (lazyModeInterval * 1000));
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toast.makeText(activity, getString(R.string.lazy_mode_start, lazyModeInterval),
                Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public void stopLazyMode() {
        if (mAdapter != null) {
            String autoplayString = mSharedPreferences.getString(SharedPreferencesUtils.VIDEO_AUTOPLAY, SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_NEVER);
            if (autoplayString.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ALWAYS_ON) ||
                    (autoplayString.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ON_WIFI) && Utils.isConnectedToWifi(activity))) {
                mAdapter.setAutoplay(true);
                refreshAdapter();
            }
        }
        isInLazyMode = false;
        isLazyModePaused = false;
        lazyModeRunnable.resetOldPosition();
        lazyModeHandler.removeCallbacks(lazyModeRunnable);
        resumeLazyModeCountDownTimer.cancel();
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toast.makeText(activity, getString(R.string.lazy_mode_stop), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resumeLazyMode(boolean resumeNow) {
        if (isInLazyMode) {
            if (mAdapter != null && mAdapter.isAutoplay()) {
                mAdapter.setAutoplay(false);
                refreshAdapter();
            }
            isLazyModePaused = false;
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            lazyModeRunnable.resetOldPosition();

            if (resumeNow) {
                lazyModeHandler.post(lazyModeRunnable);
            } else {
                lazyModeHandler.postDelayed(lazyModeRunnable, (long) (lazyModeInterval * 1000));
            }
        }
    }

    @Override
    public void pauseLazyMode(boolean startTimer) {
        resumeLazyModeCountDownTimer.cancel();
        isInLazyMode = true;
        isLazyModePaused = true;
        lazyModeHandler.removeCallbacks(lazyModeRunnable);
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (startTimer) {
            resumeLazyModeCountDownTimer.start();
        }
    }

    @Override
    public boolean isInLazyMode() {
        return isInLazyMode;
    }

    @Override
    public void changePostLayout(int postLayout) {
        this.postLayout = postLayout;
        switch (postType) {
            case PostDataSource.TYPE_FRONT_PAGE:
                mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_FRONT_PAGE_POST, postLayout).apply();
                break;
            case PostDataSource.TYPE_SUBREDDIT:
                mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_SUBREDDIT_POST_BASE + subredditName, postLayout).apply();
                break;
            case PostDataSource.TYPE_USER:
                mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_USER_POST_BASE + username, postLayout).apply();
                break;
            case PostDataSource.TYPE_SEARCH:
                mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_SEARCH_POST, postLayout).apply();
                break;
            case PostDataSource.TYPE_MULTI_REDDIT:
                mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_MULTI_REDDIT_POST_BASE + multiRedditPath, postLayout).apply();
                break;
        }

        int previousPosition = -1;
        if (mLinearLayoutManager != null) {
            previousPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if (mStaggeredGridLayoutManager != null) {
            int[] into = new int[mStaggeredGridLayoutManager.getSpanCount()];
            previousPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(into)[0];
        }
        int nColumns = getNColumns(getResources());
        if (nColumns == 1) {
            mLinearLayoutManager = new LinearLayoutManager(activity);
            if (mPostRecyclerView.getItemDecorationCount() > 0) {
                mPostRecyclerView.removeItemDecorationAt(0);
            }
            mPostRecyclerView.setLayoutManager(mLinearLayoutManager);
            mStaggeredGridLayoutManager = null;
        } else {
            mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(nColumns, StaggeredGridLayoutManager.VERTICAL);
            if (mPostRecyclerView.getItemDecorationCount() > 0) {
                mPostRecyclerView.removeItemDecorationAt(0);
            }
            mPostRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
            StaggeredGridLayoutManagerItemOffsetDecoration itemDecoration =
                    new StaggeredGridLayoutManagerItemOffsetDecoration(activity, R.dimen.staggeredLayoutManagerItemOffset, nColumns);
            mPostRecyclerView.addItemDecoration(itemDecoration);
            mLinearLayoutManager = null;
        }

        if (previousPosition > 0) {
            mPostRecyclerView.scrollToPosition(previousPosition);
        }

        if (mAdapter != null) {
            mAdapter.setPostLayout(postLayout);
            refreshAdapter();
        }
    }

    @Override
    public void applyTheme() {
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
        mSwipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        mFetchPostInfoTextView.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
    }

    @Override
    public void hideReadPosts() {
        if (mAdapter != null) {
            mAdapter.prepareToHideReadPosts();
            refreshAdapter();
        }
    }

    @Override
    public void changePostFilter(PostFilter postFilter) {
        this.postFilter = postFilter;
        postFilter.allowNSFW = !mSharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_NSFW_FOREVER, false) && mNsfwAndSpoilerSharedPreferences.getBoolean((accountName == null || accountName.equals("-") ? "" : accountName) + SharedPreferencesUtils.NSFW_BASE, false);
        if (mPostViewModel != null) {
            mPostViewModel.changePostFilter(postFilter);
        }
    }

    @Override
    public PostFilter getPostFilter() {
        return postFilter;
    }

    @Override
    public void filterPosts() {
        if (postType == PostDataSource.TYPE_SEARCH) {
            Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_QUERY, query);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_TRENDING_SOURCE, trendingSource);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
            startActivity(intent);
        } else if (postType == PostDataSource.TYPE_SUBREDDIT) {
            Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, subredditName);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
            startActivity(intent);
        } else if (postType == PostDataSource.TYPE_MULTI_REDDIT) {
            Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, multiRedditPath);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
            startActivity(intent);
        } else if (postType == PostDataSource.TYPE_USER) {
            Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, username);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_USER_WHERE, where);
            startActivity(intent);
        } else {
            Intent intent = new Intent(activity, RedditFilteredPostsActivity.class);
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_NAME, activity.getString(R.string.best));
            intent.putExtra(RedditFilteredPostsActivity.EXTRA_POST_TYPE, postType);
            startActivity(intent);
        }
    }

    public boolean getIsNsfwSubreddit() {
        if (activity instanceof RedditViewSubredditDetailActivity) {
            return ((RedditViewSubredditDetailActivity) activity).isNsfwSubreddit();
        } else {
            return false;
        }
    }

    @Nullable
    public Boolean getMasterMutingOption() {
        return masterMutingOption;
    }

    public void videoAutoplayChangeMutingOption(boolean isMute) {
        if (rememberMutingOptionInPostFeed) {
            masterMutingOption = isMute;
        }
    }

    @Subscribe
    public void onPostUpdateEvent(PostUpdateEventToPostList event) {
        PagedList<Post> posts = mAdapter.getCurrentList();
        if (posts != null && event.positionInList >= 0 && event.positionInList < posts.size()) {
            Post post = posts.get(event.positionInList);
            if (post != null && post.getFullName().equals(event.post.getFullName())) {
                post.setTitle(event.post.getTitle());
                post.setVoteType(event.post.getVoteType());
                post.setScore(event.post.getScore());
                post.setNSFW(event.post.isNSFW());
                post.setHidden(event.post.isHidden());
                post.setSpoiler(event.post.isSpoiler());
                post.setFlair(event.post.getFlair());
                post.setSaved(event.post.isSaved());
                mAdapter.notifyItemChanged(event.positionInList);
            }
        }
    }

    @Subscribe
    public void onChangeShowElapsedTimeEvent(ChangeShowElapsedTimeEvent event) {
        if (mAdapter != null) {
            mAdapter.setShowElapsedTime(event.showElapsedTime);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeTimeFormatEvent(ChangeTimeFormatEvent changeTimeFormatEvent) {
        if (mAdapter != null) {
            mAdapter.setTimeFormat(changeTimeFormatEvent.timeFormat);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeVoteButtonsPositionEvent(ChangeVoteButtonsPositionEvent event) {
        if (mAdapter != null) {
            mAdapter.setVoteButtonsPosition(event.voteButtonsOnTheRight);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeNSFWBlurEvent(ChangeNSFWBlurEvent event) {
        if (mAdapter != null) {
            mAdapter.setBlurNsfwAndDoNotBlurNsfwInNsfwSubreddits(event.needBlurNSFW, event.doNotBlurNsfwInNsfwSubreddits);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeSpoilerBlurEvent(ChangeSpoilerBlurEvent event) {
        if (mAdapter != null) {
            mAdapter.setBlurSpoiler(event.needBlurSpoiler);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangePostLayoutEvent(ChangePostLayoutEvent event) {
        changePostLayout(event.postLayout);
    }

    @Subscribe
    public void onShowDividerInCompactLayoutPreferenceEvent(ShowDividerInCompactLayoutPreferenceEvent event) {
        if (mAdapter != null) {
            mAdapter.setShowDividerInCompactLayout(event.showDividerInCompactLayout);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeDefaultPostLayoutEvent(ChangeDefaultPostLayoutEvent changeDefaultPostLayoutEvent) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            switch (postType) {
                case PostDataSource.TYPE_SUBREDDIT:
                    if (!mPostLayoutSharedPreferences.contains(SharedPreferencesUtils.POST_LAYOUT_SUBREDDIT_POST_BASE + bundle.getString(EXTRA_NAME))) {
                        changePostLayout(changeDefaultPostLayoutEvent.defaultPostLayout);
                    }
                    break;
                case PostDataSource.TYPE_USER:
                    if (!mPostLayoutSharedPreferences.contains(SharedPreferencesUtils.POST_LAYOUT_USER_POST_BASE + bundle.getString(EXTRA_USER_NAME))) {
                        changePostLayout(changeDefaultPostLayoutEvent.defaultPostLayout);
                    }
                    break;
                case PostDataSource.TYPE_MULTI_REDDIT:
                    if (!mPostLayoutSharedPreferences.contains(SharedPreferencesUtils.POST_LAYOUT_MULTI_REDDIT_POST_BASE + bundle.getString(EXTRA_NAME))) {
                        changePostLayout(changeDefaultPostLayoutEvent.defaultPostLayout);
                    }
                    break;
                case PostDataSource.TYPE_SEARCH:
                    if (!mPostLayoutSharedPreferences.contains(SharedPreferencesUtils.POST_LAYOUT_SEARCH_POST)) {
                        changePostLayout(changeDefaultPostLayoutEvent.defaultPostLayout);
                    }
                    break;
                case PostDataSource.TYPE_FRONT_PAGE:
                    if (!mPostLayoutSharedPreferences.contains(SharedPreferencesUtils.POST_LAYOUT_FRONT_PAGE_POST)) {
                        changePostLayout(changeDefaultPostLayoutEvent.defaultPostLayout);
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void onChangeDefaultLinkPostLayoutEvent(ChangeDefaultLinkPostLayoutEvent event) {
        if (mAdapter != null) {
            mAdapter.setDefaultLinkPostLayout(event.defaultLinkPostLayout);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeShowAbsoluteNumberOfVotesEvent(ChangeShowAbsoluteNumberOfVotesEvent changeShowAbsoluteNumberOfVotesEvent) {
        if (mAdapter != null) {
            mAdapter.setShowAbsoluteNumberOfVotes(changeShowAbsoluteNumberOfVotesEvent.showAbsoluteNumberOfVotes);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeVideoAutoplayEvent(ChangeVideoAutoplayEvent changeVideoAutoplayEvent) {
        if (mAdapter != null) {
            boolean autoplay = false;
            if (changeVideoAutoplayEvent.autoplay.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ALWAYS_ON)) {
                autoplay = true;
            } else if (changeVideoAutoplayEvent.autoplay.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ON_WIFI)) {
                autoplay = Utils.isConnectedToWifi(activity);
            }
            mAdapter.setAutoplay(autoplay);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeAutoplayNsfwVideosEvent(ChangeAutoplayNsfwVideosEvent changeAutoplayNsfwVideosEvent) {
        if (mAdapter != null) {
            mAdapter.setAutoplayNsfwVideos(changeAutoplayNsfwVideosEvent.autoplayNsfwVideos);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeMuteAutoplayingVideosEvent(ChangeMuteAutoplayingVideosEvent changeMuteAutoplayingVideosEvent) {
        if (mAdapter != null) {
            mAdapter.setMuteAutoplayingVideos(changeMuteAutoplayingVideosEvent.muteAutoplayingVideos);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeNetworkStatusEvent(ChangeNetworkStatusEvent changeNetworkStatusEvent) {
        if (mAdapter != null) {
            String autoplay = mSharedPreferences.getString(SharedPreferencesUtils.VIDEO_AUTOPLAY, SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_NEVER);
            String dataSavingMode = mSharedPreferences.getString(SharedPreferencesUtils.DATA_SAVING_MODE, SharedPreferencesUtils.DATA_SAVING_MODE_OFF);
            boolean stateChanged = false;
            if (autoplay.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ON_WIFI)) {
                mAdapter.setAutoplay(changeNetworkStatusEvent.connectedNetwork == Utils.NETWORK_TYPE_WIFI);
                stateChanged = true;
            }
            if (dataSavingMode.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ONLY_ON_CELLULAR_DATA)) {
                mAdapter.setDataSavingMode(changeNetworkStatusEvent.connectedNetwork == Utils.NETWORK_TYPE_CELLULAR);
                stateChanged = true;
            }

            if (stateChanged) {
                refreshAdapter();
            }
        }
    }

    @Subscribe
    public void onShowThumbnailOnTheRightInCompactLayoutEvent(ShowThumbnailOnTheRightInCompactLayoutEvent showThumbnailOnTheRightInCompactLayoutEvent) {
        if (mAdapter != null) {
            mAdapter.setShowThumbnailOnTheRightInCompactLayout(showThumbnailOnTheRightInCompactLayoutEvent.showThumbnailOnTheRightInCompactLayout);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeStartAutoplayVisibleAreaOffsetEvent(ChangeStartAutoplayVisibleAreaOffsetEvent changeStartAutoplayVisibleAreaOffsetEvent) {
        if (mAdapter != null) {
            mAdapter.setStartAutoplayVisibleAreaOffset(changeStartAutoplayVisibleAreaOffsetEvent.startAutoplayVisibleAreaOffset);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeMuteNSFWVideoEvent(ChangeMuteNSFWVideoEvent changeMuteNSFWVideoEvent) {
        if (mAdapter != null) {
            mAdapter.setMuteNSFWVideo(changeMuteNSFWVideoEvent.muteNSFWVideo);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeSavePostFeedScrolledPositionEvent(ChangeSavePostFeedScrolledPositionEvent changeSavePostFeedScrolledPositionEvent) {
        savePostFeedScrolledPosition = changeSavePostFeedScrolledPositionEvent.savePostFeedScrolledPosition;
    }

    @Subscribe
    public void onChangeVibrateWhenActionTriggeredEvent(ChangeVibrateWhenActionTriggeredEvent changeVibrateWhenActionTriggeredEvent) {
        vibrateWhenActionTriggered = changeVibrateWhenActionTriggeredEvent.vibrateWhenActionTriggered;
    }

    @Subscribe
    public void onChangeEnableSwipeActionSwitchEvent(ChangeEnableSwipeActionSwitchEvent changeEnableSwipeActionSwitchEvent) {
        if (touchHelper != null) {
            if (changeEnableSwipeActionSwitchEvent.enableSwipeAction) {
                touchHelper.attachToRecyclerView(mPostRecyclerView);
            } else {
                touchHelper.attachToRecyclerView(null);
            }
        }
    }

    @Subscribe
    public void onChangePullToRefreshEvent(ChangePullToRefreshEvent changePullToRefreshEvent) {
        mSwipeRefreshLayout.setEnabled(changePullToRefreshEvent.pullToRefresh);
    }

    @Subscribe
    public void onChangeLongPressToHideToolbarInCompactLayoutEvent(ChangeLongPressToHideToolbarInCompactLayoutEvent changeLongPressToHideToolbarInCompactLayoutEvent) {
        if (mAdapter != null) {
            mAdapter.setLongPressToHideToolbarInCompactLayout(changeLongPressToHideToolbarInCompactLayoutEvent.longPressToHideToolbarInCompactLayout);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeCompactLayoutToolbarHiddenByDefaultEvent(ChangeCompactLayoutToolbarHiddenByDefaultEvent changeCompactLayoutToolbarHiddenByDefaultEvent) {
        if (mAdapter != null) {
            mAdapter.setCompactLayoutToolbarHiddenByDefault(changeCompactLayoutToolbarHiddenByDefaultEvent.compactLayoutToolbarHiddenByDefault);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeSwipeActionThresholdEvent(ChangeSwipeActionThresholdEvent changeSwipeActionThresholdEvent) {
        swipeActionThreshold = changeSwipeActionThresholdEvent.swipeActionThreshold;
    }

    @Subscribe
    public void onChangeDataSavingModeEvent(ChangeDataSavingModeEvent changeDataSavingModeEvent) {
        if (mAdapter != null) {
            boolean dataSavingMode = false;
            if (changeDataSavingModeEvent.dataSavingMode.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ONLY_ON_CELLULAR_DATA)) {
                dataSavingMode = Utils.isConnectedToCellularData(activity);
            } else if (changeDataSavingModeEvent.dataSavingMode.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ALWAYS)) {
                dataSavingMode = true;
            }
            mAdapter.setDataSavingMode(dataSavingMode);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeDisableImagePreviewEvent(ChangeDisableImagePreviewEvent changeDisableImagePreviewEvent) {
        if (mAdapter != null) {
            mAdapter.setDisableImagePreview(changeDisableImagePreviewEvent.disableImagePreview);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeOnlyDisablePreviewInVideoAndGifPostsEvent(ChangeOnlyDisablePreviewInVideoAndGifPostsEvent changeOnlyDisablePreviewInVideoAndGifPostsEvent) {
        if (mAdapter != null) {
            mAdapter.setOnlyDisablePreviewInVideoPosts(changeOnlyDisablePreviewInVideoAndGifPostsEvent.onlyDisablePreviewInVideoAndGifPosts);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeSwipeActionEvent(ChangeSwipeActionEvent changeSwipeActionEvent) {
        swipeRightAction = changeSwipeActionEvent.swipeRightAction == -1 ? swipeRightAction : changeSwipeActionEvent.swipeRightAction;
        swipeLeftAction = changeSwipeActionEvent.swipeLeftAction == -1 ? swipeLeftAction : changeSwipeActionEvent.swipeLeftAction;
        initializeSwipeActionDrawable();
    }

    @Subscribe
    public void onNeedForPostListFromPostRecyclerViewAdapterEvent(NeedForPostListFromPostFragmentEvent event) {
        if (postFragmentId == event.postFragmentTimeId) {
            EventBus.getDefault().post(new ProvidePostListToViewPostDetailActivityEvent(postFragmentId, new ArrayList<>(mPostViewModel.getPosts().getValue())));
        }
    }

    @Subscribe
    public void onChangeHidePostTypeEvent(ChangeHidePostTypeEvent event) {
        if (mAdapter != null) {
            mAdapter.setHidePostType(event.hidePostType);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeHidePostFlairEvent(ChangeHidePostFlairEvent event) {
        if (mAdapter != null) {
            mAdapter.setHidePostFlair(event.hidePostFlair);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeHideTheNumberOfAwardsEvent(ChangeHideTheNumberOfAwardsEvent event) {
        if (mAdapter != null) {
            mAdapter.setHideTheNumberOfAwards(event.hideTheNumberOfAwards);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeHideSubredditAndUserEvent(ChangeHideSubredditAndUserPrefixEvent event) {
        if (mAdapter != null) {
            mAdapter.setHideSubredditAndUserPrefix(event.hideSubredditAndUserPrefix);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeHideTheNumberOfVotesEvent(ChangeHideTheNumberOfVotesEvent event) {
        if (mAdapter != null) {
            mAdapter.setHideTheNumberOfVotes(event.hideTheNumberOfVotes);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeHideTheNumberOfCommentsEvent(ChangeHideTheNumberOfCommentsEvent event) {
        if (mAdapter != null) {
            mAdapter.setHideTheNumberOfComments(event.hideTheNumberOfComments);
            refreshAdapter();
        }
    }

    @Subscribe
    public void onChangeRememberMutingOptionInPostFeedEvent(ChangeRememberMutingOptionInPostFeedEvent event) {
        rememberMutingOptionInPostFeed = event.rememberMutingOptionInPostFeedEvent;
        if (!event.rememberMutingOptionInPostFeedEvent) {
            masterMutingOption = null;
        }
    }

    private void refreshAdapter() {
        int previousPosition = -1;
        if (mLinearLayoutManager != null) {
            previousPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        } else if (mStaggeredGridLayoutManager != null) {
            int[] into = new int[mStaggeredGridLayoutManager.getSpanCount()];
            previousPosition = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(into)[0];
        }

        RecyclerView.LayoutManager layoutManager = mPostRecyclerView.getLayoutManager();
        mPostRecyclerView.setAdapter(null);
        mPostRecyclerView.setLayoutManager(null);
        mPostRecyclerView.setAdapter(mAdapter);
        mPostRecyclerView.setLayoutManager(layoutManager);
        if (previousPosition > 0) {
            mPostRecyclerView.scrollToPosition(previousPosition);
        }
    }

    public void goBackToTop() {
        if (mLinearLayoutManager != null) {
            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
            if (isInLazyMode) {
                lazyModeRunnable.resetOldPosition();
            }
        } else if (mStaggeredGridLayoutManager != null) {
            mStaggeredGridLayoutManager.scrollToPositionWithOffset(0, 0);
            if (isInLazyMode) {
                lazyModeRunnable.resetOldPosition();
            }
        }
    }

    public SortType getSortType() {
        return sortType;
    }

    public int getPostType() {
        return postType;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isInLazyMode) {
            pauseLazyMode(false);
        }
        if (mAdapter != null && mPostRecyclerView != null) {
            mPostRecyclerView.onWindowVisibilityChanged(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private static abstract class LazyModeRunnable implements Runnable {
        private int currentPosition = -1;

        int getCurrentPosition() {
            return currentPosition;
        }

        void setCurrentPosition(int currentPosition) {
            this.currentPosition = currentPosition;
        }

        void incrementCurrentPosition() {
            currentPosition++;
        }

        void resetOldPosition() {
            currentPosition = -1;
        }
    }

    private static class StaggeredGridLayoutManagerItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;
        private int mNColumns;

        StaggeredGridLayoutManagerItemOffsetDecoration(int itemOffset, int nColumns) {
            mItemOffset = itemOffset;
            mNColumns = nColumns;
        }

        StaggeredGridLayoutManagerItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId, int nColumns) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId), nColumns);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

            int spanIndex = layoutParams.getSpanIndex();

            int halfOffset = mItemOffset / 2;

            if (mNColumns == 2) {
                if (spanIndex == 0) {
                    outRect.set(halfOffset, 0, halfOffset / 2, 0);
                } else {
                    outRect.set(halfOffset / 2, 0, halfOffset, 0);
                }
            } else if (mNColumns == 3) {
                if (spanIndex == 0) {
                    outRect.set(halfOffset, 0, halfOffset / 2, 0);
                } else if (spanIndex == 1) {
                    outRect.set(halfOffset / 2, 0, halfOffset / 2, 0);
                } else {
                    outRect.set(halfOffset / 2, 0, halfOffset, 0);
                }
            }
        }
    }
}