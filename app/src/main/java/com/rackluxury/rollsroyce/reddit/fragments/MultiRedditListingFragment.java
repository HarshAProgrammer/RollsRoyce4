package com.rackluxury.rollsroyce.reddit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;
import com.rackluxury.rollsroyce.reddit.FragmentCommunicator;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.activities.BaseActivity;
import com.rackluxury.rollsroyce.reddit.activities.MultiredditSelectionActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSubscribedThingListingActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewMultiRedditDetailActivity;
import com.rackluxury.rollsroyce.reddit.adapters.MultiRedditListingRecyclerViewAdapter;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.MultiRedditOptionsBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.multireddit.MultiReddit;
import com.rackluxury.rollsroyce.reddit.multireddit.MultiRedditViewModel;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;
import retrofit2.Retrofit;

public class MultiRedditListingFragment extends Fragment implements FragmentCommunicator {

    public static final String EXTRA_ACCOUNT_NAME = "EAN";
    public static final String EXTRA_ACCESS_TOKEN = "EAT";
    public static final String EXTRA_IS_GETTING_MULTIREDDIT_INFO = "EIGMI";

    @BindView(R.id.swipe_refresh_layout_multi_reddit_listing_fragment)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view_multi_reddit_listing_fragment)
    RecyclerView mRecyclerView;
    @BindView(R.id.fetch_multi_reddit_listing_info_linear_layout_multi_reddit_listing_fragment)
    LinearLayout mErrorLinearLayout;
    @BindView(R.id.fetch_multi_reddit_listing_info_image_view_multi_reddit_listing_fragment)
    ImageView mErrorImageView;
    @BindView(R.id.fetch_multi_reddit_listing_info_text_view_multi_reddit_listing_fragment)
    TextView mErrorTextView;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;

    public MultiRedditViewModel mMultiRedditViewModel;
    private AppCompatActivity mActivity;
    private RequestManager mGlide;
    private LinearLayoutManager mLinearLayoutManager;

    public MultiRedditListingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multi_reddit_listing, container, false);

        ((Infinity) mActivity.getApplication()).getAppComponent().inject(this);

        ButterKnife.bind(this, rootView);

        applyTheme();

        if ((mActivity != null && ((BaseActivity) mActivity).isImmersiveInterface())) {
            mRecyclerView.setPadding(0, 0, 0, ((BaseActivity) mActivity).getNavBarHeight());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && mSharedPreferences.getBoolean(SharedPreferencesUtils.IMMERSIVE_INTERFACE_KEY, true)) {
            Resources resources = getResources();
            int navBarResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (navBarResourceId > 0) {
                mRecyclerView.setPadding(0, 0, 0, resources.getDimensionPixelSize(navBarResourceId));
            }
        }

        String accountName = getArguments().getString(EXTRA_ACCOUNT_NAME);
        String accessToken = getArguments().getString(EXTRA_ACCESS_TOKEN);
        boolean isGettingMultiredditInfo = getArguments().getBoolean(EXTRA_IS_GETTING_MULTIREDDIT_INFO, false);

        mGlide = Glide.with(this);

        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        MultiRedditListingRecyclerViewAdapter adapter = new MultiRedditListingRecyclerViewAdapter(mActivity,
                mExecutor, mOauthRetrofit, mRedditDataRoomDatabase, mCustomThemeWrapper, accessToken,
                new MultiRedditListingRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(MultiReddit multiReddit) {
                if (mActivity instanceof MultiredditSelectionActivity) {
                    ((MultiredditSelectionActivity) mActivity).getSelectedMultireddit(multiReddit);
                } else {
                    Intent intent = new Intent(mActivity, RedditViewMultiRedditDetailActivity.class);
                    intent.putExtra(RedditViewMultiRedditDetailActivity.EXTRA_MULTIREDDIT_DATA, multiReddit);
                    mActivity.startActivity(intent);
                }
            }

            @Override
            public void onLongClick(MultiReddit multiReddit) {
                if (!isGettingMultiredditInfo) {
                    showOptionsBottomSheetFragment(multiReddit);
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
        if (mActivity instanceof RedditSubscribedThingListingActivity) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        ((RedditSubscribedThingListingActivity) mActivity).hideFabInMultiredditTab();
                    } else {
                        ((RedditSubscribedThingListingActivity) mActivity).showFabInMultiredditTab();
                    }
                }
            });
        }
        new FastScrollerBuilder(mRecyclerView).build();

        mMultiRedditViewModel = new ViewModelProvider(this,
                new MultiRedditViewModel.Factory(mActivity.getApplication(), mRedditDataRoomDatabase, accountName))
                .get(MultiRedditViewModel.class);

        mMultiRedditViewModel.getAllMultiReddits().observe(getViewLifecycleOwner(), subscribedUserData -> {
            if (subscribedUserData == null || subscribedUserData.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mErrorLinearLayout.setVisibility(View.VISIBLE);
                mGlide.load(R.drawable.error_image).into(mErrorImageView);
            } else {
                mErrorLinearLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mGlide.clear(mErrorImageView);
            }
            adapter.setMultiReddits(subscribedUserData);
        });

        mMultiRedditViewModel.getAllFavoriteMultiReddits().observe(getViewLifecycleOwner(), favoriteSubscribedUserData -> {
            if (favoriteSubscribedUserData != null && favoriteSubscribedUserData.size() > 0) {
                mErrorLinearLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mGlide.clear(mErrorImageView);
            }
            adapter.setFavoriteMultiReddits(favoriteSubscribedUserData);
        });

        return rootView;
    }

    private void showOptionsBottomSheetFragment(MultiReddit multiReddit) {
        MultiRedditOptionsBottomSheetFragment fragment = new MultiRedditOptionsBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MultiRedditOptionsBottomSheetFragment.EXTRA_MULTI_REDDIT, multiReddit);
        fragment.setArguments(bundle);
        fragment.show(mActivity.getSupportFragmentManager(), fragment.getTag());
    }

    public void goBackToTop() {
        if (mLinearLayoutManager != null) {
            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public void applyTheme() {
        if (mActivity instanceof RedditSubscribedThingListingActivity) {
            mSwipeRefreshLayout.setOnRefreshListener(() -> ((RedditSubscribedThingListingActivity) mActivity).loadSubscriptions(true));
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
            mSwipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }

        mErrorTextView.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
    }

    @Override
    public void stopRefreshProgressbar() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}