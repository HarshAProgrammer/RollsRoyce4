package com.rackluxury.rollsroyce.reddit.adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.libRG.CustomTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.toro.CacheManager;
import im.ene.toro.ToroPlayer;
import im.ene.toro.ToroUtil;
import im.ene.toro.exoplayer.ExoCreator;
import im.ene.toro.exoplayer.ExoPlayerViewHelper;
import im.ene.toro.exoplayer.Playable;
import im.ene.toro.media.PlaybackInfo;
import im.ene.toro.widget.Container;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import com.rackluxury.rollsroyce.reddit.FetchGfycatOrRedgifsVideoLinks;
import com.rackluxury.rollsroyce.reddit.MarkPostAsReadInterface;
import com.rackluxury.rollsroyce.reddit.NetworkState;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.SaveThing;
import com.rackluxury.rollsroyce.reddit.VoteThing;
import com.rackluxury.rollsroyce.reddit.activities.RedditFilteredPostsActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewPostDetailActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewRedditGalleryActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewSubredditDetailActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewVideoActivity;
import com.rackluxury.rollsroyce.reddit.activities.ViewImageOrGifActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewUserDetailActivity;
import com.rackluxury.rollsroyce.reddit.asynctasks.LoadSubredditIcon;
import com.rackluxury.rollsroyce.reddit.asynctasks.LoadUserData;
import com.rackluxury.rollsroyce.reddit.bottomsheetfragments.ShareLinkBottomSheetFragment;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.customviews.AspectRatioGifImageView;
import com.rackluxury.rollsroyce.reddit.events.PostUpdateEventToPostDetailFragment;
import com.rackluxury.rollsroyce.reddit.fragments.PostFragment;
import com.rackluxury.rollsroyce.reddit.post.Post;
import com.rackluxury.rollsroyce.reddit.post.PostDataSource;
import com.rackluxury.rollsroyce.reddit.utils.APIUtils;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.rollsroyce.reddit.utils.Utils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

/**
 * Created by alex on 2/25/18.
 */

public class PostRecyclerViewAdapter extends PagedListAdapter<Post, RecyclerView.ViewHolder> implements CacheManager {
    private static final int VIEW_TYPE_POST_CARD_VIDEO_AUTOPLAY_TYPE = 1;
    private static final int VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE = 2;
    private static final int VIEW_TYPE_POST_CARD_TEXT_TYPE = 3;
    private static final int VIEW_TYPE_POST_COMPACT = 4;
    private static final int VIEW_TYPE_POST_GALLERY = 5;
    private static final int VIEW_TYPE_POST_CARD_2_VIDEO_AUTOPLAY_TYPE = 6;
    private static final int VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE = 7;
    private static final int VIEW_TYPE_POST_CARD_2_TEXT_TYPE = 8;
    private static final int VIEW_TYPE_ERROR = 9;
    private static final int VIEW_TYPE_LOADING = 10;

    private static final DiffUtil.ItemCallback<Post> DIFF_CALLBACK = new DiffUtil.ItemCallback<Post>() {
        @Override
        public boolean areItemsTheSame(@NonNull Post post, @NonNull Post t1) {
            return post.getId().equals(t1.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post post, @NonNull Post t1) {
            return false;
        }
    };

    private AppCompatActivity mActivity;
    private PostFragment mFragment;
    private SharedPreferences mSharedPreferences;
    private Executor mExecutor;
    private Retrofit mOauthRetrofit;
    private Retrofit mRetrofit;
    private Retrofit mGfycatRetrofit;
    private Retrofit mRedgifsRetrofit;
    private int mImageViewWidth;
    private String mAccessToken;
    private RequestManager mGlide;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;
    private Locale mLocale;
    private boolean canStartActivity = true;
    private int mPostType;
    private int mPostLayout;
    private int mDefaultLinkPostLayout;
    private int mColorPrimaryLightTheme;
    private int mColorAccent;
    private int mCardViewBackgroundColor;
    private int mReadPostCardViewBackgroundColor;
    private int mPrimaryTextColor;
    private int mSecondaryTextColor;
    private int mPostTitleColor;
    private int mPostContentColor;
    private int mReadPostTitleColor;
    private int mReadPostContentColor;
    private int mStickiedPostIconTint;
    private int mPostTypeBackgroundColor;
    private int mPostTypeTextColor;
    private int mSubredditColor;
    private int mUsernameColor;
    private int mSpoilerBackgroundColor;
    private int mSpoilerTextColor;
    private int mFlairBackgroundColor;
    private int mFlairTextColor;
    private int mAwardsBackgroundColor;
    private int mAwardsTextColor;
    private int mNSFWBackgroundColor;
    private int mNSFWTextColor;
    private int mArchivedIconTint;
    private int mLockedIconTint;
    private int mCrosspostIconTint;
    private int mNoPreviewPostTypeBackgroundColor;
    private int mNoPreviewPostTypeIconTint;
    private int mUpvotedColor;
    private int mDownvotedColor;
    private int mVoteAndReplyUnavailableVoteButtonColor;
    private int mButtonTextColor;
    private int mPostIconAndInfoColor;
    private int mDividerColor;
    private int mHideReadPostsIndex = 0;
    private float mScale;
    private boolean mDisplaySubredditName;
    private boolean mVoteButtonsOnTheRight;
    private boolean mNeedBlurNsfw;
    private boolean mDoNotBlurNsfwInNsfwSubreddits;
    private boolean mNeedBlurSpoiler;
    private boolean mShowElapsedTime;
    private String mTimeFormatPattern;
    private boolean mShowDividerInCompactLayout;
    private boolean mShowAbsoluteNumberOfVotes;
    private boolean mAutoplay = false;
    private boolean mAutoplayNsfwVideos;
    private boolean mMuteAutoplayingVideos;
    private boolean mShowThumbnailOnTheRightInCompactLayout;
    private double mStartAutoplayVisibleAreaOffset;
    private boolean mMuteNSFWVideo;
    private boolean mAutomaticallyTryRedgifs;
    private boolean mLongPressToHideToolbarInCompactLayout;
    private boolean mCompactLayoutToolbarHiddenByDefault;
    private boolean mDataSavingMode = false;
    private boolean mDisableImagePreview;
    private boolean mOnlyDisablePreviewInVideoAndGifPosts;
    private boolean mMarkPostsAsRead;
    private boolean mMarkPostsAsReadAfterVoting;
    private boolean mMarkPostsAsReadOnScroll;
    private boolean mHideReadPostsAutomatically;
    private boolean mHidePostType;
    private boolean mHidePostFlair;
    private boolean mHideTheNumberOfAwards;
    private boolean mHideSubredditAndUserPrefix;
    private boolean mHideTheNumberOfVotes;
    private boolean mHideTheNumberOfComments;
    private Drawable mCommentIcon;
    private NetworkState networkState;
    private ExoCreator mExoCreator;
    private Callback mCallback;

    public PostRecyclerViewAdapter(AppCompatActivity activity, PostFragment fragment, Executor executor, Retrofit oauthRetrofit, Retrofit retrofit,
                                   Retrofit gfycatRetrofit, Retrofit redgifsRetrofit,
                                   RedditDataRoomDatabase redditDataRoomDatabase,
                                   CustomThemeWrapper customThemeWrapper, Locale locale, int imageViewWidth,
                                   String accessToken, String accountName, int postType, int postLayout, boolean displaySubredditName,
                                   SharedPreferences sharedPreferences, SharedPreferences nsfwAndSpoilerSharedPreferences,
                                   SharedPreferences postHistorySharedPreferences,
                                   ExoCreator exoCreator, Callback callback) {
        super(DIFF_CALLBACK);
        if (activity != null) {
            mActivity = activity;
            mFragment = fragment;
            mSharedPreferences = sharedPreferences;
            mExecutor = executor;
            mOauthRetrofit = oauthRetrofit;
            mRetrofit = retrofit;
            mGfycatRetrofit = gfycatRetrofit;
            mRedgifsRetrofit = redgifsRetrofit;
            mImageViewWidth = imageViewWidth;
            mAccessToken = accessToken;
            mPostType = postType;
            mDisplaySubredditName = displaySubredditName;
            mNeedBlurNsfw = nsfwAndSpoilerSharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.BLUR_NSFW_BASE, true);
            mDoNotBlurNsfwInNsfwSubreddits = nsfwAndSpoilerSharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.DO_NOT_BLUR_NSFW_IN_NSFW_SUBREDDITS, false);
            mNeedBlurSpoiler = nsfwAndSpoilerSharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.BLUR_SPOILER_BASE, false);
            mVoteButtonsOnTheRight = sharedPreferences.getBoolean(SharedPreferencesUtils.VOTE_BUTTONS_ON_THE_RIGHT_KEY, false);
            mShowElapsedTime = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ELAPSED_TIME_KEY, false);
            mTimeFormatPattern = sharedPreferences.getString(SharedPreferencesUtils.TIME_FORMAT_KEY, SharedPreferencesUtils.TIME_FORMAT_DEFAULT_VALUE);
            mShowDividerInCompactLayout = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_DIVIDER_IN_COMPACT_LAYOUT, true);
            mShowAbsoluteNumberOfVotes = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ABSOLUTE_NUMBER_OF_VOTES, true);
            String autoplayString = sharedPreferences.getString(SharedPreferencesUtils.VIDEO_AUTOPLAY, SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_NEVER);
            int networkType = Utils.getConnectedNetwork(activity);
            if (autoplayString.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ALWAYS_ON)) {
                mAutoplay = true;
            } else if (autoplayString.equals(SharedPreferencesUtils.VIDEO_AUTOPLAY_VALUE_ON_WIFI)) {
                mAutoplay = networkType == Utils.NETWORK_TYPE_WIFI;
            }
            mAutoplayNsfwVideos = sharedPreferences.getBoolean(SharedPreferencesUtils.AUTOPLAY_NSFW_VIDEOS, true);
            mMuteAutoplayingVideos = sharedPreferences.getBoolean(SharedPreferencesUtils.MUTE_AUTOPLAYING_VIDEOS, true);
            mShowThumbnailOnTheRightInCompactLayout = sharedPreferences.getBoolean(
                    SharedPreferencesUtils.SHOW_THUMBNAIL_ON_THE_LEFT_IN_COMPACT_LAYOUT, false);

            Resources resources = activity.getResources();
            mStartAutoplayVisibleAreaOffset = resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                    sharedPreferences.getInt(SharedPreferencesUtils.START_AUTOPLAY_VISIBLE_AREA_OFFSET_PORTRAIT, 75) / 100.0 :
                    sharedPreferences.getInt(SharedPreferencesUtils.START_AUTOPLAY_VISIBLE_AREA_OFFSET_LANDSCAPE, 50) / 100.0;

            mMuteNSFWVideo = sharedPreferences.getBoolean(SharedPreferencesUtils.MUTE_NSFW_VIDEO, false);
            mAutomaticallyTryRedgifs = sharedPreferences.getBoolean(SharedPreferencesUtils.AUTOMATICALLY_TRY_REDGIFS, true);

            mLongPressToHideToolbarInCompactLayout = sharedPreferences.getBoolean(SharedPreferencesUtils.LONG_PRESS_TO_HIDE_TOOLBAR_IN_COMPACT_LAYOUT, false);
            mCompactLayoutToolbarHiddenByDefault = sharedPreferences.getBoolean(SharedPreferencesUtils.POST_COMPACT_LAYOUT_TOOLBAR_HIDDEN_BY_DEFAULT, false);

            String dataSavingModeString = sharedPreferences.getString(SharedPreferencesUtils.DATA_SAVING_MODE, SharedPreferencesUtils.DATA_SAVING_MODE_OFF);
            if (dataSavingModeString.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ALWAYS)) {
                mDataSavingMode = true;
            } else if (dataSavingModeString.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ONLY_ON_CELLULAR_DATA)) {
                mDataSavingMode = networkType == Utils.NETWORK_TYPE_CELLULAR;
            }
            mDisableImagePreview = sharedPreferences.getBoolean(SharedPreferencesUtils.DISABLE_IMAGE_PREVIEW, false);
            mOnlyDisablePreviewInVideoAndGifPosts = sharedPreferences.getBoolean(SharedPreferencesUtils.ONLY_DISABLE_PREVIEW_IN_VIDEO_AND_GIF_POSTS, false);

            mMarkPostsAsRead = postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.MARK_POSTS_AS_READ_BASE, false);
            mMarkPostsAsReadAfterVoting = postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.MARK_POSTS_AS_READ_AFTER_VOTING_BASE, false);
            mMarkPostsAsReadOnScroll = postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.MARK_POSTS_AS_READ_ON_SCROLL_BASE, false);
            mHideReadPostsAutomatically = postHistorySharedPreferences.getBoolean((accountName == null ? "" : accountName) + SharedPreferencesUtils.HIDE_READ_POSTS_AUTOMATICALLY_BASE, false);

            mHidePostType = sharedPreferences.getBoolean(SharedPreferencesUtils.HIDE_POST_TYPE, false);
            mHidePostFlair = sharedPreferences.getBoolean(SharedPreferencesUtils.HIDE_POST_FLAIR, false);
            mHideTheNumberOfAwards = sharedPreferences.getBoolean(SharedPreferencesUtils.HIDE_THE_NUMBER_OF_AWARDS, false);
            mHideSubredditAndUserPrefix = sharedPreferences.getBoolean(SharedPreferencesUtils.HIDE_SUBREDDIT_AND_USER_PREFIX, false);
            mHideTheNumberOfVotes = sharedPreferences.getBoolean(SharedPreferencesUtils.HIDE_THE_NUMBER_OF_VOTES, false);
            mHideTheNumberOfComments = sharedPreferences.getBoolean(SharedPreferencesUtils.HIDE_THE_NUMBER_OF_COMMENTS, false);

            mPostLayout = postLayout;
            mDefaultLinkPostLayout = Integer.parseInt(sharedPreferences.getString(SharedPreferencesUtils.DEFAULT_LINK_POST_LAYOUT_KEY, "-1"));

            mColorPrimaryLightTheme = customThemeWrapper.getColorPrimaryLightTheme();
            mColorAccent = customThemeWrapper.getColorAccent();
            mCardViewBackgroundColor = customThemeWrapper.getCardViewBackgroundColor();
            mReadPostCardViewBackgroundColor = customThemeWrapper.getReadPostCardViewBackgroundColor();
            mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
            mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
            mPostTitleColor = customThemeWrapper.getPostTitleColor();
            mPostContentColor = customThemeWrapper.getPostContentColor();
            mReadPostTitleColor = customThemeWrapper.getReadPostTitleColor();
            mReadPostContentColor = customThemeWrapper.getReadPostContentColor();
            mStickiedPostIconTint = customThemeWrapper.getStickiedPostIconTint();
            mPostTypeBackgroundColor = customThemeWrapper.getPostTypeBackgroundColor();
            mPostTypeTextColor = customThemeWrapper.getPostTypeTextColor();
            mSubredditColor = customThemeWrapper.getSubreddit();
            mUsernameColor = customThemeWrapper.getUsername();
            mSpoilerBackgroundColor = customThemeWrapper.getSpoilerBackgroundColor();
            mSpoilerTextColor = customThemeWrapper.getSpoilerTextColor();
            mFlairBackgroundColor = customThemeWrapper.getFlairBackgroundColor();
            mFlairTextColor = customThemeWrapper.getFlairTextColor();
            mAwardsBackgroundColor = customThemeWrapper.getAwardsBackgroundColor();
            mAwardsTextColor = customThemeWrapper.getAwardsTextColor();
            mNSFWBackgroundColor = customThemeWrapper.getNsfwBackgroundColor();
            mNSFWTextColor = customThemeWrapper.getNsfwTextColor();
            mArchivedIconTint = customThemeWrapper.getArchivedIconTint();
            mLockedIconTint = customThemeWrapper.getLockedIconTint();
            mCrosspostIconTint = customThemeWrapper.getCrosspostIconTint();
            mNoPreviewPostTypeBackgroundColor = customThemeWrapper.getNoPreviewPostTypeBackgroundColor();
            mNoPreviewPostTypeIconTint = customThemeWrapper.getNoPreviewPostTypeIconTint();
            mUpvotedColor = customThemeWrapper.getUpvoted();
            mDownvotedColor = customThemeWrapper.getDownvoted();
            mVoteAndReplyUnavailableVoteButtonColor = customThemeWrapper.getVoteAndReplyUnavailableButtonColor();
            mButtonTextColor = customThemeWrapper.getButtonTextColor();
            mPostIconAndInfoColor = customThemeWrapper.getPostIconAndInfoColor();
            mDividerColor = customThemeWrapper.getDividerColor();

            mCommentIcon = activity.getDrawable(R.drawable.ic_comment_grey_24dp);
            if (mCommentIcon != null) {
                DrawableCompat.setTint(mCommentIcon, mPostIconAndInfoColor);
            }

            mScale = resources.getDisplayMetrics().density;
            mGlide = Glide.with(mActivity);
            mRedditDataRoomDatabase = redditDataRoomDatabase;
            mLocale = locale;
            mExoCreator = exoCreator;
            mCallback = callback;
        }
    }

    public void setCanStartActivity(boolean canStartActivity) {
        this.canStartActivity = canStartActivity;
    }

    @Override
    public int getItemViewType(int position) {
        // Reached at the end
        if (hasExtraRow() && position == getItemCount() - 1) {
            if (networkState.getStatus() == NetworkState.Status.LOADING) {
                return VIEW_TYPE_LOADING;
            } else {
                return VIEW_TYPE_ERROR;
            }
        } else {
            if (mPostLayout == SharedPreferencesUtils.POST_LAYOUT_CARD) {
                Post post = getItem(position);
                if (post != null) {
                    switch (post.getPostType()) {
                        case Post.VIDEO_TYPE:
                            if (mAutoplay) {
                                if (!mAutoplayNsfwVideos && post.isNSFW()) {
                                    return VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE;
                                }
                                return VIEW_TYPE_POST_CARD_VIDEO_AUTOPLAY_TYPE;
                            }
                            return VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE;
                        case Post.GIF_TYPE:
                        case Post.IMAGE_TYPE:
                        case Post.GALLERY_TYPE:
                            return VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE;
                        case Post.LINK_TYPE:
                        case Post.NO_PREVIEW_LINK_TYPE:
                            switch (mDefaultLinkPostLayout) {
                                case SharedPreferencesUtils.POST_LAYOUT_CARD_2:
                                    return VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE;
                                case SharedPreferencesUtils.POST_LAYOUT_GALLERY:
                                    return VIEW_TYPE_POST_GALLERY;
                                case SharedPreferencesUtils.POST_LAYOUT_COMPACT:
                                    return VIEW_TYPE_POST_COMPACT;
                            }
                            return VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE;
                        default:
                            return VIEW_TYPE_POST_CARD_TEXT_TYPE;
                    }
                }
                return VIEW_TYPE_POST_CARD_TEXT_TYPE;
            } else if (mPostLayout == SharedPreferencesUtils.POST_LAYOUT_COMPACT) {
                Post post = getItem(position);
                if (post != null) {
                    if (post.getPostType() == Post.LINK_TYPE || post.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
                        switch (mDefaultLinkPostLayout) {
                            case SharedPreferencesUtils.POST_LAYOUT_CARD:
                                return VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE;
                            case SharedPreferencesUtils.POST_LAYOUT_GALLERY:
                                return VIEW_TYPE_POST_GALLERY;
                            case SharedPreferencesUtils.POST_LAYOUT_CARD_2:
                                return VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE;
                        }
                    }
                }
                return VIEW_TYPE_POST_COMPACT;
            } else if (mPostLayout == SharedPreferencesUtils.POST_LAYOUT_GALLERY) {
                return VIEW_TYPE_POST_GALLERY;
            } else {
                Post post = getItem(position);
                if (post != null) {
                    switch (post.getPostType()) {
                        case Post.VIDEO_TYPE:
                            if (mAutoplay) {
                                if (!mAutoplayNsfwVideos && post.isNSFW()) {
                                    return VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE;
                                }
                                return VIEW_TYPE_POST_CARD_2_VIDEO_AUTOPLAY_TYPE;
                            }
                            return VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE;
                        case Post.GIF_TYPE:
                        case Post.IMAGE_TYPE:
                        case Post.GALLERY_TYPE:
                            return VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE;
                        case Post.LINK_TYPE:
                        case Post.NO_PREVIEW_LINK_TYPE:
                            switch (mDefaultLinkPostLayout) {
                                case SharedPreferencesUtils.POST_LAYOUT_CARD:
                                    return VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE;
                                case SharedPreferencesUtils.POST_LAYOUT_GALLERY:
                                    return VIEW_TYPE_POST_GALLERY;
                                case SharedPreferencesUtils.POST_LAYOUT_COMPACT:
                                    return VIEW_TYPE_POST_COMPACT;
                            }
                            return VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE;
                        default:
                            return VIEW_TYPE_POST_CARD_2_TEXT_TYPE;
                    }
                }
                return VIEW_TYPE_POST_CARD_2_TEXT_TYPE;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_POST_CARD_VIDEO_AUTOPLAY_TYPE) {
            if (mDataSavingMode) {
                return new PostWithPreviewTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_with_preview, parent, false));
            }
            return new PostVideoAutoplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_video_type_autoplay, parent, false));
        } else if (viewType == VIEW_TYPE_POST_CARD_WITH_PREVIEW_TYPE) {
            return new PostWithPreviewTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_with_preview, parent, false));
        } else if (viewType == VIEW_TYPE_POST_CARD_TEXT_TYPE) {
            return new PostTextTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_text, parent, false));
        } else if (viewType == VIEW_TYPE_POST_COMPACT) {
            if (mShowThumbnailOnTheRightInCompactLayout) {
                return new PostCompactRightThumbnailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_compact_right_thumbnail, parent, false));
            } else {
                return new PostCompactLeftThumbnailViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_compact, parent, false));
            }
        } else if (viewType == VIEW_TYPE_POST_GALLERY) {
            return new PostGalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_gallery, parent, false));
        } else if (viewType == VIEW_TYPE_POST_CARD_2_VIDEO_AUTOPLAY_TYPE) {
            return new PostCard2VideoAutoplayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_card_2_video_autoplay, parent, false));
        } else if (viewType == VIEW_TYPE_POST_CARD_2_WITH_PREVIEW_TYPE) {
            return new PostCard2WithPreviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_card_2_with_preview, parent, false));
        } else if (viewType == VIEW_TYPE_POST_CARD_2_TEXT_TYPE) {
            return new PostCard2TextTypeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_card_2_text, parent, false));
        } else if (viewType == VIEW_TYPE_ERROR) {
            return new ErrorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_error, parent, false));
        } else {
            return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_loading, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostBaseViewHolder) {
            Post post = getItem(position);
            if (post != null) {
                if (post.isRead()) {
                    if ((mHideReadPostsAutomatically && !post.isHiddenManuallyByUser()) || position < mHideReadPostsIndex) {
                        post.hidePostInRecyclerView();
                        holder.itemView.setVisibility(View.GONE);
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                        params.height = 0;
                        params.topMargin = 0;
                        params.bottomMargin = 0;
                        holder.itemView.setLayoutParams(params);
                        return;
                    }
                    if (((PostBaseViewHolder) holder).itemViewIsNotCardView) {
                        holder.itemView.setBackgroundColor(mReadPostCardViewBackgroundColor);
                    } else {
                        holder.itemView.setBackgroundTintList(ColorStateList.valueOf(mReadPostCardViewBackgroundColor));
                    }

                    ((PostBaseViewHolder) holder).titleTextView.setTextColor(mReadPostTitleColor);
                }
                String subredditNamePrefixed = post.getSubredditNamePrefixed();
                String subredditName = subredditNamePrefixed.substring(2);
                String authorPrefixed = "u/" + post.getAuthor();
                String flair = post.getFlair();
                int nAwards = post.getNAwards();

                if (mHideSubredditAndUserPrefix) {
                    ((PostBaseViewHolder) holder).subredditTextView.setText(subredditName);
                    ((PostBaseViewHolder) holder).userTextView.setText(post.getAuthor());
                } else {
                    ((PostBaseViewHolder) holder).subredditTextView.setText(subredditNamePrefixed);
                    ((PostBaseViewHolder) holder).userTextView.setText(authorPrefixed);
                }

                if (mDisplaySubredditName) {
                    if (authorPrefixed.equals(subredditNamePrefixed)) {
                        if (post.getAuthorIconUrl() == null) {
                            LoadUserData.loadUserData(mExecutor, new Handler(), mRedditDataRoomDatabase, post.getAuthor(),
                                    mRetrofit, iconImageUrl -> {
                                if (mActivity != null && getItemCount() > 0) {
                                    if (iconImageUrl == null || iconImageUrl.equals("")) {
                                        mGlide.load(R.drawable.subreddit_default_icon)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                .into(((PostBaseViewHolder) holder).iconGifImageView);
                                    } else {
                                        mGlide.load(iconImageUrl)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                                .into(((PostBaseViewHolder) holder).iconGifImageView);
                                    }

                                    if (holder.getBindingAdapterPosition() >= 0) {
                                        post.setAuthorIconUrl(iconImageUrl);
                                    }
                                }
                            });
                        } else if (!post.getAuthorIconUrl().equals("")) {
                            mGlide.load(post.getAuthorIconUrl())
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .error(mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                    .into(((PostBaseViewHolder) holder).iconGifImageView);
                        } else {
                            mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .into(((PostBaseViewHolder) holder).iconGifImageView);
                        }
                    } else {
                        if (post.getSubredditIconUrl() == null) {
                            LoadSubredditIcon.loadSubredditIcon(mExecutor, new Handler(), mRedditDataRoomDatabase,
                                    subredditName, mRetrofit,
                                    iconImageUrl -> {
                                        if (mActivity != null && getItemCount() > 0) {
                                            if (iconImageUrl == null || iconImageUrl.equals("")) {
                                                mGlide.load(R.drawable.subreddit_default_icon)
                                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                        .into(((PostBaseViewHolder) holder).iconGifImageView);
                                            } else {
                                                mGlide.load(iconImageUrl)
                                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                                        .into(((PostBaseViewHolder) holder).iconGifImageView);
                                            }

                                            if (holder.getBindingAdapterPosition() >= 0) {
                                                post.setSubredditIconUrl(iconImageUrl);
                                            }
                                        }
                                    });
                        } else if (!post.getSubredditIconUrl().equals("")) {
                            mGlide.load(post.getSubredditIconUrl())
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .error(mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                    .into(((PostBaseViewHolder) holder).iconGifImageView);
                        } else {
                            mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .into(((PostBaseViewHolder) holder).iconGifImageView);
                        }
                    }
                } else {
                    if (post.getAuthorIconUrl() == null) {
                        String authorName = post.getAuthor().equals("[deleted]") ? post.getSubredditNamePrefixed().substring(2) : post.getAuthor();
                        LoadUserData.loadUserData(mExecutor, new Handler(), mRedditDataRoomDatabase, authorName, mRetrofit, iconImageUrl -> {
                            if (mActivity != null && getItemCount() > 0) {
                                if (iconImageUrl == null || iconImageUrl.equals("")) {
                                    mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                            .into(((PostBaseViewHolder) holder).iconGifImageView);
                                } else {
                                    mGlide.load(iconImageUrl)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                            .into(((PostBaseViewHolder) holder).iconGifImageView);
                                }

                                if (holder.getBindingAdapterPosition() >= 0) {
                                    post.setAuthorIconUrl(iconImageUrl);
                                }
                            }
                        });
                    } else if (!post.getAuthorIconUrl().equals("")) {
                        mGlide.load(post.getAuthorIconUrl())
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                .error(mGlide.load(R.drawable.subreddit_default_icon)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                .into(((PostBaseViewHolder) holder).iconGifImageView);
                    } else {
                        mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                .into(((PostBaseViewHolder) holder).iconGifImageView);
                    }
                }

                if (mShowElapsedTime) {
                    ((PostBaseViewHolder) holder).postTimeTextView.setText(
                            Utils.getElapsedTime(mActivity, post.getPostTimeMillis()));
                } else {
                    ((PostBaseViewHolder) holder).postTimeTextView.setText(Utils.getFormattedTime(mLocale, post.getPostTimeMillis(), mTimeFormatPattern));
                }

                ((PostBaseViewHolder) holder).titleTextView.setText(post.getTitle());
                if (!mHideTheNumberOfVotes) {
                    ((PostBaseViewHolder) holder).scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                } else {
                    ((PostBaseViewHolder) holder).scoreTextView.setText(mActivity.getString(R.string.vote));
                }

                if (post.isLocked()) {
                    ((PostBaseViewHolder) holder).lockedImageView.setVisibility(View.VISIBLE);
                }

                if (post.isNSFW()) {
                    ((PostBaseViewHolder) holder).nsfwTextView.setVisibility(View.VISIBLE);
                }

                if (post.isSpoiler()) {
                    ((PostBaseViewHolder) holder).spoilerTextView.setVisibility(View.VISIBLE);
                }

                if (flair != null && !flair.equals("")) {
                    if (mHidePostFlair) {
                        ((PostBaseViewHolder) holder).flairTextView.setVisibility(View.GONE);
                    } else {
                        ((PostBaseViewHolder) holder).flairTextView.setVisibility(View.VISIBLE);
                        Utils.setHTMLWithImageToTextView(((PostBaseViewHolder) holder).flairTextView, flair, false);
                    }
                }

                if (nAwards > 0 && !mHideTheNumberOfAwards) {
                    ((PostBaseViewHolder) holder).awardsTextView.setVisibility(View.VISIBLE);
                    if (nAwards == 1) {
                        ((PostBaseViewHolder) holder).awardsTextView.setText(mActivity.getString(R.string.one_award));
                    } else {
                        ((PostBaseViewHolder) holder).awardsTextView.setText(mActivity.getString(R.string.n_awards, nAwards));
                    }
                }

                switch (post.getVoteType()) {
                    case 1:
                        //Upvoted
                        ((PostBaseViewHolder) holder).upvoteButton.setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                        ((PostBaseViewHolder) holder).scoreTextView.setTextColor(mUpvotedColor);
                        break;
                    case -1:
                        //Downvoted
                        ((PostBaseViewHolder) holder).downvoteButton.setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                        ((PostBaseViewHolder) holder).scoreTextView.setTextColor(mDownvotedColor);
                        break;
                }

                if (mPostType == PostDataSource.TYPE_SUBREDDIT && !mDisplaySubredditName && post.isStickied()) {
                    ((PostBaseViewHolder) holder).stickiedPostImageView.setVisibility(View.VISIBLE);
                    mGlide.load(R.drawable.ic_thumbtack_24dp).into(((PostBaseViewHolder) holder).stickiedPostImageView);
                }

                if (post.isArchived()) {
                    ((PostBaseViewHolder) holder).archivedImageView.setVisibility(View.VISIBLE);

                    ((PostBaseViewHolder) holder).upvoteButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor, PorterDuff.Mode.SRC_IN);
                    ((PostBaseViewHolder) holder).downvoteButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor, PorterDuff.Mode.SRC_IN);
                }

                if (post.isCrosspost()) {
                    ((PostBaseViewHolder) holder).crosspostImageView.setVisibility(View.VISIBLE);
                }

                if (!mHideTheNumberOfComments) {
                    ((PostBaseViewHolder) holder).commentsCountTextView.setVisibility(View.VISIBLE);
                    ((PostBaseViewHolder) holder).commentsCountTextView.setText(Integer.toString(post.getNComments()));
                } else {
                    ((PostBaseViewHolder) holder).commentsCountTextView.setVisibility(View.GONE);
                }

                if (post.isSaved()) {
                    ((PostBaseViewHolder) holder).saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                } else {
                    ((PostBaseViewHolder) holder).saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                }

                if (mHidePostType) {
                    ((PostBaseViewHolder) holder).typeTextView.setVisibility(View.GONE);
                } else {
                    ((PostBaseViewHolder) holder).typeTextView.setVisibility(View.VISIBLE);
                }

                if (holder instanceof PostVideoAutoplayViewHolder) {
                    ((PostVideoAutoplayViewHolder) holder).previewImageView.setVisibility(View.VISIBLE);
                    Post.Preview preview = getSuitablePreview(post.getPreviews());
                    if (preview != null) {
                        ((PostVideoAutoplayViewHolder) holder).aspectRatioFrameLayout.setAspectRatio((float) preview.getPreviewWidth() / preview.getPreviewHeight());
                        if (mImageViewWidth > preview.getPreviewWidth()) {
                            mGlide.load(preview.getPreviewUrl()).override(Target.SIZE_ORIGINAL).into(((PostVideoAutoplayViewHolder) holder).previewImageView);
                        } else {
                            mGlide.load(preview.getPreviewUrl()).into(((PostVideoAutoplayViewHolder) holder).previewImageView);
                        }
                    } else {
                        ((PostVideoAutoplayViewHolder) holder).aspectRatioFrameLayout.setAspectRatio(1);
                    }
                    if (mFragment.getMasterMutingOption() == null) {
                        ((PostVideoAutoplayViewHolder) holder).setVolume(mMuteAutoplayingVideos || (post.isNSFW() && mMuteNSFWVideo) ? 0f : 1f);
                    } else {
                        ((PostVideoAutoplayViewHolder) holder).setVolume(mFragment.getMasterMutingOption() ? 0f : 1f);
                    }

                    if (post.isGfycat() || post.isRedgifs() && !post.isLoadGfyOrRedgifsVideoSuccess()) {
                        ((PostVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks = new FetchGfycatOrRedgifsVideoLinks(new FetchGfycatOrRedgifsVideoLinks.FetchGfycatOrRedgifsVideoLinksListener() {
                            @Override
                            public void success(String webm, String mp4) {
                                post.setVideoDownloadUrl(mp4);
                                post.setVideoUrl(webm);
                                post.setLoadGfyOrRedgifsVideoSuccess(true);
                                if (position == holder.getBindingAdapterPosition()) {
                                    ((PostVideoAutoplayViewHolder) holder).bindVideoUri(Uri.parse(post.getVideoUrl()));
                                }
                            }

                            @Override
                            public void failed(int errorCode) {
                                if (position == holder.getBindingAdapterPosition()) {
                                    ((PostVideoAutoplayViewHolder) holder).errorLoadingGfycatImageView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        ((PostVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks
                                .fetchGfycatOrRedgifsVideoLinksInRecyclerViewAdapter(mExecutor, new Handler(),
                                        mGfycatRetrofit, mRedgifsRetrofit, post.getGfycatId(),
                                        post.isGfycat(), mAutomaticallyTryRedgifs);
                    } else {
                        ((PostVideoAutoplayViewHolder) holder).bindVideoUri(Uri.parse(post.getVideoUrl()));
                    }
                } else if (holder instanceof PostWithPreviewTypeViewHolder) {
                    if (post.getPostType() == Post.VIDEO_TYPE) {
                        ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_circle_36dp));
                        ((PostWithPreviewTypeViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.video));
                    } else if (post.getPostType() == Post.GIF_TYPE) {
                        if (!mAutoplay) {
                            ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                            ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_circle_36dp));
                        }
                        ((PostWithPreviewTypeViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.gif));
                    } else if (post.getPostType() == Post.IMAGE_TYPE) {
                        ((PostWithPreviewTypeViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.image));
                    } else if (post.getPostType() == Post.LINK_TYPE || post.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
                        ((PostWithPreviewTypeViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.link));
                        ((PostWithPreviewTypeViewHolder) holder).linkTextView.setVisibility(View.VISIBLE);
                        String domain = Uri.parse(post.getUrl()).getHost();
                        ((PostWithPreviewTypeViewHolder) holder).linkTextView.setText(domain);
                        if (post.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
                            ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setVisibility(View.VISIBLE);
                            ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_link);
                        }
                    } else if (post.getPostType() == Post.GALLERY_TYPE) {
                        ((PostWithPreviewTypeViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.gallery));
                        ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_gallery_reverse_color_24dp));
                    }

                    if (post.getPostType() != Post.NO_PREVIEW_LINK_TYPE) {
                        ((PostWithPreviewTypeViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                    }

                    if (mDataSavingMode && mDisableImagePreview) {
                        ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setVisibility(View.VISIBLE);
                        if (post.getPostType() == Post.VIDEO_TYPE) {
                            ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                            ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                        } else if (post.getPostType() == Post.IMAGE_TYPE || post.getPostType() == Post.GIF_TYPE) {
                            ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_image_24dp);
                            ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                        } else if (post.getPostType() == Post.LINK_TYPE) {
                            ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_link);
                        } else if (post.getPostType() == Post.GALLERY_TYPE) {
                            ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                        }
                    } else if (mDataSavingMode && mOnlyDisablePreviewInVideoAndGifPosts && (post.getPostType() == Post.VIDEO_TYPE || post.getPostType() == Post.GIF_TYPE)) {
                        ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setVisibility(View.VISIBLE);
                        ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                        ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                    } else {
                        Post.Preview preview = getSuitablePreview(post.getPreviews());
                        if (preview != null) {
                            ((PostWithPreviewTypeViewHolder) holder).imageWrapperRelativeLayout.setVisibility(View.VISIBLE);
                            if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                                int height = (int) (400 * mScale);
                                ((PostWithPreviewTypeViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ((PostWithPreviewTypeViewHolder) holder).imageView.getLayoutParams().height = height;
                                preview.setPreviewWidth(mImageViewWidth);
                                preview.setPreviewHeight(height);
                            } else {
                                ((PostWithPreviewTypeViewHolder) holder).imageView
                                        .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());
                            }
                            loadImage(holder, post, preview);
                        } else {
                            ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setVisibility(View.VISIBLE);
                            if (post.getPostType() == Post.VIDEO_TYPE) {
                                ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                                ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                            } else if (post.getPostType() == Post.IMAGE_TYPE || post.getPostType() == Post.GIF_TYPE) {
                                ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_image_24dp);
                                ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                            } else if (post.getPostType() == Post.LINK_TYPE) {
                                ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_link);
                            } else if (post.getPostType() == Post.GALLERY_TYPE) {
                                ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                            }
                        }
                    }
                } else if (holder instanceof PostTextTypeViewHolder) {
                    if (!post.isSpoiler() && post.getSelfTextPlainTrimmed() != null && !post.getSelfTextPlainTrimmed().equals("")) {
                        ((PostTextTypeViewHolder) holder).contentTextView.setVisibility(View.VISIBLE);
                        if (post.isRead()) {
                            ((PostTextTypeViewHolder) holder).contentTextView.setTextColor(mReadPostContentColor);
                        }
                        ((PostTextTypeViewHolder) holder).contentTextView.setText(post.getSelfTextPlainTrimmed());
                    }
                } else if (holder instanceof PostCard2VideoAutoplayViewHolder) {
                    ((PostCard2VideoAutoplayViewHolder) holder).previewImageView.setVisibility(View.VISIBLE);
                    Post.Preview preview = getSuitablePreview(post.getPreviews());
                    if (preview != null) {
                        ((PostCard2VideoAutoplayViewHolder) holder).aspectRatioFrameLayout.setAspectRatio((float) preview.getPreviewWidth() / preview.getPreviewHeight());
                        if (mImageViewWidth > preview.getPreviewWidth()) {
                            mGlide.load(preview.getPreviewUrl()).override(Target.SIZE_ORIGINAL).into(((PostCard2VideoAutoplayViewHolder) holder).previewImageView);
                        } else {
                            mGlide.load(preview.getPreviewUrl()).into(((PostCard2VideoAutoplayViewHolder) holder).previewImageView);
                        }
                    } else {
                        ((PostCard2VideoAutoplayViewHolder) holder).aspectRatioFrameLayout.setAspectRatio(1);
                    }
                    if (mFragment.getMasterMutingOption() == null) {
                        ((PostCard2VideoAutoplayViewHolder) holder).setVolume(mMuteAutoplayingVideos || (post.isNSFW() && mMuteNSFWVideo) ? 0f : 1f);
                    } else {
                        ((PostCard2VideoAutoplayViewHolder) holder).setVolume(mFragment.getMasterMutingOption() ? 0f : 1f);
                    }

                    if (post.isGfycat() || post.isRedgifs() && !post.isLoadGfyOrRedgifsVideoSuccess()) {
                        ((PostCard2VideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks = new FetchGfycatOrRedgifsVideoLinks(new FetchGfycatOrRedgifsVideoLinks.FetchGfycatOrRedgifsVideoLinksListener() {
                            @Override
                            public void success(String webm, String mp4) {
                                post.setVideoDownloadUrl(mp4);
                                post.setVideoUrl(webm);
                                post.setLoadGfyOrRedgifsVideoSuccess(true);
                                if (position == holder.getBindingAdapterPosition()) {
                                    ((PostCard2VideoAutoplayViewHolder) holder).bindVideoUri(Uri.parse(post.getVideoUrl()));
                                }
                            }

                            @Override
                            public void failed(int errorCode) {
                                if (position == holder.getBindingAdapterPosition()) {
                                    ((PostCard2VideoAutoplayViewHolder) holder).errorLoadingGfycatImageView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        ((PostCard2VideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks
                                .fetchGfycatOrRedgifsVideoLinksInRecyclerViewAdapter(mExecutor, new Handler(),
                                        mGfycatRetrofit, mRedgifsRetrofit, post.getGfycatId(), post.isGfycat(),
                                        mAutomaticallyTryRedgifs);
                    } else {
                        ((PostCard2VideoAutoplayViewHolder) holder).bindVideoUri(Uri.parse(post.getVideoUrl()));
                    }
                } else if (holder instanceof PostCard2WithPreviewViewHolder) {
                    if (post.getPostType() == Post.VIDEO_TYPE) {
                        ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_circle_36dp));
                        ((PostCard2WithPreviewViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.video));
                    } else if (post.getPostType() == Post.GIF_TYPE) {
                        if (!mAutoplay) {
                            ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                            ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_circle_36dp));
                        }
                        ((PostCard2WithPreviewViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.gif));
                    } else if (post.getPostType() == Post.IMAGE_TYPE) {
                        ((PostCard2WithPreviewViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.image));
                    } else if (post.getPostType() == Post.LINK_TYPE || post.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
                        ((PostCard2WithPreviewViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.link));
                        ((PostCard2WithPreviewViewHolder) holder).linkTextView.setVisibility(View.VISIBLE);
                        String domain = Uri.parse(post.getUrl()).getHost();
                        ((PostCard2WithPreviewViewHolder) holder).linkTextView.setText(domain);
                        if (post.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
                            ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                            ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_link);
                        }
                    } else if (post.getPostType() == Post.GALLERY_TYPE) {
                        ((PostCard2WithPreviewViewHolder) holder).typeTextView.setText(mActivity.getString(R.string.gallery));
                        ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_gallery_reverse_color_24dp));
                    }

                    if (post.getPostType() != Post.NO_PREVIEW_LINK_TYPE) {
                        ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                    }

                    if (mDataSavingMode && mDisableImagePreview) {
                        ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.GONE);
                        ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                        if (post.getPostType() == Post.VIDEO_TYPE) {
                            ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                            ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                        } else if (post.getPostType() == Post.IMAGE_TYPE || post.getPostType() == Post.GIF_TYPE) {
                            ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_image_24dp);
                            ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                        } else if (post.getPostType() == Post.LINK_TYPE) {
                            ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_link);
                        } else if (post.getPostType() == Post.GALLERY_TYPE) {
                            ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                        }
                    } else if (mDataSavingMode && mOnlyDisablePreviewInVideoAndGifPosts && (post.getPostType() == Post.VIDEO_TYPE || post.getPostType() == Post.GIF_TYPE)) {
                        ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.GONE);
                        ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                        ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                        ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                    } else {
                        Post.Preview preview = getSuitablePreview(post.getPreviews());
                        if (preview != null) {
                            ((PostCard2WithPreviewViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                            if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                                int height = (int) (400 * mScale);
                                ((PostCard2WithPreviewViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ((PostCard2WithPreviewViewHolder) holder).imageView.getLayoutParams().height = height;
                                preview.setPreviewWidth(mImageViewWidth);
                                preview.setPreviewHeight(height);
                            } else {
                                ((PostCard2WithPreviewViewHolder) holder).imageView
                                        .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());
                            }
                            loadImage(holder, post, preview);
                        } else {
                            ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.GONE);
                            ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                            if (post.getPostType() == Post.VIDEO_TYPE) {
                                ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                                ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                            } else if (post.getPostType() == Post.IMAGE_TYPE || post.getPostType() == Post.GIF_TYPE) {
                                ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_image_24dp);
                                ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                            } else if (post.getPostType() == Post.LINK_TYPE) {
                                ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_link);
                            } else if (post.getPostType() == Post.GALLERY_TYPE) {
                                ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                            }
                        }
                    }
                } else if (holder instanceof PostCard2TextTypeViewHolder) {
                    if (!post.isSpoiler() && post.getSelfTextPlainTrimmed() != null && !post.getSelfTextPlainTrimmed().equals("")) {
                        ((PostCard2TextTypeViewHolder) holder).contentTextView.setVisibility(View.VISIBLE);
                        if (post.isRead()) {
                            ((PostCard2TextTypeViewHolder) holder).contentTextView.setTextColor(mReadPostContentColor);
                        }
                        ((PostCard2TextTypeViewHolder) holder).contentTextView.setText(post.getSelfTextPlainTrimmed());
                    }
                }
                mCallback.currentlyBindItem(holder.getBindingAdapterPosition());
            }
        } else if (holder instanceof PostCompactBaseViewHolder) {
            Post post = getItem(position);
            if (post != null) {
                if (post.isRead()) {
                    if ((mHideReadPostsAutomatically && !post.isHiddenManuallyByUser()) || position < mHideReadPostsIndex) {
                        post.hidePostInRecyclerView();
                        holder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        params.height = 0;
                        holder.itemView.setLayoutParams(params);
                        return;
                    }
                    holder.itemView.setBackgroundColor(mReadPostCardViewBackgroundColor);
                    ((PostCompactBaseViewHolder) holder).titleTextView.setTextColor(mReadPostTitleColor);
                }
                final String subredditNamePrefixed = post.getSubredditNamePrefixed();
                String subredditName = subredditNamePrefixed.substring(2);
                String authorPrefixed = "u/" + post.getAuthor();
                final String title = post.getTitle();
                int voteType = post.getVoteType();
                boolean nsfw = post.isNSFW();
                boolean spoiler = post.isSpoiler();
                String flair = post.getFlair();
                int nAwards = post.getNAwards();
                boolean isArchived = post.isArchived();

                if (mDisplaySubredditName) {
                    if (authorPrefixed.equals(subredditNamePrefixed)) {
                        if (post.getAuthorIconUrl() == null) {
                            LoadUserData.loadUserData(mExecutor, new Handler(), mRedditDataRoomDatabase, post.getAuthor(), mRetrofit, iconImageUrl -> {
                                if (mActivity != null && getItemCount() > 0) {
                                    if (iconImageUrl == null || iconImageUrl.equals("")) {
                                        mGlide.load(R.drawable.subreddit_default_icon)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                                    } else {
                                        mGlide.load(iconImageUrl)
                                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                                .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                                    }

                                    if (holder.getBindingAdapterPosition() >= 0) {
                                        post.setAuthorIconUrl(iconImageUrl);
                                    }
                                }
                            });
                        } else if (!post.getAuthorIconUrl().equals("")) {
                            mGlide.load(post.getAuthorIconUrl())
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .error(mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                    .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                        } else {
                            mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                        }
                    } else {
                        if (post.getSubredditIconUrl() == null) {
                            LoadSubredditIcon.loadSubredditIcon(mExecutor, new Handler(), mRedditDataRoomDatabase,
                                    subredditName, mRetrofit,
                                    iconImageUrl -> {
                                        if (mActivity != null && getItemCount() > 0) {
                                            if (iconImageUrl == null || iconImageUrl.equals("")) {
                                                mGlide.load(R.drawable.subreddit_default_icon)
                                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                        .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                                            } else {
                                                mGlide.load(iconImageUrl)
                                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                                        .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                                            }

                                            if (holder.getBindingAdapterPosition() >= 0) {
                                                post.setSubredditIconUrl(iconImageUrl);
                                            }
                                        }
                                    });
                        } else if (!post.getSubredditIconUrl().equals("")) {
                            mGlide.load(post.getSubredditIconUrl())
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .error(mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                    .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                        } else {
                            mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                        }
                    }

                    ((PostCompactBaseViewHolder) holder).nameTextView.setTextColor(mSubredditColor);
                    if (mHideSubredditAndUserPrefix) {
                        ((PostCompactBaseViewHolder) holder).nameTextView.setText(subredditName);
                    } else {
                        ((PostCompactBaseViewHolder) holder).nameTextView.setText(subredditNamePrefixed);
                    }
                } else {
                    if (post.getAuthorIconUrl() == null) {
                        String authorName = post.getAuthor().equals("[deleted]") ? post.getSubredditNamePrefixed().substring(2) : post.getAuthor();
                        LoadUserData.loadUserData(mExecutor, new Handler(), mRedditDataRoomDatabase, authorName, mRetrofit, iconImageUrl -> {
                            if (mActivity != null && getItemCount() > 0) {
                                if (iconImageUrl == null || iconImageUrl.equals("")) {
                                    mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                            .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                                } else {
                                    mGlide.load(iconImageUrl)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                            .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                                }

                                if (holder.getBindingAdapterPosition() >= 0) {
                                    post.setAuthorIconUrl(iconImageUrl);
                                }
                            }
                        });
                    } else if (!post.getAuthorIconUrl().equals("")) {
                        mGlide.load(post.getAuthorIconUrl())
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                .error(mGlide.load(R.drawable.subreddit_default_icon)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                    } else {
                        mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                .into(((PostCompactBaseViewHolder) holder).iconGifImageView);
                    }

                    ((PostCompactBaseViewHolder) holder).nameTextView.setTextColor(mUsernameColor);
                    if (mHideSubredditAndUserPrefix) {
                        ((PostCompactBaseViewHolder) holder).nameTextView.setText(post.getAuthor());
                    } else {
                        ((PostCompactBaseViewHolder) holder).nameTextView.setText(authorPrefixed);
                    }
                }

                if (mShowElapsedTime) {
                    ((PostCompactBaseViewHolder) holder).postTimeTextView.setText(
                            Utils.getElapsedTime(mActivity, post.getPostTimeMillis()));
                } else {
                    ((PostCompactBaseViewHolder) holder).postTimeTextView.setText(Utils.getFormattedTime(mLocale, post.getPostTimeMillis(), mTimeFormatPattern));
                }

                if (mCompactLayoutToolbarHiddenByDefault) {
                    ViewGroup.LayoutParams params = ((PostCompactBaseViewHolder) holder).bottomConstraintLayout.getLayoutParams();
                    params.height = 0;
                    ((PostCompactBaseViewHolder) holder).bottomConstraintLayout.setLayoutParams(params);
                } else {
                    ViewGroup.LayoutParams params = ((PostCompactBaseViewHolder) holder).bottomConstraintLayout.getLayoutParams();
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    ((PostCompactBaseViewHolder) holder).bottomConstraintLayout.setLayoutParams(params);
                }

                if (mShowDividerInCompactLayout) {
                    ((PostCompactBaseViewHolder) holder).divider.setVisibility(View.VISIBLE);
                } else {
                    ((PostCompactBaseViewHolder) holder).divider.setVisibility(View.GONE);
                }

                ((PostCompactBaseViewHolder) holder).titleTextView.setText(title);
                if (!mHideTheNumberOfVotes) {
                    ((PostCompactBaseViewHolder) holder).scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                } else {
                    ((PostCompactBaseViewHolder) holder).scoreTextView.setText(mActivity.getString(R.string.vote));
                }

                if (post.isLocked()) {
                    ((PostCompactBaseViewHolder) holder).lockedImageView.setVisibility(View.VISIBLE);
                }

                if (nsfw) {
                    ((PostCompactBaseViewHolder) holder).nsfwTextView.setVisibility(View.VISIBLE);
                }

                if (spoiler) {
                    ((PostCompactBaseViewHolder) holder).spoilerTextView.setVisibility(View.VISIBLE);
                }

                if (flair != null && !flair.equals("")) {
                    if (mHidePostFlair) {
                        ((PostCompactBaseViewHolder) holder).flairTextView.setVisibility(View.GONE);
                    } else {
                        ((PostCompactBaseViewHolder) holder).flairTextView.setVisibility(View.VISIBLE);
                        Utils.setHTMLWithImageToTextView(((PostCompactBaseViewHolder) holder).flairTextView, flair, false);
                    }
                }

                if (nAwards > 0 && !mHideTheNumberOfAwards) {
                    ((PostCompactBaseViewHolder) holder).awardsTextView.setVisibility(View.VISIBLE);
                    if (nAwards == 1) {
                        ((PostCompactBaseViewHolder) holder).awardsTextView.setText(mActivity.getString(R.string.one_award));
                    } else {
                        ((PostCompactBaseViewHolder) holder).awardsTextView.setText(mActivity.getString(R.string.n_awards, nAwards));
                    }
                }

                switch (voteType) {
                    case 1:
                        //Upvoted
                        ((PostCompactBaseViewHolder) holder).upvoteButton.setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                        ((PostCompactBaseViewHolder) holder).scoreTextView.setTextColor(mUpvotedColor);
                        break;
                    case -1:
                        //Downvoted
                        ((PostCompactBaseViewHolder) holder).downvoteButton.setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                        ((PostCompactBaseViewHolder) holder).scoreTextView.setTextColor(mDownvotedColor);
                        break;
                }

                if (post.getPostType() != Post.TEXT_TYPE && post.getPostType() != Post.NO_PREVIEW_LINK_TYPE && !(mDataSavingMode && mDisableImagePreview)) {
                    ((PostCompactBaseViewHolder) holder).relativeLayout.setVisibility(View.VISIBLE);
                    if (post.getPostType() == Post.GALLERY_TYPE && post.getPreviews() != null && post.getPreviews().isEmpty()) {
                        ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.VISIBLE);
                        ((PostCompactBaseViewHolder) holder).noPreviewPostImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                    }
                    ArrayList<Post.Preview> previews = post.getPreviews();
                    if (previews != null && !previews.isEmpty()) {
                        if (post.getPostType() != Post.GIF_TYPE && post.getPostType() != Post.VIDEO_TYPE) {
                            ((PostCompactBaseViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                            ((PostCompactBaseViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        }
                        loadImage(holder, post, previews.get(0));
                    }
                }

                if (mPostType == PostDataSource.TYPE_SUBREDDIT && !mDisplaySubredditName && post.isStickied()) {
                    ((PostCompactBaseViewHolder) holder).stickiedPostImageView.setVisibility(View.VISIBLE);
                    mGlide.load(R.drawable.ic_thumbtack_24dp).into(((PostCompactBaseViewHolder) holder).stickiedPostImageView);
                }

                if (isArchived) {
                    ((PostCompactBaseViewHolder) holder).archivedImageView.setVisibility(View.VISIBLE);

                    ((PostCompactBaseViewHolder) holder).upvoteButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor, PorterDuff.Mode.SRC_IN);
                    ((PostCompactBaseViewHolder) holder).downvoteButton
                            .setColorFilter(mVoteAndReplyUnavailableVoteButtonColor, PorterDuff.Mode.SRC_IN);
                }

                if (post.isCrosspost()) {
                    ((PostCompactBaseViewHolder) holder).crosspostImageView.setVisibility(View.VISIBLE);
                }

                if (mHidePostType) {
                    ((PostCompactBaseViewHolder) holder).typeTextView.setVisibility(View.GONE);
                } else {
                    ((PostCompactBaseViewHolder) holder).typeTextView.setVisibility(View.VISIBLE);
                }

                switch (post.getPostType()) {
                    case Post.IMAGE_TYPE:
                        ((PostCompactBaseViewHolder) holder).typeTextView.setText(R.string.image);
                        if (mDataSavingMode && mDisableImagePreview) {
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.VISIBLE);
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageView.setImageResource(R.drawable.ic_image_24dp);
                        }
                        break;
                    case Post.LINK_TYPE:
                        ((PostCompactBaseViewHolder) holder).typeTextView.setText(R.string.link);
                        if (mDataSavingMode && mDisableImagePreview) {
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.VISIBLE);
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageView.setImageResource(R.drawable.ic_link);
                        }

                        ((PostCompactBaseViewHolder) holder).linkTextView.setVisibility(View.VISIBLE);
                        String domain = Uri.parse(post.getUrl()).getHost();
                        ((PostCompactBaseViewHolder) holder).linkTextView.setText(domain);
                        break;
                    case Post.GIF_TYPE:
                        ((PostCompactBaseViewHolder) holder).typeTextView.setText(R.string.gif);
                        if (mDataSavingMode && (mDisableImagePreview || mOnlyDisablePreviewInVideoAndGifPosts)) {
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.VISIBLE);
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageView.setImageResource(R.drawable.ic_image_24dp);
                        } else {
                            ((PostCompactBaseViewHolder) holder).playButtonImageView.setVisibility(View.VISIBLE);
                        }

                        break;
                    case Post.VIDEO_TYPE:
                        ((PostCompactBaseViewHolder) holder).typeTextView.setText(R.string.video);
                        if (mDataSavingMode && (mDisableImagePreview || mOnlyDisablePreviewInVideoAndGifPosts)) {
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.VISIBLE);
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                        } else {
                            ((PostCompactBaseViewHolder) holder).playButtonImageView.setVisibility(View.VISIBLE);
                        }

                        break;
                    case Post.NO_PREVIEW_LINK_TYPE:
                        ((PostCompactBaseViewHolder) holder).typeTextView.setText(R.string.link);

                        String noPreviewLinkUrl = post.getUrl();
                        ((PostCompactBaseViewHolder) holder).linkTextView.setVisibility(View.VISIBLE);
                        String noPreviewLinkDomain = Uri.parse(noPreviewLinkUrl).getHost();
                        ((PostCompactBaseViewHolder) holder).linkTextView.setText(noPreviewLinkDomain);
                        ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.VISIBLE);
                        ((PostCompactBaseViewHolder) holder).noPreviewPostImageView.setImageResource(R.drawable.ic_link);
                        break;
                    case Post.GALLERY_TYPE:
                        ((PostCompactBaseViewHolder) holder).typeTextView.setText(R.string.gallery);
                        if (mDataSavingMode && mDisableImagePreview) {
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.VISIBLE);
                            ((PostCompactBaseViewHolder) holder).noPreviewPostImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                        }
                        break;
                    case Post.TEXT_TYPE:
                        ((PostCompactBaseViewHolder) holder).typeTextView.setText(R.string.text);
                        break;
                }

                if (!mHideTheNumberOfComments) {
                    ((PostCompactBaseViewHolder) holder).commentsCountTextView.setVisibility(View.VISIBLE);
                    ((PostCompactBaseViewHolder) holder).commentsCountTextView.setText(Integer.toString(post.getNComments()));
                } else {
                    ((PostCompactBaseViewHolder) holder).commentsCountTextView.setVisibility(View.GONE);
                }

                if (post.isSaved()) {
                    ((PostCompactBaseViewHolder) holder).saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                } else {
                    ((PostCompactBaseViewHolder) holder).saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                }

                mCallback.currentlyBindItem(holder.getBindingAdapterPosition());
            }
        } else if (holder instanceof PostGalleryViewHolder) {
            Post post = getItem(position);
            if (post != null) {
                if (post.isRead()) {
                    if ((mHideReadPostsAutomatically && !post.isHiddenManuallyByUser()) || position < mHideReadPostsIndex) {
                        post.hidePostInRecyclerView();
                        holder.itemView.setVisibility(View.GONE);
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                        params.height = 0;
                        params.topMargin = 0;
                        params.bottomMargin = 0;
                        holder.itemView.setLayoutParams(params);
                        return;
                    }
                    holder.itemView.setBackgroundTintList(ColorStateList.valueOf(mReadPostCardViewBackgroundColor));
                    ((PostGalleryViewHolder) holder).titleTextView.setTextColor(mReadPostTitleColor);
                }

                switch (post.getPostType()) {
                    case Post.IMAGE_TYPE: {
                        ((PostGalleryViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.VISIBLE);

                        Post.Preview preview = getSuitablePreview(post.getPreviews());
                        if (preview != null) {
                            if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                                int height = (int) (400 * mScale);
                                ((PostGalleryViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ((PostGalleryViewHolder) holder).imageView.getLayoutParams().height = height;
                                preview.setPreviewWidth(mImageViewWidth);
                                preview.setPreviewHeight(height);
                            } else {
                                ((PostGalleryViewHolder) holder).imageView
                                        .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());
                            }
                            loadImage(holder, post, preview);
                        } else {
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                            if (post.getPostType() == Post.VIDEO_TYPE) {
                                ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                                ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                            } else if (post.getPostType() == Post.IMAGE_TYPE || post.getPostType() == Post.GIF_TYPE) {
                                ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                            } else if (post.getPostType() == Post.LINK_TYPE) {
                                ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_link);
                            } else if (post.getPostType() == Post.GALLERY_TYPE) {
                                ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                            }
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_image_24dp);
                        }
                        break;
                    }
                    case Post.GIF_TYPE: {
                        ((PostGalleryViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_circle_36dp));

                        Post.Preview preview = getSuitablePreview(post.getPreviews());
                        if (preview != null) {
                            if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                                int height = (int) (400 * mScale);
                                ((PostGalleryViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ((PostGalleryViewHolder) holder).imageView.getLayoutParams().height = height;
                                preview.setPreviewWidth(mImageViewWidth);
                                preview.setPreviewHeight(height);
                            } else {
                                ((PostGalleryViewHolder) holder).imageView
                                        .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());
                            }
                            loadImage(holder, post, preview);
                        } else {
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_image_24dp);
                        }
                        break;
                    }
                    case Post.VIDEO_TYPE: {
                        ((PostGalleryViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_play_circle_36dp));

                        Post.Preview preview = getSuitablePreview(post.getPreviews());
                        if (preview != null) {
                            if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                                int height = (int) (400 * mScale);
                                ((PostGalleryViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ((PostGalleryViewHolder) holder).imageView.getLayoutParams().height = height;
                                preview.setPreviewWidth(mImageViewWidth);
                                preview.setPreviewHeight(height);
                            } else {
                                ((PostGalleryViewHolder) holder).imageView
                                        .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());
                            }
                            loadImage(holder, post, preview);
                        } else {
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_outline_video_24dp);
                        }
                        break;
                    }
                    case Post.LINK_TYPE: {
                        ((PostGalleryViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_link_post_type_indicator));

                        Post.Preview preview = getSuitablePreview(post.getPreviews());
                        if (preview != null) {
                            if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                                int height = (int) (400 * mScale);
                                ((PostGalleryViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ((PostGalleryViewHolder) holder).imageView.getLayoutParams().height = height;
                                preview.setPreviewWidth(mImageViewWidth);
                                preview.setPreviewHeight(height);
                            } else {
                                ((PostGalleryViewHolder) holder).imageView
                                        .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());
                            }
                            loadImage(holder, post, preview);
                        } else {
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_link);
                        }
                        break;
                    }
                    case Post.NO_PREVIEW_LINK_TYPE: {
                        ((PostGalleryViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_link);
                        break;
                    }
                    case Post.TEXT_TYPE: {
                        ((PostGalleryViewHolder) holder).titleTextView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).titleTextView.setText(post.getTitle());
                        break;
                    }
                    case Post.GALLERY_TYPE: {
                        ((PostGalleryViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_gallery_reverse_color_24dp));

                        Post.Preview preview = getSuitablePreview(post.getPreviews());
                        if (preview != null) {
                            if (preview.getPreviewWidth() <= 0 || preview.getPreviewHeight() <= 0) {
                                int height = (int) (400 * mScale);
                                ((PostGalleryViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ((PostGalleryViewHolder) holder).imageView.getLayoutParams().height = height;
                                preview.setPreviewWidth(mImageViewWidth);
                                preview.setPreviewHeight(height);
                            } else {
                                ((PostGalleryViewHolder) holder).imageView
                                        .setRatio((float) preview.getPreviewHeight() / preview.getPreviewWidth());
                            }
                            loadImage(holder, post, preview);
                        } else {
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setVisibility(View.VISIBLE);
                            ((PostGalleryViewHolder) holder).noPreviewImageView.setImageResource(R.drawable.ic_gallery_reverse_color_24dp);
                        }
                        break;
                    }
                }
            }
        }
    }

    @Nullable
    private Post.Preview getSuitablePreview(ArrayList<Post.Preview> previews) {
        Post.Preview preview;
        if (!previews.isEmpty()) {
            int previewIndex;
            if (mDataSavingMode && previews.size() > 2) {
                previewIndex = previews.size() / 2;
            } else {
                previewIndex = 0;
            }
            preview = previews.get(previewIndex);
            if (preview.getPreviewWidth() * preview.getPreviewHeight() > 10_000_000) {
                for (int i = previews.size() - 1; i >= 1; i--) {
                    preview = previews.get(i);
                    if (mImageViewWidth >= preview.getPreviewWidth()) {
                        if (preview.getPreviewWidth() * preview.getPreviewHeight() <= 10_000_000) {
                            return preview;
                        }
                    } else {
                        int height = mImageViewWidth / preview.getPreviewWidth() * preview.getPreviewHeight();
                        if (mImageViewWidth * height <= 10_000_000) {
                            return preview;
                        }
                    }
                }
            }

            if (preview.getPreviewWidth() * preview.getPreviewHeight() > 10_000_000) {
                int divisor = 2;
                do {
                    preview.setPreviewWidth(preview.getPreviewWidth() / divisor);
                    preview.setPreviewHeight(preview.getPreviewHeight() / divisor);
                    divisor *= 2;
                } while (preview.getPreviewWidth() * preview.getPreviewHeight() > 10_000_000);
            }
            return preview;
        }

        return null;
    }

    private void loadImage(final RecyclerView.ViewHolder holder, final Post post, @NonNull Post.Preview preview) {
        if (holder instanceof PostWithPreviewTypeViewHolder) {
            String url;
            boolean blurImage = (post.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit()) && !(post.getPostType() == Post.GIF_TYPE && mAutoplayNsfwVideos)) || post.isSpoiler() && mNeedBlurSpoiler;
            if (post.getPostType() == Post.GIF_TYPE && mAutoplay && !blurImage) {
                url = post.getUrl();
            } else {
                url = preview.getPreviewUrl();
            }
            RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ((PostWithPreviewTypeViewHolder) holder).progressBar.setVisibility(View.GONE);
                    ((PostWithPreviewTypeViewHolder) holder).errorRelativeLayout.setVisibility(View.VISIBLE);
                    ((PostWithPreviewTypeViewHolder) holder).errorRelativeLayout.setOnClickListener(view -> {
                        ((PostWithPreviewTypeViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        ((PostWithPreviewTypeViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                        loadImage(holder, post, preview);
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ((PostWithPreviewTypeViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                    ((PostWithPreviewTypeViewHolder) holder).progressBar.setVisibility(View.GONE);
                    return false;
                }
            });

            if (blurImage) {
                imageRequestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                        .into(((PostWithPreviewTypeViewHolder) holder).imageView);
            } else {
                if (mImageViewWidth > preview.getPreviewWidth()) {
                    imageRequestBuilder.override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostWithPreviewTypeViewHolder) holder).imageView);
                } else {
                    imageRequestBuilder.into(((PostWithPreviewTypeViewHolder) holder).imageView);
                }
            }
        } else if (holder instanceof PostCompactBaseViewHolder) {
            String postCompactThumbnailPreviewUrl;
            ArrayList<Post.Preview> previews = post.getPreviews();
            if (previews != null && !previews.isEmpty()) {
                if (previews.size() >= 2) {
                    postCompactThumbnailPreviewUrl = previews.get(1).getPreviewUrl();
                } else {
                    postCompactThumbnailPreviewUrl = preview.getPreviewUrl();
                }

                RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(postCompactThumbnailPreviewUrl)
                        .error(R.drawable.ic_error_outline_black_24dp).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                ((PostCompactBaseViewHolder) holder).progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                ((PostCompactBaseViewHolder) holder).progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        });
                if ((post.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit())) || post.isSpoiler() && mNeedBlurSpoiler) {
                    imageRequestBuilder
                            .transform(new BlurTransformation(50, 2)).into(((PostCompactBaseViewHolder) holder).imageView);
                } else {
                    imageRequestBuilder.into(((PostCompactBaseViewHolder) holder).imageView);
                }
            }
        } else if (holder instanceof PostGalleryViewHolder) {
            String url;
            boolean blurImage = (post.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit()) && !(post.getPostType() == Post.GIF_TYPE && mAutoplayNsfwVideos)) || post.isSpoiler() && mNeedBlurSpoiler;
            if (post.getPostType() == Post.GIF_TYPE && mAutoplay && !blurImage) {
                url = post.getUrl();
            } else {
                url = preview.getPreviewUrl();
            }
            RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.GONE);
                    ((PostGalleryViewHolder) holder).errorRelativeLayout.setVisibility(View.VISIBLE);
                    ((PostGalleryViewHolder) holder).errorRelativeLayout.setOnClickListener(view -> {
                        ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        ((PostGalleryViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                        loadImage(holder, post, preview);
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ((PostGalleryViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                    ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.GONE);
                    return false;
                }
            });

            if (blurImage) {
                imageRequestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                        .into(((PostGalleryViewHolder) holder).imageView);
            } else {
                if (mImageViewWidth > preview.getPreviewWidth()) {
                    imageRequestBuilder.override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostGalleryViewHolder) holder).imageView);
                } else {
                    imageRequestBuilder.into(((PostGalleryViewHolder) holder).imageView);
                }
            }
        } else if (holder instanceof PostCard2WithPreviewViewHolder) {
            String url;
            boolean blurImage = (post.isNSFW() && mNeedBlurNsfw && !(mDoNotBlurNsfwInNsfwSubreddits && mFragment != null && mFragment.getIsNsfwSubreddit()) && !(post.getPostType() == Post.GIF_TYPE && mAutoplayNsfwVideos)) || post.isSpoiler() && mNeedBlurSpoiler;
            if (post.getPostType() == Post.GIF_TYPE && mAutoplay && !blurImage) {
                url = post.getUrl();
            } else {
                url = preview.getPreviewUrl();
            }
            RequestBuilder<Drawable> imageRequestBuilder = mGlide.load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.GONE);
                    ((PostCard2WithPreviewViewHolder) holder).errorRelativeLayout.setVisibility(View.VISIBLE);
                    ((PostCard2WithPreviewViewHolder) holder).errorRelativeLayout.setOnClickListener(view -> {
                        ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                        ((PostCard2WithPreviewViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                        loadImage(holder, post, preview);
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ((PostCard2WithPreviewViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                    ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.GONE);
                    return false;
                }
            });

            if (blurImage) {
                imageRequestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(50, 10)))
                        .into(((PostCard2WithPreviewViewHolder) holder).imageView);
            } else {
                if (mImageViewWidth > preview.getPreviewWidth()) {
                    imageRequestBuilder.override(preview.getPreviewWidth(), preview.getPreviewHeight()).into(((PostCard2WithPreviewViewHolder) holder).imageView);
                } else {
                    imageRequestBuilder.into(((PostCard2WithPreviewViewHolder) holder).imageView);
                }
            }
        }
    }

    private void shareLink(Post post) {
        Bundle bundle = new Bundle();
        bundle.putString(ShareLinkBottomSheetFragment.EXTRA_POST_LINK, post.getPermalink());
        if (post.getPostType() != Post.TEXT_TYPE) {
            bundle.putInt(ShareLinkBottomSheetFragment.EXTRA_MEDIA_TYPE, post.getPostType());
            switch (post.getPostType()) {
                case Post.IMAGE_TYPE:
                case Post.GIF_TYPE:
                case Post.LINK_TYPE:
                case Post.NO_PREVIEW_LINK_TYPE:
                    bundle.putString(ShareLinkBottomSheetFragment.EXTRA_MEDIA_LINK, post.getUrl());
                    break;
                case Post.VIDEO_TYPE:
                    bundle.putString(ShareLinkBottomSheetFragment.EXTRA_MEDIA_LINK, post.getVideoDownloadUrl());
                    break;
            }
        }
        ShareLinkBottomSheetFragment shareLinkBottomSheetFragment = new ShareLinkBottomSheetFragment();
        shareLinkBottomSheetFragment.setArguments(bundle);
        shareLinkBottomSheetFragment.show(mActivity.getSupportFragmentManager(), shareLinkBottomSheetFragment.getTag());
    }

    @Override
    public int getItemCount() {
        if (hasExtraRow()) {
            return super.getItemCount() + 1;
        }
        return super.getItemCount();
    }

    @Nullable
    public Post getItemByPosition(int position) {
        if (position >= 0 && super.getItemCount() > position) {
            return super.getItem(position);
        }

        return null;
    }

    public void setVoteButtonsPosition(boolean voteButtonsOnTheRight) {
        mVoteButtonsOnTheRight = voteButtonsOnTheRight;
    }

    public void setPostLayout(int postLayout) {
        mPostLayout = postLayout;
    }

    public void setBlurNsfwAndDoNotBlurNsfwInNsfwSubreddits(boolean needBlurNsfw, boolean doNotBlurNsfwInNsfwSubreddits) {
        mNeedBlurNsfw = needBlurNsfw;
        mDoNotBlurNsfwInNsfwSubreddits = doNotBlurNsfwInNsfwSubreddits;
    }

    public void setBlurSpoiler(boolean needBlurSpoiler) {
        mNeedBlurSpoiler = needBlurSpoiler;
    }

    public void setShowElapsedTime(boolean showElapsedTime) {
        mShowElapsedTime = showElapsedTime;
    }

    public void setTimeFormat(String timeFormat) {
        mTimeFormatPattern = timeFormat;
    }

    public void setShowDividerInCompactLayout(boolean showDividerInCompactLayout) {
        mShowDividerInCompactLayout = showDividerInCompactLayout;
    }

    public void setShowAbsoluteNumberOfVotes(boolean showAbsoluteNumberOfVotes) {
        mShowAbsoluteNumberOfVotes = showAbsoluteNumberOfVotes;
    }

    public int getHideReadPostsIndex() {
        return mHideReadPostsIndex;
    }

    public void setHideReadPostsIndex(int hideReadPostsIndex) {
        mHideReadPostsIndex = hideReadPostsIndex;
    }

    public void prepareToHideReadPosts() {
        mHideReadPostsIndex = getItemCount();
    }

    public int getNextItemPositionWithoutBeingHidden(int fromPosition) {
        int temp = fromPosition;
        while (temp >= 0 && temp < super.getItemCount()) {
            Post post = getItem(temp);
            if (post != null && post.isHiddenInRecyclerView()) {
                temp++;
            } else {
                break;
            }
        }

        return temp;
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState.getStatus() != NetworkState.Status.SUCCESS;
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount() - 1);
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (newExtraRow && !previousState.equals(newNetworkState)) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void removeFooter() {
        if (hasExtraRow()) {
            notifyItemRemoved(getItemCount() - 1);
        }

        networkState = null;
    }

    public void setAutoplay(boolean autoplay) {
        mAutoplay = autoplay;
    }

    public boolean isAutoplay() {
        return mAutoplay;
    }

    public void setAutoplayNsfwVideos(boolean autoplayNsfwVideos) {
        mAutoplayNsfwVideos = autoplayNsfwVideos;
    }

    public void setMuteAutoplayingVideos(boolean muteAutoplayingVideos) {
        mMuteAutoplayingVideos = muteAutoplayingVideos;
    }

    public void setShowThumbnailOnTheRightInCompactLayout(boolean showThumbnailOnTheRightInCompactLayout) {
        mShowThumbnailOnTheRightInCompactLayout = showThumbnailOnTheRightInCompactLayout;
    }

    public void setStartAutoplayVisibleAreaOffset(double startAutoplayVisibleAreaOffset) {
        this.mStartAutoplayVisibleAreaOffset = startAutoplayVisibleAreaOffset / 100.0;
    }

    public void setMuteNSFWVideo(boolean muteNSFWVideo) {
        this.mMuteNSFWVideo = muteNSFWVideo;
    }

    public void setLongPressToHideToolbarInCompactLayout(boolean longPressToHideToolbarInCompactLayout) {
        mLongPressToHideToolbarInCompactLayout = longPressToHideToolbarInCompactLayout;
    }

    public void setCompactLayoutToolbarHiddenByDefault(boolean compactLayoutToolbarHiddenByDefault) {
        mCompactLayoutToolbarHiddenByDefault = compactLayoutToolbarHiddenByDefault;
    }

    public void setDataSavingMode(boolean dataSavingMode) {
        mDataSavingMode = dataSavingMode;
    }

    public void setDisableImagePreview(boolean disableImagePreview) {
        mDisableImagePreview = disableImagePreview;
    }

    public void setOnlyDisablePreviewInVideoPosts(boolean onlyDisablePreviewInVideoAndGifPosts) {
        mOnlyDisablePreviewInVideoAndGifPosts = onlyDisablePreviewInVideoAndGifPosts;
    }

    public void setHidePostType(boolean hidePostType) {
        mHidePostType = hidePostType;
    }

    public void setHidePostFlair(boolean hidePostFlair) {
        mHidePostFlair = hidePostFlair;
    }

    public void setHideTheNumberOfAwards(boolean hideTheNumberOfAwards) {
        mHideTheNumberOfAwards = hideTheNumberOfAwards;
    }

    public void setHideSubredditAndUserPrefix(boolean hideSubredditAndUserPrefix) {
        mHideSubredditAndUserPrefix = hideSubredditAndUserPrefix;
    }

    public void setHideTheNumberOfVotes(boolean hideTheNumberOfVotes) {
        mHideTheNumberOfVotes = hideTheNumberOfVotes;
    }

    public void setHideTheNumberOfComments(boolean hideTheNumberOfComments) {
        mHideTheNumberOfComments = hideTheNumberOfComments;
    }

    public void setDefaultLinkPostLayout(int defaultLinkPostLayout) {
        mDefaultLinkPostLayout = defaultLinkPostLayout;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof PostBaseViewHolder) {
            if (mMarkPostsAsReadOnScroll) {
                int position = holder.getBindingAdapterPosition();
                if (position < super.getItemCount() && position >= 0) {
                    Post post = getItem(position);
                    ((PostBaseViewHolder) holder).markPostRead(post, false);
                }
            }
            ((PostBaseViewHolder) holder).itemView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (holder instanceof PostCard2VideoAutoplayViewHolder || holder instanceof PostCard2WithPreviewViewHolder) {
                int paddingPixel = (int) Utils.convertDpToPixel(16, mActivity);
                ((PostBaseViewHolder) holder).itemView.setPadding(0, paddingPixel, 0, 0);
            } else if (holder instanceof PostCard2TextTypeViewHolder) {
                int paddingPixel = (int) Utils.convertDpToPixel(12, mActivity);
                ((PostBaseViewHolder) holder).itemView.setPadding(0, paddingPixel, 0, 0);
            } else {
                int marginPixel = (int) Utils.convertDpToPixel(8, mActivity);
                params.topMargin = marginPixel;
                params.bottomMargin = marginPixel;
            }
            holder.itemView.setLayoutParams(params);
            if (((PostBaseViewHolder) holder).itemViewIsNotCardView) {
                ((PostBaseViewHolder) holder).itemView.setBackgroundColor(mCardViewBackgroundColor);
            } else {
                ((PostBaseViewHolder) holder).itemView.setBackgroundTintList(ColorStateList.valueOf(mCardViewBackgroundColor));
            }
            ((PostBaseViewHolder) holder).titleTextView.setTextColor(mPostTitleColor);
            if (holder instanceof PostVideoAutoplayViewHolder) {
                ((PostVideoAutoplayViewHolder) holder).mediaUri = null;
                if (((PostVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks != null) {
                    ((PostVideoAutoplayViewHolder) holder).fetchGfycatOrRedgifsVideoLinks.cancel();
                }
                ((PostVideoAutoplayViewHolder) holder).errorLoadingGfycatImageView.setVisibility(View.GONE);
                ((PostVideoAutoplayViewHolder) holder).muteButton.setVisibility(View.GONE);
                ((PostVideoAutoplayViewHolder) holder).resetVolume();
                mGlide.clear(((PostVideoAutoplayViewHolder) holder).previewImageView);
                ((PostVideoAutoplayViewHolder) holder).previewImageView.setVisibility(View.GONE);
            } else if (holder instanceof PostWithPreviewTypeViewHolder) {
                mGlide.clear(((PostWithPreviewTypeViewHolder) holder).imageView);
                ((PostWithPreviewTypeViewHolder) holder).imageWrapperRelativeLayout.setVisibility(View.GONE);
                ((PostWithPreviewTypeViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                ((PostWithPreviewTypeViewHolder) holder).noPreviewLinkImageView.setVisibility(View.GONE);
                ((PostWithPreviewTypeViewHolder) holder).progressBar.setVisibility(View.GONE);
                ((PostWithPreviewTypeViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                ((PostWithPreviewTypeViewHolder) holder).linkTextView.setVisibility(View.GONE);
            } else if (holder instanceof PostTextTypeViewHolder) {
                ((PostTextTypeViewHolder) holder).contentTextView.setText("");
                ((PostTextTypeViewHolder) holder).contentTextView.setTextColor(mPostContentColor);
                ((PostTextTypeViewHolder) holder).contentTextView.setVisibility(View.GONE);
            } else if (holder instanceof PostCard2WithPreviewViewHolder) {
                mGlide.clear(((PostCard2WithPreviewViewHolder) holder).imageView);
                ((PostCard2WithPreviewViewHolder) holder).imageView.setVisibility(View.GONE);
                ((PostCard2WithPreviewViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
                ((PostCard2WithPreviewViewHolder) holder).noPreviewImageView.setVisibility(View.GONE);
                ((PostCard2WithPreviewViewHolder) holder).progressBar.setVisibility(View.GONE);
                ((PostCard2WithPreviewViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
                ((PostCard2WithPreviewViewHolder) holder).linkTextView.setVisibility(View.GONE);
            } else if (holder instanceof PostCard2TextTypeViewHolder) {
                ((PostCard2TextTypeViewHolder) holder).contentTextView.setText("");
                ((PostCard2TextTypeViewHolder) holder).contentTextView.setTextColor(mPostContentColor);
                ((PostCard2TextTypeViewHolder) holder).contentTextView.setVisibility(View.GONE);
            }

            mGlide.clear(((PostBaseViewHolder) holder).iconGifImageView);
            ((PostBaseViewHolder) holder).stickiedPostImageView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).crosspostImageView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).archivedImageView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).lockedImageView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).nsfwTextView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).spoilerTextView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).flairTextView.setText("");
            ((PostBaseViewHolder) holder).flairTextView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).awardsTextView.setText("");
            ((PostBaseViewHolder) holder).awardsTextView.setVisibility(View.GONE);
            ((PostBaseViewHolder) holder).upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            ((PostBaseViewHolder) holder).scoreTextView.setTextColor(mPostIconAndInfoColor);
            ((PostBaseViewHolder) holder).downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
        } else if (holder instanceof PostCompactBaseViewHolder) {
            if (mMarkPostsAsReadOnScroll) {
                int position = holder.getBindingAdapterPosition();
                if (position < super.getItemCount() && position >= 0) {
                    Post post = getItem(position);
                    ((PostCompactBaseViewHolder) holder).markPostRead(post, false);
                }
            }
            ((PostCompactBaseViewHolder) holder).itemView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.itemView.setLayoutParams(params);
            ((PostCompactBaseViewHolder) holder).itemView.setBackgroundColor(mCardViewBackgroundColor);
            ((PostCompactBaseViewHolder) holder).titleTextView.setTextColor(mPostTitleColor);
            mGlide.clear(((PostCompactBaseViewHolder) holder).imageView);
            mGlide.clear(((PostCompactBaseViewHolder) holder).iconGifImageView);
            ((PostCompactBaseViewHolder) holder).stickiedPostImageView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).relativeLayout.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).crosspostImageView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).archivedImageView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).lockedImageView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).nsfwTextView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).spoilerTextView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).flairTextView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).flairTextView.setText("");
            ((PostCompactBaseViewHolder) holder).awardsTextView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).awardsTextView.setText("");
            ((PostCompactBaseViewHolder) holder).linkTextView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).progressBar.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).imageView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).playButtonImageView.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).noPreviewPostImageFrameLayout.setVisibility(View.GONE);
            ((PostCompactBaseViewHolder) holder).upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            ((PostCompactBaseViewHolder) holder).scoreTextView.setTextColor(mPostIconAndInfoColor);
            ((PostCompactBaseViewHolder) holder).downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
        } else if (holder instanceof PostGalleryViewHolder) {
            if (mMarkPostsAsReadOnScroll) {
                int position = holder.getBindingAdapterPosition();
                if (position < super.getItemCount() && position >= 0) {
                    Post post = getItem(position);
                    ((PostGalleryViewHolder) holder).markPostRead(post, false);
                }
            }
            ((PostGalleryViewHolder) holder).itemView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            int marginPixel = (int) Utils.convertDpToPixel(8, mActivity);
            params.topMargin = marginPixel;
            params.bottomMargin = marginPixel;
            holder.itemView.setLayoutParams(params);
            ((PostGalleryViewHolder) holder).itemView.setBackgroundTintList(ColorStateList.valueOf(mCardViewBackgroundColor));

            ((PostGalleryViewHolder) holder).titleTextView.setText("");
            ((PostGalleryViewHolder) holder).titleTextView.setVisibility(View.GONE);
            mGlide.clear(((PostGalleryViewHolder) holder).imageView);
            ((PostGalleryViewHolder) holder).imageView.setVisibility(View.GONE);
            ((PostGalleryViewHolder) holder).progressBar.setVisibility(View.GONE);
            ((PostGalleryViewHolder) holder).errorRelativeLayout.setVisibility(View.GONE);
            ((PostGalleryViewHolder) holder).videoOrGifIndicatorImageView.setVisibility(View.GONE);
            ((PostGalleryViewHolder) holder).noPreviewImageView.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public Object getKeyForOrder(int order) {
        if (super.getItemCount() <= 0 || order >= super.getItemCount()) {
            return null;
        }
        return order;
    }

    @Nullable
    @Override
    public Integer getOrderForKey(@NonNull Object key) {
        if (key instanceof Integer) {
            return (Integer) key;
        }

        return null;
    }

    public void onItemSwipe(RecyclerView.ViewHolder viewHolder, int direction, int swipeLeftAction, int swipeRightAction) {
        if (viewHolder instanceof PostBaseViewHolder) {
            if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.START) {
                if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((PostBaseViewHolder) viewHolder).upvoteButton.performClick();
                } else if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((PostBaseViewHolder) viewHolder).downvoteButton.performClick();
                }
            } else {
                if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((PostBaseViewHolder) viewHolder).upvoteButton.performClick();
                } else if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((PostBaseViewHolder) viewHolder).downvoteButton.performClick();
                }
            }
        } else if (viewHolder instanceof PostCompactBaseViewHolder) {
            if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.START) {
                if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((PostCompactBaseViewHolder) viewHolder).upvoteButton.performClick();
                } else if (swipeLeftAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((PostCompactBaseViewHolder) viewHolder).downvoteButton.performClick();
                }
            } else {
                if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_UPVOTE) {
                    ((PostCompactBaseViewHolder) viewHolder).upvoteButton.performClick();
                } else if (swipeRightAction == SharedPreferencesUtils.SWIPE_ACITON_DOWNVOTE) {
                    ((PostCompactBaseViewHolder) viewHolder).downvoteButton.performClick();
                }
            }
        }
    }

    public interface Callback {
        void retryLoadingMore();

        void typeChipClicked(int filter);

        void flairChipClicked(String flair);

        void nsfwChipClicked();

        void currentlyBindItem(int position);

        void delayTransition();
    }

    private void openViewPostDetailActivity(Post post, int position) {
        Intent intent = new Intent(mActivity, RedditViewPostDetailActivity.class);
        intent.putExtra(RedditViewPostDetailActivity.EXTRA_POST_DATA, post);
        intent.putExtra(RedditViewPostDetailActivity.EXTRA_POST_LIST_POSITION, position);
        intent.putExtra(RedditViewPostDetailActivity.EXTRA_POST_FRAGMENT_ID, mFragment.getPostFragmentId());
        intent.putExtra(RedditViewPostDetailActivity.EXTRA_IS_NSFW_SUBREDDIT, mFragment.getIsNsfwSubreddit());
        mActivity.startActivity(intent);
    }

    private void openMedia(Post post) {
        if (post.getPostType() == Post.VIDEO_TYPE) {
            Intent intent = new Intent(mActivity, RedditViewVideoActivity.class);
            if (post.isGfycat()) {
                intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_GFYCAT);
                intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, post.getGfycatId());
            } else if (post.isRedgifs()) {
                intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_REDGIFS);
                intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, post.getGfycatId());
            } else {
                intent.setData(Uri.parse(post.getVideoUrl()));
                intent.putExtra(RedditViewVideoActivity.EXTRA_SUBREDDIT, post.getSubredditName());
                intent.putExtra(RedditViewVideoActivity.EXTRA_ID, post.getId());
                intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, post.getVideoDownloadUrl());
            }
            intent.putExtra(RedditViewVideoActivity.EXTRA_POST_TITLE, post.getTitle());
            intent.putExtra(RedditViewVideoActivity.EXTRA_IS_NSFW, post.isNSFW());
            mActivity.startActivity(intent);
        } else if (post.getPostType() == Post.IMAGE_TYPE) {
            Intent intent = new Intent(mActivity, ViewImageOrGifActivity.class);
            intent.putExtra(ViewImageOrGifActivity.EXTRA_IMAGE_URL_KEY, post.getUrl());
            intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, post.getSubredditName()
                    + "-" + post.getId() + ".jpg");
            intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, post.getTitle());
            intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, post.getSubredditName());
            mActivity.startActivity(intent);
        } else if (post.getPostType() == Post.GIF_TYPE){
            Intent intent = new Intent(mActivity, ViewImageOrGifActivity.class);
            intent.putExtra(ViewImageOrGifActivity.EXTRA_FILE_NAME_KEY, post.getSubredditName()
                    + "-" + post.getId() + ".gif");
            intent.putExtra(ViewImageOrGifActivity.EXTRA_GIF_URL_KEY, post.getVideoUrl());
            intent.putExtra(ViewImageOrGifActivity.EXTRA_POST_TITLE_KEY, post.getTitle());
            intent.putExtra(ViewImageOrGifActivity.EXTRA_SUBREDDIT_OR_USERNAME_KEY, post.getSubredditName());
            mActivity.startActivity(intent);
        } else if (post.getPostType() == Post.LINK_TYPE || post.getPostType() == Post.NO_PREVIEW_LINK_TYPE) {
            Intent intent = new Intent(mActivity, RedditLinkResolverActivity.class);
            Uri uri = Uri.parse(post.getUrl());
            intent.setData(uri);
            intent.putExtra(RedditLinkResolverActivity.EXTRA_IS_NSFW, post.isNSFW());
            mActivity.startActivity(intent);
        } else if (post.getPostType() == Post.GALLERY_TYPE) {
            Intent intent = new Intent(mActivity, RedditViewRedditGalleryActivity.class);
            intent.putParcelableArrayListExtra(RedditViewRedditGalleryActivity.EXTRA_REDDIT_GALLERY, post.getGallery());
            intent.putExtra(RedditViewRedditGalleryActivity.EXTRA_SUBREDDIT_NAME, post.getSubredditName());
            mActivity.startActivity(intent);
        }
    }

    public class PostBaseViewHolder extends RecyclerView.ViewHolder {
        AspectRatioGifImageView iconGifImageView;
        TextView subredditTextView;
        TextView userTextView;
        ImageView stickiedPostImageView;
        TextView postTimeTextView;
        TextView titleTextView;
        CustomTextView typeTextView;
        ImageView archivedImageView;
        ImageView lockedImageView;
        ImageView crosspostImageView;
        CustomTextView nsfwTextView;
        CustomTextView spoilerTextView;
        CustomTextView flairTextView;
        CustomTextView awardsTextView;
        ConstraintLayout bottomConstraintLayout;
        ImageView upvoteButton;
        TextView scoreTextView;
        ImageView downvoteButton;
        TextView commentsCountTextView;
        ImageView saveButton;
        ImageView shareButton;

        boolean itemViewIsNotCardView = false;

        PostBaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setBaseView(AspectRatioGifImageView iconGifImageView,
                         TextView subredditTextView,
                         TextView userTextView,
                         ImageView stickiedPostImageView,
                         TextView postTimeTextView,
                         TextView titleTextView,
                         CustomTextView typeTextView,
                         ImageView archivedImageView,
                         ImageView lockedImageView,
                         ImageView crosspostImageView,
                         CustomTextView nsfwTextView,
                         CustomTextView spoilerTextView,
                         CustomTextView flairTextView,
                         CustomTextView awardsTextView,
                         ConstraintLayout bottomConstraintLayout,
                         ImageView upvoteButton,
                         TextView scoreTextView,
                         ImageView downvoteButton,
                         TextView commentsCountTextView,
                         ImageView saveButton,
                         ImageView shareButton) {
            this.iconGifImageView = iconGifImageView;
            this.subredditTextView = subredditTextView;
            this.userTextView = userTextView;
            this.stickiedPostImageView = stickiedPostImageView;
            this.postTimeTextView = postTimeTextView;
            this.titleTextView = titleTextView;
            this.typeTextView = typeTextView;
            this.archivedImageView = archivedImageView;
            this.lockedImageView = lockedImageView;
            this.crosspostImageView = crosspostImageView;
            this.nsfwTextView = nsfwTextView;
            this.spoilerTextView = spoilerTextView;
            this.flairTextView = flairTextView;
            this.awardsTextView = awardsTextView;
            this.bottomConstraintLayout = bottomConstraintLayout;
            this.upvoteButton = upvoteButton;
            this.scoreTextView = scoreTextView;
            this.downvoteButton = downvoteButton;
            this.commentsCountTextView = commentsCountTextView;
            this.saveButton = saveButton;
            this.shareButton = shareButton;

            scoreTextView.setOnClickListener(null);

            if (mVoteButtonsOnTheRight) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(bottomConstraintLayout);
                constraintSet.clear(upvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(scoreTextView.getId(), ConstraintSet.START);
                constraintSet.clear(downvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(saveButton.getId(), ConstraintSet.END);
                constraintSet.clear(shareButton.getId(), ConstraintSet.END);
                constraintSet.connect(upvoteButton.getId(), ConstraintSet.END, scoreTextView.getId(), ConstraintSet.START);
                constraintSet.connect(scoreTextView.getId(), ConstraintSet.END, downvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(downvoteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.connect(commentsCountTextView.getId(), ConstraintSet.START, saveButton.getId(), ConstraintSet.END);
                constraintSet.connect(commentsCountTextView.getId(), ConstraintSet.END, upvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(saveButton.getId(), ConstraintSet.START, shareButton.getId(), ConstraintSet.END);
                constraintSet.connect(shareButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.setHorizontalBias(commentsCountTextView.getId(), 0);
                constraintSet.applyTo(bottomConstraintLayout);
            }

            if (itemViewIsNotCardView) {
                itemView.setBackgroundColor(mCardViewBackgroundColor);
            } else {
                itemView.setBackgroundTintList(ColorStateList.valueOf(mCardViewBackgroundColor));
            }
            subredditTextView.setTextColor(mSubredditColor);
            userTextView.setTextColor(mUsernameColor);
            postTimeTextView.setTextColor(mSecondaryTextColor);
            titleTextView.setTextColor(mPostTitleColor);
            stickiedPostImageView.setColorFilter(mStickiedPostIconTint, PorterDuff.Mode.SRC_IN);
            typeTextView.setBackgroundColor(mPostTypeBackgroundColor);
            typeTextView.setBorderColor(mPostTypeBackgroundColor);
            typeTextView.setTextColor(mPostTypeTextColor);
            spoilerTextView.setBackgroundColor(mSpoilerBackgroundColor);
            spoilerTextView.setBorderColor(mSpoilerBackgroundColor);
            spoilerTextView.setTextColor(mSpoilerTextColor);
            nsfwTextView.setBackgroundColor(mNSFWBackgroundColor);
            nsfwTextView.setBorderColor(mNSFWBackgroundColor);
            nsfwTextView.setTextColor(mNSFWTextColor);
            flairTextView.setBackgroundColor(mFlairBackgroundColor);
            flairTextView.setBorderColor(mFlairBackgroundColor);
            flairTextView.setTextColor(mFlairTextColor);
            awardsTextView.setBackgroundColor(mAwardsBackgroundColor);
            awardsTextView.setBorderColor(mAwardsBackgroundColor);
            awardsTextView.setTextColor(mAwardsTextColor);
            archivedImageView.setColorFilter(mArchivedIconTint, PorterDuff.Mode.SRC_IN);
            lockedImageView.setColorFilter(mLockedIconTint, PorterDuff.Mode.SRC_IN);
            crosspostImageView.setColorFilter(mCrosspostIconTint, PorterDuff.Mode.SRC_IN);
            upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            scoreTextView.setTextColor(mPostIconAndInfoColor);
            downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            commentsCountTextView.setTextColor(mPostIconAndInfoColor);
            commentsCountTextView.setCompoundDrawablesWithIntrinsicBounds(mCommentIcon, null, null, null);
            saveButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            shareButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);

            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position >= 0 && canStartActivity) {
                    Post post = getItem(position);
                    if (post != null) {
                        markPostRead(post, true);
                        canStartActivity = false;

                        openViewPostDetailActivity(post, getBindingAdapterPosition());
                    }
                }
            });

            userTextView.setOnClickListener(view -> {
                if (canStartActivity) {
                    int position = getBindingAdapterPosition();
                    if (position < 0) {
                        return;
                    }
                    Post post = getItem(position);
                    if (post != null) {
                        canStartActivity = false;
                        Intent intent = new Intent(mActivity, RedditViewUserDetailActivity.class);
                        intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, post.getAuthor());
                        mActivity.startActivity(intent);
                    }
                }
            });

            if (mDisplaySubredditName) {
                subredditTextView.setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    if (position < 0) {
                        return;
                    }
                    Post post = getItem(position);
                    if (post != null) {
                        if (canStartActivity) {
                            canStartActivity = false;
                            if (post.getSubredditNamePrefixed().startsWith("u/")) {
                                Intent intent = new Intent(mActivity, RedditViewUserDetailActivity.class);
                                intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY,
                                        post.getSubredditNamePrefixed().substring(2));
                                mActivity.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mActivity, RedditViewSubredditDetailActivity.class);
                                intent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY,
                                        post.getSubredditName());
                                mActivity.startActivity(intent);
                            }
                        }
                    }
                });

                iconGifImageView.setOnClickListener(view -> subredditTextView.performClick());
            } else {
                subredditTextView.setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    if (position < 0) {
                        return;
                    }
                    Post post = getItem(position);
                    if (post != null) {
                        if (canStartActivity) {
                            canStartActivity = false;
                            if (post.getSubredditNamePrefixed().startsWith("u/")) {
                                Intent intent = new Intent(mActivity, RedditViewUserDetailActivity.class);
                                intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, post.getAuthor());
                                mActivity.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mActivity, RedditViewSubredditDetailActivity.class);
                                intent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY,
                                        post.getSubredditName());
                                mActivity.startActivity(intent);
                            }
                        }
                    }
                });

                iconGifImageView.setOnClickListener(view -> userTextView.performClick());
            }

            if (!(mActivity instanceof RedditFilteredPostsActivity)) {
                nsfwTextView.setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    if (position < 0) {
                        return;
                    }
                    Post post = getItem(position);
                    if (post != null) {
                        mCallback.nsfwChipClicked();
                    }
                });
                typeTextView.setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    if (position < 0) {
                        return;
                    }
                    Post post = getItem(position);
                    if (post != null) {
                        mCallback.typeChipClicked(post.getPostType());
                    }
                });

                flairTextView.setOnClickListener(view -> {
                    int position = getBindingAdapterPosition();
                    if (position < 0) {
                        return;
                    }
                    Post post = getItem(position);
                    if (post != null) {
                        mCallback.flairChipClicked(post.getFlair());
                    }
                });
            }

            upvoteButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    if (mAccessToken == null) {
                        Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mMarkPostsAsReadAfterVoting) {
                        markPostRead(post, true);
                    }

                    if (post.isArchived()) {
                        Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ColorFilter previousUpvoteButtonColorFilter = upvoteButton.getColorFilter();
                    ColorFilter previousDownvoteButtonColorFilter = downvoteButton.getColorFilter();
                    int previousScoreTextViewColor = scoreTextView.getCurrentTextColor();

                    int previousVoteType = post.getVoteType();
                    String newVoteType;

                    downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);

                    if (previousVoteType != 1) {
                        //Not upvoted before
                        post.setVoteType(1);
                        newVoteType = APIUtils.DIR_UPVOTE;
                        upvoteButton
                                .setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mUpvotedColor);
                    } else {
                        //Upvoted before
                        post.setVoteType(0);
                        newVoteType = APIUtils.DIR_UNVOTE;
                        upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mPostIconAndInfoColor);
                    }

                    if (!mHideTheNumberOfVotes) {
                        scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                    }

                    VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingListener() {
                        @Override
                        public void onVoteThingSuccess(int position1) {
                            int currentPosition = getBindingAdapterPosition();
                            if (newVoteType.equals(APIUtils.DIR_UPVOTE)) {
                                post.setVoteType(1);
                                if (currentPosition == position) {
                                    upvoteButton.setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mUpvotedColor);
                                }
                            } else {
                                post.setVoteType(0);
                                if (currentPosition == position) {
                                    upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mPostIconAndInfoColor);
                                }
                            }

                            if (currentPosition == position) {
                                downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                                }
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }

                        @Override
                        public void onVoteThingFail(int position1) {
                            Toast.makeText(mActivity, R.string.vote_failed, Toast.LENGTH_SHORT).show();
                            post.setVoteType(previousVoteType);
                            if (getBindingAdapterPosition() == position) {
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + previousVoteType));
                                }
                                upvoteButton.setColorFilter(previousUpvoteButtonColorFilter);
                                downvoteButton.setColorFilter(previousDownvoteButtonColorFilter);
                                scoreTextView.setTextColor(previousScoreTextViewColor);
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }
                    }, post.getFullName(), newVoteType, getBindingAdapterPosition());
                }
            });

            downvoteButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    if (mAccessToken == null) {
                        Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mMarkPostsAsReadAfterVoting) {
                        markPostRead(post, true);
                    }

                    if (post.isArchived()) {
                        Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ColorFilter previousUpvoteButtonColorFilter = upvoteButton.getColorFilter();
                    ColorFilter previousDownvoteButtonColorFilter = downvoteButton.getColorFilter();
                    int previousScoreTextViewColor = scoreTextView.getCurrentTextColor();

                    int previousVoteType = post.getVoteType();
                    String newVoteType;

                    upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);

                    if (previousVoteType != -1) {
                        //Not downvoted before
                        post.setVoteType(-1);
                        newVoteType = APIUtils.DIR_DOWNVOTE;
                        downvoteButton
                                .setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mDownvotedColor);
                    } else {
                        //Downvoted before
                        post.setVoteType(0);
                        newVoteType = APIUtils.DIR_UNVOTE;
                        downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mPostIconAndInfoColor);
                    }

                    if (!mHideTheNumberOfVotes) {
                        scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                    }

                    VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingListener() {
                        @Override
                        public void onVoteThingSuccess(int position1) {
                            int currentPosition = getBindingAdapterPosition();
                            if (newVoteType.equals(APIUtils.DIR_DOWNVOTE)) {
                                post.setVoteType(-1);
                                if (currentPosition == position) {
                                    downvoteButton.setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mDownvotedColor);
                                }
                            } else {
                                post.setVoteType(0);
                                if (currentPosition == position) {
                                    downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mPostIconAndInfoColor);
                                }
                            }

                            if (currentPosition == position) {
                                upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                                }
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }

                        @Override
                        public void onVoteThingFail(int position1) {
                            Toast.makeText(mActivity, R.string.vote_failed, Toast.LENGTH_SHORT).show();
                            post.setVoteType(previousVoteType);
                            if (getBindingAdapterPosition() == position) {
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + previousVoteType));
                                }
                                upvoteButton.setColorFilter(previousUpvoteButtonColorFilter);
                                downvoteButton.setColorFilter(previousDownvoteButtonColorFilter);
                                scoreTextView.setTextColor(previousScoreTextViewColor);
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }
                    }, post.getFullName(), newVoteType, getBindingAdapterPosition());
                }
            });

            saveButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    if (mAccessToken == null) {
                        Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (post.isSaved()) {
                        saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                        SaveThing.unsaveThing(mOauthRetrofit, mAccessToken, post.getFullName(),
                                new SaveThing.SaveThingListener() {
                                    @Override
                                    public void success() {
                                        post.setSaved(false);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_unsaved_success, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }

                                    @Override
                                    public void failed() {
                                        post.setSaved(true);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_unsaved_failed, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }
                                });
                    } else {
                        saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                        SaveThing.saveThing(mOauthRetrofit, mAccessToken, post.getFullName(),
                                new SaveThing.SaveThingListener() {
                                    @Override
                                    public void success() {
                                        post.setSaved(true);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_saved_success, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }

                                    @Override
                                    public void failed() {
                                        post.setSaved(false);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_saved_failed, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }
                                });
                    }
                }
            });

            shareButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    shareLink(post);
                }
            });
        }

        void setBaseView(AspectRatioGifImageView iconGifImageView,
                         TextView subredditTextView,
                         TextView userTextView,
                         ImageView stickiedPostImageView,
                         TextView postTimeTextView,
                         TextView titleTextView,
                         CustomTextView typeTextView,
                         ImageView archivedImageView,
                         ImageView lockedImageView,
                         ImageView crosspostImageView,
                         CustomTextView nsfwTextView,
                         CustomTextView spoilerTextView,
                         CustomTextView flairTextView,
                         CustomTextView awardsTextView,
                         ConstraintLayout bottomConstraintLayout,
                         ImageView upvoteButton,
                         TextView scoreTextView,
                         ImageView downvoteButton,
                         TextView commentsCountTextView,
                         ImageView saveButton,
                         ImageView shareButton, boolean itemViewIsNotCardView) {
            this.itemViewIsNotCardView = itemViewIsNotCardView;

            setBaseView(iconGifImageView, subredditTextView, userTextView, stickiedPostImageView, postTimeTextView,
                    titleTextView, typeTextView, archivedImageView, lockedImageView, crosspostImageView,
                    nsfwTextView, spoilerTextView, flairTextView, awardsTextView, bottomConstraintLayout,
                    upvoteButton, scoreTextView, downvoteButton, commentsCountTextView, saveButton, shareButton);
        }

        void markPostRead(Post post, boolean changePostItemColor) {
            if (mAccessToken != null && !post.isRead() && mMarkPostsAsRead) {
                post.markAsRead(true);
                if (changePostItemColor) {
                    if (itemViewIsNotCardView) {
                        itemView.setBackgroundColor(mReadPostCardViewBackgroundColor);
                    } else {
                        itemView.setBackgroundTintList(ColorStateList.valueOf(mReadPostCardViewBackgroundColor));
                    }
                    titleTextView.setTextColor(mReadPostTitleColor);
                    if (this instanceof PostTextTypeViewHolder) {
                        ((PostTextTypeViewHolder) this).contentTextView.setTextColor(mReadPostContentColor);
                    }
                }
                if (mActivity != null && mActivity instanceof MarkPostAsReadInterface) {
                    ((MarkPostAsReadInterface) mActivity).markPostAsRead(post);
                }
            }
        }
    }

    class PostVideoAutoplayViewHolder extends PostBaseViewHolder implements ToroPlayer {
        @BindView(R.id.icon_gif_image_view_item_post_video_type_autoplay)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.subreddit_name_text_view_item_post_video_type_autoplay)
        TextView subredditTextView;
        @BindView(R.id.user_text_view_item_post_video_type_autoplay)
        TextView userTextView;
        @BindView(R.id.stickied_post_image_view_item_post_video_type_autoplay)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_video_type_autoplay)
        TextView postTimeTextView;
        @BindView(R.id.title_text_view_item_post_video_type_autoplay)
        TextView titleTextView;
        @BindView(R.id.type_text_view_item_post_video_type_autoplay)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_video_type_autoplay)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_video_type_autoplay)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_video_type_autoplay)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_video_type_autoplay)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_video_type_autoplay)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_video_type_autoplay)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_video_type_autoplay)
        CustomTextView awardsTextView;
        @BindView(R.id.aspect_ratio_frame_layout_item_post_video_type_autoplay)
        AspectRatioFrameLayout aspectRatioFrameLayout;
        @BindView(R.id.preview_image_view_item_post_video_type_autoplay)
        GifImageView previewImageView;
        @BindView(R.id.error_loading_gfycat_image_view_item_post_video_type_autoplay)
        ImageView errorLoadingGfycatImageView;
        @BindView(R.id.player_view_item_post_video_type_autoplay)
        PlayerView videoPlayer;
        @BindView(R.id.mute_exo_playback_control_view)
        ImageView muteButton;
        @BindView(R.id.fullscreen_exo_playback_control_view)
        ImageView fullscreenButton;
        @BindView(R.id.bottom_constraint_layout_item_post_video_type_autoplay)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_video_type_autoplay)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_video_type_autoplay)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_video_type_autoplay)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_video_type_autoplay)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_video_type_autoplay)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_video_type_autoplay)
        ImageView shareButton;

        @Nullable
        ExoPlayerViewHelper helper;
        private Uri mediaUri;
        private float volume;
        public FetchGfycatOrRedgifsVideoLinks fetchGfycatOrRedgifsVideoLinks;

        PostVideoAutoplayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(
                    iconGifImageView,
                    subredditTextView,
                    userTextView,
                    stickiedPostImageView,
                    postTimeTextView,
                    titleTextView,
                    typeTextView,
                    archivedImageView,
                    lockedImageView,
                    crosspostImageView,
                    nsfwTextView,
                    spoilerTextView,
                    flairTextView,
                    awardsTextView,
                    bottomConstraintLayout,
                    upvoteButton,
                    scoreTextView,
                    downvoteButton,
                    commentsCountTextView,
                    saveButton,
                    shareButton);

            aspectRatioFrameLayout.setOnClickListener(null);

            muteButton.setOnClickListener(view -> {
                if (helper != null) {
                    if (helper.getVolume() != 0) {
                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_mute_white_rounded_24dp));
                        helper.setVolume(0f);
                        volume = 0f;
                        mFragment.videoAutoplayChangeMutingOption(true);
                    } else {
                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_unmute_white_rounded_24dp));
                        helper.setVolume(1f);
                        volume = 1f;
                        mFragment.videoAutoplayChangeMutingOption(false);
                    }
                }
            });

            fullscreenButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    markPostRead(post, true);
                    Intent intent = new Intent(mActivity, RedditViewVideoActivity.class);
                    if (post.isGfycat()) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_GFYCAT);
                        intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, post.getGfycatId());
                        if (post.isLoadGfyOrRedgifsVideoSuccess()) {
                            intent.setData(Uri.parse(post.getVideoUrl()));
                            intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, post.getVideoDownloadUrl());
                        }
                    } else if (post.isRedgifs()) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_REDGIFS);
                        intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, post.getGfycatId());
                        if (post.isLoadGfyOrRedgifsVideoSuccess()) {
                            intent.setData(Uri.parse(post.getVideoUrl()));
                            intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, post.getVideoDownloadUrl());
                        }
                    } else {
                        intent.setData(Uri.parse(post.getVideoUrl()));
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, post.getVideoDownloadUrl());
                        intent.putExtra(RedditViewVideoActivity.EXTRA_SUBREDDIT, post.getSubredditName());
                        intent.putExtra(RedditViewVideoActivity.EXTRA_ID, post.getId());
                    }
                    intent.putExtra(RedditViewVideoActivity.EXTRA_POST_TITLE, post.getTitle());
                    if (helper != null) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_PROGRESS_SECONDS, helper.getLatestPlaybackInfo().getResumePosition());
                    }
                    intent.putExtra(RedditViewVideoActivity.EXTRA_IS_NSFW, post.isNSFW());
                    mActivity.startActivity(intent);
                }
            });

            previewImageView.setOnLongClickListener(view -> fullscreenButton.performClick());
        }

        void bindVideoUri(Uri videoUri) {
            mediaUri = videoUri;
        }

        void setVolume(float volume) {
            this.volume = volume;
        }

        void resetVolume() {
            volume = 0f;
        }

        @NonNull
        @Override
        public View getPlayerView() {
            return videoPlayer;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null && mediaUri != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
            if (mediaUri == null) {
                return;
            }
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, mediaUri, null, mExoCreator);
                helper.addEventListener(new Playable.EventListener() {
                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        if (!trackGroups.isEmpty()) {
                            for (int i = 0; i < trackGroups.length; i++) {
                                String mimeType = trackGroups.get(i).getFormat(0).sampleMimeType;
                                if (mimeType != null && mimeType.contains("audio")) {
                                    if (mFragment.getMasterMutingOption() != null) {
                                        volume = mFragment.getMasterMutingOption() ? 0f : 1f;
                                    }
                                    helper.setVolume(volume);
                                    muteButton.setVisibility(View.VISIBLE);
                                    if (volume != 0f) {
                                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_unmute_white_rounded_24dp));
                                    } else {
                                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_mute_white_rounded_24dp));
                                    }
                                    break;
                                }
                            }
                        } else {
                            muteButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onMetadata(Metadata metadata) {

                    }

                    @Override
                    public void onCues(List<Cue> cues) {

                    }

                    @Override
                    public void onRenderedFirstFrame() {
                        mGlide.clear(previewImageView);
                        previewImageView.setVisibility(View.GONE);
                    }
                });
            }
            helper.initialize(container, playbackInfo);
        }

        @Override
        public void play() {
            if (helper != null && mediaUri != null) {
                helper.play();
            }
        }

        @Override
        public void pause() {
            if (helper != null) helper.pause();
        }

        @Override
        public boolean isPlaying() {
            return helper != null && helper.isPlaying();
        }

        @Override
        public void release() {
            if (helper != null) {
                helper.release();
                helper = null;
            }
        }

        @Override
        public boolean wantsToPlay() {
            return mediaUri != null && ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= mStartAutoplayVisibleAreaOffset;
        }

        @Override
        public int getPlayerOrder() {
            return getBindingAdapterPosition();
        }
    }

    class PostWithPreviewTypeViewHolder extends PostBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_with_preview)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.subreddit_name_text_view_item_post_with_preview)
        TextView subredditTextView;
        @BindView(R.id.user_text_view_item_post_with_preview)
        TextView userTextView;
        @BindView(R.id.stickied_post_image_view_item_post_with_preview)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_with_preview)
        TextView postTimeTextView;
        @BindView(R.id.title_text_view_item_post_with_preview)
        TextView titleTextView;
        @BindView(R.id.type_text_view_item_post_with_preview)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_with_preview)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_with_preview)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_with_preview)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_with_preview)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_with_preview)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_with_preview)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_with_preview)
        CustomTextView awardsTextView;
        @BindView(R.id.link_text_view_item_post_with_preview)
        TextView linkTextView;
        @BindView(R.id.video_or_gif_indicator_image_view_item_post_with_preview)
        ImageView videoOrGifIndicatorImageView;
        @BindView(R.id.image_wrapper_relative_layout_item_post_with_preview)
        RelativeLayout imageWrapperRelativeLayout;
        @BindView(R.id.progress_bar_item_post_with_preview)
        ProgressBar progressBar;
        @BindView(R.id.image_view_item_post_with_preview)
        AspectRatioGifImageView imageView;
        @BindView(R.id.load_image_error_relative_layout_item_post_with_preview)
        RelativeLayout errorRelativeLayout;
        @BindView(R.id.load_image_error_text_view_item_post_with_preview)
        TextView errorTextView;
        @BindView(R.id.image_view_no_preview_gallery_item_post_with_preview)
        ImageView noPreviewLinkImageView;
        @BindView(R.id.bottom_constraint_layout_item_post_with_preview)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_with_preview)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_with_preview)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_with_preview)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_with_preview)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_with_preview)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_with_preview)
        ImageView shareButton;

        PostWithPreviewTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(
                    iconGifImageView,
                    subredditTextView,
                    userTextView,
                    stickiedPostImageView,
                    postTimeTextView,
                    titleTextView,
                    typeTextView,
                    archivedImageView,
                    lockedImageView,
                    crosspostImageView,
                    nsfwTextView,
                    spoilerTextView,
                    flairTextView,
                    awardsTextView,
                    bottomConstraintLayout,
                    upvoteButton,
                    scoreTextView,
                    downvoteButton,
                    commentsCountTextView,
                    saveButton,
                    shareButton);

            linkTextView.setTextColor(mSecondaryTextColor);
            noPreviewLinkImageView.setBackgroundColor(mNoPreviewPostTypeBackgroundColor);
            noPreviewLinkImageView.setColorFilter(mNoPreviewPostTypeIconTint, PorterDuff.Mode.SRC_IN);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            errorTextView.setTextColor(mPrimaryTextColor);

            imageView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    markPostRead(post, true);
                    openMedia(post);
                }
            });

            noPreviewLinkImageView.setOnClickListener(view -> {
                imageView.performClick();
            });
        }
    }

    class PostTextTypeViewHolder extends PostBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_text_type)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.subreddit_name_text_view_item_post_text_type)
        TextView subredditTextView;
        @BindView(R.id.user_text_view_item_post_text_type)
        TextView userTextView;
        @BindView(R.id.stickied_post_image_view_item_post_text_type)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_text_type)
        TextView postTimeTextView;
        @BindView(R.id.title_text_view_item_post_text_type)
        TextView titleTextView;
        @BindView(R.id.type_text_view_item_post_text_type)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_text_type)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_text_type)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_text_type)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_text_type)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_text_type)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_text_type)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_text_type)
        CustomTextView awardsTextView;
        @BindView(R.id.content_text_view_item_post_text_type)
        TextView contentTextView;
        @BindView(R.id.bottom_constraint_layout_item_post_text_type)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_text_type)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_text_type)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_text_type)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_text_type)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_text_type)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_text_type)
        ImageView shareButton;

        PostTextTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(
                    iconGifImageView,
                    subredditTextView,
                    userTextView,
                    stickiedPostImageView,
                    postTimeTextView,
                    titleTextView,
                    typeTextView,
                    archivedImageView,
                    lockedImageView,
                    crosspostImageView,
                    nsfwTextView,
                    spoilerTextView,
                    flairTextView,
                    awardsTextView,
                    bottomConstraintLayout,
                    upvoteButton,
                    scoreTextView,
                    downvoteButton,
                    commentsCountTextView,
                    saveButton,
                    shareButton);

            contentTextView.setTextColor(mPostContentColor);
        }
    }

    public class PostCompactBaseViewHolder extends RecyclerView.ViewHolder {
        AspectRatioGifImageView iconGifImageView;
        TextView nameTextView;
        ImageView stickiedPostImageView;
        TextView postTimeTextView;
        ConstraintLayout titleAndImageConstraintLayout;
        TextView titleTextView;
        CustomTextView typeTextView;
        ImageView archivedImageView;
        ImageView lockedImageView;
        ImageView crosspostImageView;
        CustomTextView nsfwTextView;
        CustomTextView spoilerTextView;
        CustomTextView flairTextView;
        CustomTextView awardsTextView;
        TextView linkTextView;
        RelativeLayout relativeLayout;
        ProgressBar progressBar;
        ImageView imageView;
        ImageView playButtonImageView;
        FrameLayout noPreviewPostImageFrameLayout;
        ImageView noPreviewPostImageView;
        Barrier imageBarrier;
        ConstraintLayout bottomConstraintLayout;
        ImageView upvoteButton;
        TextView scoreTextView;
        ImageView downvoteButton;
        TextView commentsCountTextView;
        ImageView saveButton;
        ImageView shareButton;
        View divider;

        PostCompactBaseViewHolder(View itemView) {
            super(itemView);
        }

        void setBaseView(AspectRatioGifImageView iconGifImageView,
                                         TextView nameTextView, ImageView stickiedPostImageView,
                                         TextView postTimeTextView, ConstraintLayout titleAndImageConstraintLayout,
                                         TextView titleTextView, CustomTextView typeTextView,
                                         ImageView archivedImageView, ImageView lockedImageView,
                                         ImageView crosspostImageView, CustomTextView nsfwTextView,
                                         CustomTextView spoilerTextView, CustomTextView flairTextView,
                                         CustomTextView awardsTextView, TextView linkTextView,
                                         RelativeLayout relativeLayout, ProgressBar progressBar,
                                         ImageView imageView, ImageView playButtonImageView,
                                         FrameLayout noPreviewLinkImageFrameLayout,
                                         ImageView noPreviewLinkImageView, Barrier imageBarrier,
                                         ConstraintLayout bottomConstraintLayout, ImageView upvoteButton,
                                         TextView scoreTextView, ImageView downvoteButton,
                                         TextView commentsCountTextView, ImageView saveButton,
                                         ImageView shareButton, View divider) {
            this.iconGifImageView = iconGifImageView;
            this.nameTextView = nameTextView;
            this.stickiedPostImageView = stickiedPostImageView;
            this.postTimeTextView = postTimeTextView;
            this.titleAndImageConstraintLayout = titleAndImageConstraintLayout;
            this.titleTextView = titleTextView;
            this.typeTextView = typeTextView;
            this.archivedImageView = archivedImageView;
            this.lockedImageView = lockedImageView;
            this.crosspostImageView = crosspostImageView;
            this.nsfwTextView = nsfwTextView;
            this.spoilerTextView = spoilerTextView;
            this.flairTextView = flairTextView;
            this.awardsTextView = awardsTextView;
            this.linkTextView = linkTextView;
            this.relativeLayout = relativeLayout;
            this.progressBar = progressBar;
            this.imageView = imageView;
            this.playButtonImageView = playButtonImageView;
            this.noPreviewPostImageFrameLayout = noPreviewLinkImageFrameLayout;
            this.noPreviewPostImageView = noPreviewLinkImageView;
            this.imageBarrier = imageBarrier;
            this.bottomConstraintLayout = bottomConstraintLayout;
            this.upvoteButton = upvoteButton;
            this.scoreTextView = scoreTextView;
            this.downvoteButton = downvoteButton;
            this.commentsCountTextView = commentsCountTextView;
            this.saveButton = saveButton;
            this.shareButton = shareButton;
            this.divider = divider;

            scoreTextView.setOnClickListener(null);

            if (mVoteButtonsOnTheRight) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(bottomConstraintLayout);
                constraintSet.clear(upvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(scoreTextView.getId(), ConstraintSet.START);
                constraintSet.clear(downvoteButton.getId(), ConstraintSet.START);
                constraintSet.clear(saveButton.getId(), ConstraintSet.END);
                constraintSet.clear(shareButton.getId(), ConstraintSet.END);
                constraintSet.connect(upvoteButton.getId(), ConstraintSet.END, scoreTextView.getId(), ConstraintSet.START);
                constraintSet.connect(scoreTextView.getId(), ConstraintSet.END, downvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(downvoteButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
                constraintSet.connect(commentsCountTextView.getId(), ConstraintSet.START, saveButton.getId(), ConstraintSet.END);
                constraintSet.connect(commentsCountTextView.getId(), ConstraintSet.END, upvoteButton.getId(), ConstraintSet.START);
                constraintSet.connect(saveButton.getId(), ConstraintSet.START, shareButton.getId(), ConstraintSet.END);
                constraintSet.connect(shareButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
                constraintSet.setHorizontalBias(commentsCountTextView.getId(), 0);
                constraintSet.applyTo(bottomConstraintLayout);
            }

            itemView.setBackgroundColor(mCardViewBackgroundColor);
            postTimeTextView.setTextColor(mSecondaryTextColor);
            titleTextView.setTextColor(mPostTitleColor);
            stickiedPostImageView.setColorFilter(mStickiedPostIconTint, PorterDuff.Mode.SRC_IN);
            typeTextView.setBackgroundColor(mPostTypeBackgroundColor);
            typeTextView.setBorderColor(mPostTypeBackgroundColor);
            typeTextView.setTextColor(mPostTypeTextColor);
            spoilerTextView.setBackgroundColor(mSpoilerBackgroundColor);
            spoilerTextView.setBorderColor(mSpoilerBackgroundColor);
            spoilerTextView.setTextColor(mSpoilerTextColor);
            nsfwTextView.setBackgroundColor(mNSFWBackgroundColor);
            nsfwTextView.setBorderColor(mNSFWBackgroundColor);
            nsfwTextView.setTextColor(mNSFWTextColor);
            flairTextView.setBackgroundColor(mFlairBackgroundColor);
            flairTextView.setBorderColor(mFlairBackgroundColor);
            flairTextView.setTextColor(mFlairTextColor);
            awardsTextView.setBackgroundColor(mAwardsBackgroundColor);
            awardsTextView.setBorderColor(mAwardsBackgroundColor);
            awardsTextView.setTextColor(mAwardsTextColor);
            archivedImageView.setColorFilter(mArchivedIconTint, PorterDuff.Mode.SRC_IN);
            lockedImageView.setColorFilter(mLockedIconTint, PorterDuff.Mode.SRC_IN);
            crosspostImageView.setColorFilter(mCrosspostIconTint, PorterDuff.Mode.SRC_IN);
            linkTextView.setTextColor(mSecondaryTextColor);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            noPreviewLinkImageView.setBackgroundColor(mNoPreviewPostTypeBackgroundColor);
            noPreviewLinkImageView.setColorFilter(mNoPreviewPostTypeIconTint, PorterDuff.Mode.SRC_IN);
            upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            scoreTextView.setTextColor(mPostIconAndInfoColor);
            downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            commentsCountTextView.setTextColor(mPostIconAndInfoColor);
            commentsCountTextView.setCompoundDrawablesWithIntrinsicBounds(mCommentIcon, null, null, null);
            saveButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            shareButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
            divider.setBackgroundColor(mDividerColor);

            imageView.setClipToOutline(true);
            noPreviewLinkImageFrameLayout.setClipToOutline(true);

            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null && canStartActivity) {
                    markPostRead(post, true);
                    canStartActivity = false;

                    openViewPostDetailActivity(post, getBindingAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(view -> {
                if (mLongPressToHideToolbarInCompactLayout) {
                    if (bottomConstraintLayout.getLayoutParams().height == 0) {
                        ViewGroup.LayoutParams params = (LinearLayout.LayoutParams) bottomConstraintLayout.getLayoutParams();
                        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        bottomConstraintLayout.setLayoutParams(params);
                        mCallback.delayTransition();
                    } else {
                        mCallback.delayTransition();
                        ViewGroup.LayoutParams params = bottomConstraintLayout.getLayoutParams();
                        params.height = 0;
                        bottomConstraintLayout.setLayoutParams(params);
                    }
                }
                return true;
            });

            nameTextView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null && canStartActivity) {
                    canStartActivity = false;
                    if (mDisplaySubredditName) {
                        if (post.getSubredditNamePrefixed().startsWith("u/")) {
                            Intent intent = new Intent(mActivity, RedditViewUserDetailActivity.class);
                            intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY,
                                    post.getSubredditNamePrefixed().substring(2));
                            mActivity.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mActivity, RedditViewSubredditDetailActivity.class);
                            intent.putExtra(RedditViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY,
                                    post.getSubredditNamePrefixed().substring(2));
                            mActivity.startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(mActivity, RedditViewUserDetailActivity.class);
                        intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, post.getAuthor());
                        mActivity.startActivity(intent);
                    }
                }
            });

            iconGifImageView.setOnClickListener(view -> nameTextView.performClick());

            nsfwTextView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null && !(mActivity instanceof RedditFilteredPostsActivity)) {
                    mCallback.nsfwChipClicked();
                }
            });

            typeTextView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null && !(mActivity instanceof RedditFilteredPostsActivity)) {
                    mCallback.typeChipClicked(post.getPostType());
                }
            });

            flairTextView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null && !(mActivity instanceof RedditFilteredPostsActivity)) {
                    mCallback.flairChipClicked(post.getFlair());
                }
            });

            imageView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    markPostRead(post, true);
                    openMedia(post);
                }
            });

            noPreviewLinkImageFrameLayout.setOnClickListener(view -> {
                imageView.performClick();
            });

            upvoteButton.setOnClickListener(view -> {
                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    if (mMarkPostsAsReadAfterVoting) {
                        markPostRead(post, true);
                    }

                    if (post.isArchived()) {
                        Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ColorFilter previousUpvoteButtonColorFilter = upvoteButton.getColorFilter();
                    ColorFilter previousDownvoteButtonColorFilter = downvoteButton.getColorFilter();
                    int previousScoreTextViewColor = scoreTextView.getCurrentTextColor();

                    int previousVoteType = post.getVoteType();
                    String newVoteType;

                    downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);

                    if (previousVoteType != 1) {
                        //Not upvoted before
                        post.setVoteType(1);
                        newVoteType = APIUtils.DIR_UPVOTE;
                        upvoteButton
                                .setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mUpvotedColor);
                    } else {
                        //Upvoted before
                        post.setVoteType(0);
                        newVoteType = APIUtils.DIR_UNVOTE;
                        upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mPostIconAndInfoColor);
                    }

                    if (!mHideTheNumberOfVotes) {
                        scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                    }

                    VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingListener() {
                        @Override
                        public void onVoteThingSuccess(int position1) {
                            int currentPosition = getBindingAdapterPosition();
                            if (newVoteType.equals(APIUtils.DIR_UPVOTE)) {
                                post.setVoteType(1);
                                if (currentPosition == position) {
                                    upvoteButton.setColorFilter(mUpvotedColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mUpvotedColor);
                                }
                            } else {
                                post.setVoteType(0);
                                if (currentPosition == position) {
                                    upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mPostIconAndInfoColor);
                                }
                            }

                            if (currentPosition == position) {
                                downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                                }
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }

                        @Override
                        public void onVoteThingFail(int position1) {
                            Toast.makeText(mActivity, R.string.vote_failed, Toast.LENGTH_SHORT).show();
                            post.setVoteType(previousVoteType);
                            if (getBindingAdapterPosition() == position) {
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + previousVoteType));
                                }
                                upvoteButton.setColorFilter(previousUpvoteButtonColorFilter);
                                downvoteButton.setColorFilter(previousDownvoteButtonColorFilter);
                                scoreTextView.setTextColor(previousScoreTextViewColor);
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }
                    }, post.getFullName(), newVoteType, getBindingAdapterPosition());
                }
            });

            downvoteButton.setOnClickListener(view -> {
                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    if (mMarkPostsAsReadAfterVoting) {
                        markPostRead(post, true);
                    }

                    if (post.isArchived()) {
                        Toast.makeText(mActivity, R.string.archived_post_vote_unavailable, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ColorFilter previousUpvoteButtonColorFilter = upvoteButton.getColorFilter();
                    ColorFilter previousDownvoteButtonColorFilter = downvoteButton.getColorFilter();
                    int previousScoreTextViewColor = scoreTextView.getCurrentTextColor();

                    int previousVoteType = post.getVoteType();
                    String newVoteType;

                    upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);

                    if (previousVoteType != -1) {
                        //Not downvoted before
                        post.setVoteType(-1);
                        newVoteType = APIUtils.DIR_DOWNVOTE;
                        downvoteButton
                                .setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mDownvotedColor);
                    } else {
                        //Downvoted before
                        post.setVoteType(0);
                        newVoteType = APIUtils.DIR_UNVOTE;
                        downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                        scoreTextView.setTextColor(mPostIconAndInfoColor);
                    }

                    if (!mHideTheNumberOfVotes) {
                        scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                    }

                    VoteThing.voteThing(mActivity, mOauthRetrofit, mAccessToken, new VoteThing.VoteThingListener() {
                        @Override
                        public void onVoteThingSuccess(int position1) {
                            int currentPosition = getBindingAdapterPosition();
                            if (newVoteType.equals(APIUtils.DIR_DOWNVOTE)) {
                                post.setVoteType(-1);
                                if (currentPosition == position) {
                                    downvoteButton.setColorFilter(mDownvotedColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mDownvotedColor);
                                }

                            } else {
                                post.setVoteType(0);
                                if (currentPosition == position) {
                                    downvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                    scoreTextView.setTextColor(mPostIconAndInfoColor);
                                }
                            }

                            if (currentPosition == position) {
                                upvoteButton.setColorFilter(mPostIconAndInfoColor, PorterDuff.Mode.SRC_IN);
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + post.getVoteType()));
                                }
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }

                        @Override
                        public void onVoteThingFail(int position1) {
                            Toast.makeText(mActivity, R.string.vote_failed, Toast.LENGTH_SHORT).show();
                            post.setVoteType(previousVoteType);
                            if (getBindingAdapterPosition() == position) {
                                if (!mHideTheNumberOfVotes) {
                                    scoreTextView.setText(Utils.getNVotes(mShowAbsoluteNumberOfVotes, post.getScore() + previousVoteType));
                                }
                                upvoteButton.setColorFilter(previousUpvoteButtonColorFilter);
                                downvoteButton.setColorFilter(previousDownvoteButtonColorFilter);
                                scoreTextView.setTextColor(previousScoreTextViewColor);
                            }

                            EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                        }
                    }, post.getFullName(), newVoteType, getBindingAdapterPosition());
                }
            });

            saveButton.setOnClickListener(view -> {
                if (mAccessToken == null) {
                    Toast.makeText(mActivity, R.string.login_first, Toast.LENGTH_SHORT).show();
                    return;
                }

                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    if (post.isSaved()) {
                        saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                        SaveThing.unsaveThing(mOauthRetrofit, mAccessToken, post.getFullName(),
                                new SaveThing.SaveThingListener() {
                                    @Override
                                    public void success() {
                                        post.setSaved(false);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_unsaved_success, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }

                                    @Override
                                    public void failed() {
                                        post.setSaved(true);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_unsaved_failed, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }
                                });
                    } else {
                        saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                        SaveThing.saveThing(mOauthRetrofit, mAccessToken, post.getFullName(),
                                new SaveThing.SaveThingListener() {
                                    @Override
                                    public void success() {
                                        post.setSaved(true);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_saved_success, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }

                                    @Override
                                    public void failed() {
                                        post.setSaved(false);
                                        if (getBindingAdapterPosition() == position) {
                                            saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                                        }
                                        Toast.makeText(mActivity, R.string.post_saved_failed, Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().post(new PostUpdateEventToPostDetailFragment(post));
                                    }
                                });
                    }
                }
            });

            shareButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    shareLink(post);
                }
            });
        }

        void markPostRead(Post post, boolean changePostItemColor) {
            if (mAccessToken != null && !post.isRead() && mMarkPostsAsRead) {
                post.markAsRead(true);
                if (changePostItemColor) {
                    itemView.setBackgroundColor(mReadPostCardViewBackgroundColor);
                    titleTextView.setTextColor(mReadPostTitleColor);
                }
                if (mActivity != null && mActivity instanceof MarkPostAsReadInterface) {
                    ((MarkPostAsReadInterface) mActivity).markPostAsRead(post);
                }
            }
        }
    }

    class PostCompactLeftThumbnailViewHolder extends PostCompactBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_compact)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.name_text_view_item_post_compact)
        TextView nameTextView;
        @BindView(R.id.stickied_post_image_view_item_post_compact)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_compact)
        TextView postTimeTextView;
        @BindView(R.id.title_and_image_constraint_layout)
        ConstraintLayout titleAndImageConstraintLayout;
        @BindView(R.id.title_text_view_item_post_compact)
        TextView titleTextView;
        @BindView(R.id.type_text_view_item_post_compact)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_compact)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_compact)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_compact)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_compact)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_compact)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_compact)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_compact)
        CustomTextView awardsTextView;
        @BindView(R.id.link_text_view_item_post_compact)
        TextView linkTextView;
        @BindView(R.id.image_view_wrapper_item_post_compact)
        RelativeLayout relativeLayout;
        @BindView(R.id.progress_bar_item_post_compact)
        ProgressBar progressBar;
        @BindView(R.id.image_view_item_post_compact)
        ImageView imageView;
        @BindView(R.id.play_button_image_view_item_post_compact)
        ImageView playButtonImageView;
        @BindView(R.id.frame_layout_image_view_no_preview_link_item_post_compact)
        FrameLayout noPreviewLinkImageFrameLayout;
        @BindView(R.id.image_view_no_preview_link_item_post_compact)
        ImageView noPreviewLinkImageView;
        @BindView(R.id.barrier2)
        Barrier imageBarrier;
        @BindView(R.id.bottom_constraint_layout_item_post_compact)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_compact)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_compact)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_compact)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_compact)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_compact)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_compact)
        ImageView shareButton;
        @BindView(R.id.divider_item_post_compact)
        View divider;

        PostCompactLeftThumbnailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            setBaseView(iconGifImageView, nameTextView, stickiedPostImageView, postTimeTextView,
                    titleAndImageConstraintLayout, titleTextView, typeTextView, archivedImageView,
                    lockedImageView, crosspostImageView, nsfwTextView, spoilerTextView,
                    flairTextView, awardsTextView, linkTextView, relativeLayout, progressBar, imageView,
                    playButtonImageView, noPreviewLinkImageFrameLayout, noPreviewLinkImageView,
                    imageBarrier, bottomConstraintLayout, upvoteButton, scoreTextView, downvoteButton,
                    commentsCountTextView, saveButton, shareButton, divider);
        }
    }

    class PostCompactRightThumbnailViewHolder extends PostCompactBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_compact_right_thumbnail)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.name_text_view_item_post_compact_right_thumbnail)
        TextView nameTextView;
        @BindView(R.id.stickied_post_image_view_item_post_compact_right_thumbnail)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_compact_right_thumbnail)
        TextView postTimeTextView;
        @BindView(R.id.title_and_image_constraint_layout)
        ConstraintLayout titleAndImageConstraintLayout;
        @BindView(R.id.title_text_view_item_post_compact_right_thumbnail)
        TextView titleTextView;
        @BindView(R.id.type_text_view_item_post_compact_right_thumbnail)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_compact_right_thumbnail)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_compact_right_thumbnail)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_compact_right_thumbnail)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_compact_right_thumbnail)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_compact_right_thumbnail)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_compact_right_thumbnail)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_compact_right_thumbnail)
        CustomTextView awardsTextView;
        @BindView(R.id.link_text_view_item_post_compact_right_thumbnail)
        TextView linkTextView;
        @BindView(R.id.image_view_wrapper_item_post_compact_right_thumbnail)
        RelativeLayout relativeLayout;
        @BindView(R.id.progress_bar_item_post_compact_right_thumbnail)
        ProgressBar progressBar;
        @BindView(R.id.image_view_item_post_compact_right_thumbnail)
        ImageView imageView;
        @BindView(R.id.play_button_image_view_item_post_compact_right_thumbnail)
        ImageView playButtonImageView;
        @BindView(R.id.frame_layout_image_view_no_preview_link_item_post_compact_right_thumbnail)
        FrameLayout noPreviewLinkImageFrameLayout;
        @BindView(R.id.image_view_no_preview_link_item_post_compact_right_thumbnail)
        ImageView noPreviewLinkImageView;
        @BindView(R.id.barrier2)
        Barrier imageBarrier;
        @BindView(R.id.bottom_constraint_layout_item_post_compact_right_thumbnail)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_compact_right_thumbnail)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_compact_right_thumbnail)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_compact_right_thumbnail)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_compact_right_thumbnail)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_compact_right_thumbnail)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_compact_right_thumbnail)
        ImageView shareButton;
        @BindView(R.id.divider_item_post_compact_right_thumbnail)
        View divider;

        PostCompactRightThumbnailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            setBaseView(iconGifImageView, nameTextView, stickiedPostImageView, postTimeTextView,
                    titleAndImageConstraintLayout, titleTextView, typeTextView, archivedImageView,
                    lockedImageView, crosspostImageView, nsfwTextView, spoilerTextView,
                    flairTextView, awardsTextView, linkTextView, relativeLayout, progressBar, imageView,
                    playButtonImageView, noPreviewLinkImageFrameLayout, noPreviewLinkImageView,
                    imageBarrier, bottomConstraintLayout, upvoteButton, scoreTextView, downvoteButton,
                    commentsCountTextView, saveButton, shareButton, divider);
        }
    }

    class PostGalleryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress_bar_item_post_gallery)
        ProgressBar progressBar;
        @BindView(R.id.video_or_gif_indicator_image_view_item_post_gallery)
        ImageView videoOrGifIndicatorImageView;
        @BindView(R.id.image_view_item_post_gallery)
        AspectRatioGifImageView imageView;
        @BindView(R.id.load_image_error_relative_layout_item_post_gallery)
        RelativeLayout errorRelativeLayout;
        @BindView(R.id.load_image_error_text_view_item_gallery)
        TextView errorTextView;
        @BindView(R.id.image_view_no_preview_item_post_gallery)
        ImageView noPreviewImageView;
        @BindView(R.id.title_text_view_item_post_gallery)
        TextView titleTextView;

        public PostGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setBackgroundTintList(ColorStateList.valueOf(mCardViewBackgroundColor));
            titleTextView.setTextColor(mPostTitleColor);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            noPreviewImageView.setBackgroundColor(mNoPreviewPostTypeBackgroundColor);
            noPreviewImageView.setColorFilter(mNoPreviewPostTypeIconTint, PorterDuff.Mode.SRC_IN);
            errorTextView.setTextColor(mPrimaryTextColor);

            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position >= 0 && canStartActivity) {
                    Post post = getItem(position);
                    if (post != null) {
                        markPostRead(post, true);
                        canStartActivity = false;

                        if (post.getPostType() == Post.TEXT_TYPE || !mSharedPreferences.getBoolean(SharedPreferencesUtils.CLICK_TO_SHOW_MEDIA_IN_GALLERY_LAYOUT, false)) {
                            openViewPostDetailActivity(post, getBindingAdapterPosition());
                        } else {
                            openMedia(post);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position >= 0 && canStartActivity) {
                    Post post = getItem(position);
                    if (post != null) {
                        markPostRead(post, true);
                        canStartActivity = false;

                        if (post.getPostType() == Post.TEXT_TYPE || mSharedPreferences.getBoolean(SharedPreferencesUtils.CLICK_TO_SHOW_MEDIA_IN_GALLERY_LAYOUT, false)) {
                            openViewPostDetailActivity(post, getBindingAdapterPosition());
                        } else {
                            openMedia(post);
                        }
                    }
                }

                return true;
            });

            noPreviewImageView.setOnClickListener(view -> {
                itemView.performClick();
            });
        }

        void markPostRead(Post post, boolean changePostItemColor) {
            if (mAccessToken != null && !post.isRead() && mMarkPostsAsRead) {
                post.markAsRead(true);
                if (changePostItemColor) {
                    itemView.setBackgroundTintList(ColorStateList.valueOf(mReadPostCardViewBackgroundColor));
                    titleTextView.setTextColor(mReadPostTitleColor);
                }
                if (mActivity != null && mActivity instanceof MarkPostAsReadInterface) {
                    ((MarkPostAsReadInterface) mActivity).markPostAsRead(post);
                }
            }
        }
    }

    class PostCard2VideoAutoplayViewHolder extends PostBaseViewHolder implements ToroPlayer {
        @BindView(R.id.icon_gif_image_view_item_post_card_2_video_autoplay)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.subreddit_name_text_view_item_post_card_2_video_autoplay)
        TextView subredditTextView;
        @BindView(R.id.user_text_view_item_post_card_2_video_autoplay)
        TextView userTextView;
        @BindView(R.id.stickied_post_image_view_item_post_card_2_video_autoplay)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_card_2_video_autoplay)
        TextView postTimeTextView;
        @BindView(R.id.title_text_view_item_post_card_2_video_autoplay)
        TextView titleTextView;
        @BindView(R.id.type_text_view_item_post_card_2_video_autoplay)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_card_2_video_autoplay)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_card_2_video_autoplay)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_card_2_video_autoplay)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_card_2_video_autoplay)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_card_2_video_autoplay)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_card_2_video_autoplay)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_card_2_video_autoplay)
        CustomTextView awardsTextView;
        @BindView(R.id.aspect_ratio_frame_layout_item_post_card_2_video_autoplay)
        AspectRatioFrameLayout aspectRatioFrameLayout;
        @BindView(R.id.preview_image_view_item_post_card_2_video_autoplay)
        GifImageView previewImageView;
        @BindView(R.id.error_loading_gfycat_image_view_item_post_card_2_video_autoplay)
        ImageView errorLoadingGfycatImageView;
        @BindView(R.id.player_view_item_post_card_2_video_autoplay)
        PlayerView videoPlayer;
        @BindView(R.id.mute_exo_playback_control_view)
        ImageView muteButton;
        @BindView(R.id.fullscreen_exo_playback_control_view)
        ImageView fullscreenButton;
        @BindView(R.id.bottom_constraint_layout_item_post_card_2_video_autoplay)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_card_2_video_autoplay)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_card_2_video_autoplay)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_card_2_video_autoplay)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_card_2_video_autoplay)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_card_2_video_autoplay)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_card_2_video_autoplay)
        ImageView shareButton;
        @BindView(R.id.divider_item_post_card_2_video_autoplay)
        View divider;

        @Nullable
        ExoPlayerViewHelper helper;
        private Uri mediaUri;
        private float volume;
        public FetchGfycatOrRedgifsVideoLinks fetchGfycatOrRedgifsVideoLinks;

        PostCard2VideoAutoplayViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(
                    iconGifImageView,
                    subredditTextView,
                    userTextView,
                    stickiedPostImageView,
                    postTimeTextView,
                    titleTextView,
                    typeTextView,
                    archivedImageView,
                    lockedImageView,
                    crosspostImageView,
                    nsfwTextView,
                    spoilerTextView,
                    flairTextView,
                    awardsTextView,
                    bottomConstraintLayout,
                    upvoteButton,
                    scoreTextView,
                    downvoteButton,
                    commentsCountTextView,
                    saveButton,
                    shareButton,
                    true);

            divider.setBackgroundColor(mDividerColor);

            aspectRatioFrameLayout.setOnClickListener(null);

            muteButton.setOnClickListener(view -> {
                if (helper != null) {
                    if (helper.getVolume() != 0) {
                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_mute_white_rounded_24dp));
                        helper.setVolume(0f);
                        volume = 0f;
                        mFragment.videoAutoplayChangeMutingOption(true);
                    } else {
                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_unmute_white_rounded_24dp));
                        helper.setVolume(1f);
                        volume = 1f;
                        mFragment.videoAutoplayChangeMutingOption(false);
                    }
                }
            });

            fullscreenButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    markPostRead(post, true);
                    Intent intent = new Intent(mActivity, RedditViewVideoActivity.class);
                    if (post.isGfycat()) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_GFYCAT);
                        intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, post.getGfycatId());
                        if (post.isLoadGfyOrRedgifsVideoSuccess()) {
                            intent.setData(Uri.parse(post.getVideoUrl()));
                            intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, post.getVideoDownloadUrl());
                        }
                    } else if (post.isRedgifs()) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_TYPE, RedditViewVideoActivity.VIDEO_TYPE_REDGIFS);
                        intent.putExtra(RedditViewVideoActivity.EXTRA_GFYCAT_ID, post.getGfycatId());
                        if (post.isLoadGfyOrRedgifsVideoSuccess()) {
                            intent.setData(Uri.parse(post.getVideoUrl()));
                            intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, post.getVideoDownloadUrl());
                        }
                    } else {
                        intent.setData(Uri.parse(post.getVideoUrl()));
                        intent.putExtra(RedditViewVideoActivity.EXTRA_VIDEO_DOWNLOAD_URL, post.getVideoDownloadUrl());
                        intent.putExtra(RedditViewVideoActivity.EXTRA_SUBREDDIT, post.getSubredditName());
                        intent.putExtra(RedditViewVideoActivity.EXTRA_ID, post.getId());
                    }
                    intent.putExtra(RedditViewVideoActivity.EXTRA_POST_TITLE, post.getTitle());
                    if (helper != null) {
                        intent.putExtra(RedditViewVideoActivity.EXTRA_PROGRESS_SECONDS, helper.getLatestPlaybackInfo().getResumePosition());
                    }
                    intent.putExtra(RedditViewVideoActivity.EXTRA_IS_NSFW, post.isNSFW());
                    mActivity.startActivity(intent);
                }
            });

            previewImageView.setOnLongClickListener(view -> fullscreenButton.performClick());
        }

        void bindVideoUri(Uri videoUri) {
            mediaUri = videoUri;
        }

        void setVolume(float volume) {
            this.volume = volume;
        }

        void resetVolume() {
            volume = 0f;
        }

        @NonNull
        @Override
        public View getPlayerView() {
            return videoPlayer;
        }

        @NonNull
        @Override
        public PlaybackInfo getCurrentPlaybackInfo() {
            return helper != null && mediaUri != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
        }

        @Override
        public void initialize(@NonNull Container container, @NonNull PlaybackInfo playbackInfo) {
            if (mediaUri == null) {
                return;
            }
            if (helper == null) {
                helper = new ExoPlayerViewHelper(this, mediaUri, null, mExoCreator);
                helper.addEventListener(new Playable.EventListener() {
                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        if (!trackGroups.isEmpty()) {
                            for (int i = 0; i < trackGroups.length; i++) {
                                String mimeType = trackGroups.get(i).getFormat(0).sampleMimeType;
                                if (mimeType != null && mimeType.contains("audio")) {
                                    if (mFragment.getMasterMutingOption() != null) {
                                        volume = mFragment.getMasterMutingOption() ? 0f : 1f;
                                    }
                                    helper.setVolume(volume);
                                    muteButton.setVisibility(View.VISIBLE);
                                    if (volume != 0f) {
                                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_unmute_white_rounded_24dp));
                                    } else {
                                        muteButton.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_mute_white_rounded_24dp));
                                    }
                                    break;
                                }
                            }
                        } else {
                            muteButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onMetadata(Metadata metadata) {

                    }

                    @Override
                    public void onCues(List<Cue> cues) {

                    }

                    @Override
                    public void onRenderedFirstFrame() {
                        mGlide.clear(previewImageView);
                        previewImageView.setVisibility(View.GONE);
                    }
                });
            }
            helper.initialize(container, playbackInfo);
        }

        @Override
        public void play() {
            if (helper != null && mediaUri != null) {
                if (mFragment.getMasterMutingOption() != null) {
                    helper.setVolume(mFragment.getMasterMutingOption() ? 0f : 1f);
                }
                helper.play();
            }
        }

        @Override
        public void pause() {
            if (helper != null) helper.pause();
        }

        @Override
        public boolean isPlaying() {
            return helper != null && helper.isPlaying();
        }

        @Override
        public void release() {
            if (helper != null) {
                helper.release();
                helper = null;
            }
        }

        @Override
        public boolean wantsToPlay() {
            return mediaUri != null && ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= mStartAutoplayVisibleAreaOffset;
        }

        @Override
        public int getPlayerOrder() {
            return getBindingAdapterPosition();
        }
    }

    class PostCard2WithPreviewViewHolder extends PostBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_card_2_with_preview)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.subreddit_name_text_view_item_post_card_2_with_preview)
        TextView subredditTextView;
        @BindView(R.id.user_text_view_item_post_card_2_with_preview)
        TextView userTextView;
        @BindView(R.id.stickied_post_image_view_item_post_card_2_with_preview)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_card_2_with_preview)
        TextView postTimeTextView;
        @BindView(R.id.title_text_view_item_post_card_2_with_preview)
        TextView titleTextView;
        @BindView(R.id.type_text_view_item_post_card_2_with_preview)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_card_2_with_preview)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_card_2_with_preview)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_card_2_with_preview)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_card_2_with_preview)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_card_2_with_preview)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_card_2_with_preview)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_card_2_with_preview)
        CustomTextView awardsTextView;
        @BindView(R.id.link_text_view_item_post_card_2_with_preview)
        TextView linkTextView;
        @BindView(R.id.video_or_gif_indicator_image_view_item_post_card_2_with_preview)
        ImageView videoOrGifIndicatorImageView;
        @BindView(R.id.progress_bar_item_post_card_2_with_preview)
        ProgressBar progressBar;
        @BindView(R.id.image_view_item_post_card_2_with_preview)
        AspectRatioGifImageView imageView;
        @BindView(R.id.load_image_error_relative_layout_item_post_card_2_with_preview)
        RelativeLayout errorRelativeLayout;
        @BindView(R.id.load_image_error_text_view_item_post_card_2_with_preview)
        TextView errorTextView;
        @BindView(R.id.image_view_no_preview_gallery_item_post_card_2_with_preview)
        ImageView noPreviewImageView;
        @BindView(R.id.bottom_constraint_layout_item_post_card_2_with_preview)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_card_2_with_preview)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_card_2_with_preview)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_card_2_with_preview)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_card_2_with_preview)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_card_2_with_preview)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_card_2_with_preview)
        ImageView shareButton;
        @BindView(R.id.divider_item_post_card_2_with_preview)
        View divider;

        PostCard2WithPreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(
                    iconGifImageView,
                    subredditTextView,
                    userTextView,
                    stickiedPostImageView,
                    postTimeTextView,
                    titleTextView,
                    typeTextView,
                    archivedImageView,
                    lockedImageView,
                    crosspostImageView,
                    nsfwTextView,
                    spoilerTextView,
                    flairTextView,
                    awardsTextView,
                    bottomConstraintLayout,
                    upvoteButton,
                    scoreTextView,
                    downvoteButton,
                    commentsCountTextView,
                    saveButton,
                    shareButton,
                    true);

            linkTextView.setTextColor(mSecondaryTextColor);
            noPreviewImageView.setBackgroundColor(mNoPreviewPostTypeBackgroundColor);
            noPreviewImageView.setColorFilter(mNoPreviewPostTypeIconTint, PorterDuff.Mode.SRC_IN);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
            errorTextView.setTextColor(mPrimaryTextColor);
            divider.setBackgroundColor(mDividerColor);

            imageView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position < 0) {
                    return;
                }
                Post post = getItem(position);
                if (post != null) {
                    markPostRead(post, true);
                    openMedia(post);
                }
            });

            noPreviewImageView.setOnClickListener(view -> {
                imageView.performClick();
            });
        }
    }

    class PostCard2TextTypeViewHolder extends PostBaseViewHolder {
        @BindView(R.id.icon_gif_image_view_item_post_card_2_text)
        AspectRatioGifImageView iconGifImageView;
        @BindView(R.id.subreddit_name_text_view_item_post_card_2_text)
        TextView subredditTextView;
        @BindView(R.id.user_text_view_item_post_card_2_text)
        TextView userTextView;
        @BindView(R.id.stickied_post_image_view_item_post_card_2_text)
        ImageView stickiedPostImageView;
        @BindView(R.id.post_time_text_view_item_post_card_2_text)
        TextView postTimeTextView;
        @BindView(R.id.title_text_view_item_post_card_2_text)
        TextView titleTextView;
        @BindView(R.id.content_text_view_item_post_card_2_text)
        TextView contentTextView;
        @BindView(R.id.type_text_view_item_post_card_2_text)
        CustomTextView typeTextView;
        @BindView(R.id.archived_image_view_item_post_card_2_text)
        ImageView archivedImageView;
        @BindView(R.id.locked_image_view_item_post_card_2_text)
        ImageView lockedImageView;
        @BindView(R.id.crosspost_image_view_item_post_card_2_text)
        ImageView crosspostImageView;
        @BindView(R.id.nsfw_text_view_item_post_card_2_text)
        CustomTextView nsfwTextView;
        @BindView(R.id.spoiler_custom_text_view_item_post_card_2_text)
        CustomTextView spoilerTextView;
        @BindView(R.id.flair_custom_text_view_item_post_card_2_text)
        CustomTextView flairTextView;
        @BindView(R.id.awards_text_view_item_post_card_2_text)
        CustomTextView awardsTextView;
        @BindView(R.id.bottom_constraint_layout_item_post_card_2_text)
        ConstraintLayout bottomConstraintLayout;
        @BindView(R.id.plus_button_item_post_card_2_text)
        ImageView upvoteButton;
        @BindView(R.id.score_text_view_item_post_card_2_text)
        TextView scoreTextView;
        @BindView(R.id.minus_button_item_post_card_2_text)
        ImageView downvoteButton;
        @BindView(R.id.comments_count_item_post_card_2_text)
        TextView commentsCountTextView;
        @BindView(R.id.save_button_item_post_card_2_text)
        ImageView saveButton;
        @BindView(R.id.share_button_item_post_card_2_text)
        ImageView shareButton;
        @BindView(R.id.divider_item_post_card_2_text)
        View divider;

        PostCard2TextTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(
                    iconGifImageView,
                    subredditTextView,
                    userTextView,
                    stickiedPostImageView,
                    postTimeTextView,
                    titleTextView,
                    typeTextView,
                    archivedImageView,
                    lockedImageView,
                    crosspostImageView,
                    nsfwTextView,
                    spoilerTextView,
                    flairTextView,
                    awardsTextView,
                    bottomConstraintLayout,
                    upvoteButton,
                    scoreTextView,
                    downvoteButton,
                    commentsCountTextView,
                    saveButton,
                    shareButton,
                    true);

            contentTextView.setTextColor(mPostContentColor);
            divider.setBackgroundColor(mDividerColor);
        }
    }

    class ErrorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.error_text_view_item_footer_error)
        TextView errorTextView;
        @BindView(R.id.retry_button_item_footer_error)
        Button retryButton;

        ErrorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            errorTextView.setText(R.string.load_more_posts_error);
            errorTextView.setTextColor(mSecondaryTextColor);
            retryButton.setOnClickListener(view -> mCallback.retryLoadingMore());
            retryButton.setBackgroundTintList(ColorStateList.valueOf(mColorPrimaryLightTheme));
            retryButton.setTextColor(mButtonTextColor);
            itemView.setOnClickListener(view -> retryButton.performClick());
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress_bar_item_footer_loading)
        ProgressBar progressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
        }
    }
}
