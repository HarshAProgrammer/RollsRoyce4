package com.rackluxury.rollsroyce.reddit.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.zhanghai.android.fastscroll.PopupTextProvider;
import com.rackluxury.rollsroyce.reddit.FavoriteThing;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.RedditDataRoomDatabase;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewUserDetailActivity;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.subscribeduser.SubscribedUserData;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

public class FollowedUsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PopupTextProvider {
    private static final int VIEW_TYPE_FAVORITE_USER_DIVIDER = 0;
    private static final int VIEW_TYPE_FAVORITE_USER = 1;
    private static final int VIEW_TYPE_USER_DIVIDER = 2;
    private static final int VIEW_TYPE_USER = 3;

    private List<SubscribedUserData> mSubscribedUserData;
    private List<SubscribedUserData> mFavoriteSubscribedUserData;
    private final Context mContext;
    private final Executor mExecutor;
    private final Retrofit mOauthRetrofit;
    private final RedditDataRoomDatabase mRedditDataRoomDatabase;
    private final String mAccessToken;
    private final RequestManager glide;
    private final int mPrimaryTextColor;
    private final int mSecondaryTextColor;

    public FollowedUsersRecyclerViewAdapter(Context context, Executor executor, Retrofit oauthRetrofit,
                                            RedditDataRoomDatabase redditDataRoomDatabase,
                                            CustomThemeWrapper customThemeWrapper,
                                            String accessToken) {
        mContext = context;
        mExecutor = executor;
        mOauthRetrofit = oauthRetrofit;
        mRedditDataRoomDatabase = redditDataRoomDatabase;
        mAccessToken = accessToken;
        glide = Glide.with(context);
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
    }

    @Override
    public int getItemViewType(int position) {
        if (mFavoriteSubscribedUserData != null && mFavoriteSubscribedUserData.size() > 0) {
            if (position == 0) {
                return VIEW_TYPE_FAVORITE_USER_DIVIDER;
            } else if (position == mFavoriteSubscribedUserData.size() + 1) {
                return VIEW_TYPE_USER_DIVIDER;
            } else if (position <= mFavoriteSubscribedUserData.size()) {
                return VIEW_TYPE_FAVORITE_USER;
            } else {
                return VIEW_TYPE_USER;
            }
        } else {
            return VIEW_TYPE_USER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case VIEW_TYPE_FAVORITE_USER_DIVIDER:
                return new FavoriteUsersDividerViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, viewGroup, false));
            case VIEW_TYPE_FAVORITE_USER:
                return new FavoriteUserViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_subscribed_thing, viewGroup, false));
            case VIEW_TYPE_USER_DIVIDER:
                return new AllUsersDividerViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, viewGroup, false));
            default:
                return new UserViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_subscribed_thing, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof UserViewHolder) {
            int offset = (mFavoriteSubscribedUserData != null && mFavoriteSubscribedUserData.size() > 0) ?
                    mFavoriteSubscribedUserData.size() + 2 : 0;

            if (!mSubscribedUserData.get(viewHolder.getAdapterPosition() - offset).getIconUrl().equals("")) {
                glide.load(mSubscribedUserData.get(viewHolder.getAdapterPosition() - offset).getIconUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(glide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((UserViewHolder) viewHolder).iconGifImageView);
            } else {
                glide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((UserViewHolder) viewHolder).iconGifImageView);
            }
            ((UserViewHolder) viewHolder).userNameTextView.setText(mSubscribedUserData.get(viewHolder.getAdapterPosition() - offset).getName());

            if(mSubscribedUserData.get(viewHolder.getAdapterPosition() - offset).isFavorite()) {
                ((UserViewHolder) viewHolder).favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
            } else {
                ((UserViewHolder) viewHolder).favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
            }
        } else if (viewHolder instanceof FavoriteUserViewHolder) {
            if (!mFavoriteSubscribedUserData.get(viewHolder.getAdapterPosition() - 1).getIconUrl().equals("")) {
                glide.load(mFavoriteSubscribedUserData.get(viewHolder.getAdapterPosition() - 1).getIconUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(glide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((FavoriteUserViewHolder) viewHolder).iconGifImageView);
            } else {
                glide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((FavoriteUserViewHolder) viewHolder).iconGifImageView);
            }
            ((FavoriteUserViewHolder) viewHolder).userNameTextView.setText(mFavoriteSubscribedUserData.get(viewHolder.getAdapterPosition() - 1).getName());

            if(mFavoriteSubscribedUserData.get(viewHolder.getAdapterPosition() - 1).isFavorite()) {
                ((FavoriteUserViewHolder) viewHolder).favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
            } else {
                ((FavoriteUserViewHolder) viewHolder).favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mSubscribedUserData != null && mSubscribedUserData.size() > 0) {
            if(mFavoriteSubscribedUserData != null && mFavoriteSubscribedUserData.size() > 0) {
                return mSubscribedUserData.size() + mFavoriteSubscribedUserData.size() + 2;
            }
            return mSubscribedUserData.size();
        }
        return 0;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if(holder instanceof UserViewHolder) {
            glide.clear(((UserViewHolder) holder).iconGifImageView);
        } else if (holder instanceof FavoriteUserViewHolder) {
            glide.clear(((FavoriteUserViewHolder) holder).iconGifImageView);
        }
    }

    public void setSubscribedUsers(List<SubscribedUserData> subscribedUsers) {
        mSubscribedUserData = subscribedUsers;
        notifyDataSetChanged();
    }

    public void setFavoriteSubscribedUsers(List<SubscribedUserData> favoriteSubscribedUsers) {
        mFavoriteSubscribedUserData = favoriteSubscribedUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_USER:
                int offset = (mFavoriteSubscribedUserData != null && mFavoriteSubscribedUserData.size() > 0) ?
                        mFavoriteSubscribedUserData.size() + 2 : 0;
                return mSubscribedUserData.get(position - offset).getName().substring(0, 1).toUpperCase();
            case VIEW_TYPE_FAVORITE_USER:
                return mFavoriteSubscribedUserData.get(position - 1).getName().substring(0, 1).toUpperCase();
            default:
                return "";
        }
    }

    class FavoriteUserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thing_icon_gif_image_view_item_subscribed_thing)
        GifImageView iconGifImageView;
        @BindView(R.id.thing_name_text_view_item_subscribed_thing)
        TextView userNameTextView;
        @BindView(R.id.favorite_image_view_item_subscribed_thing)
        ImageView favoriteImageView;

        FavoriteUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            userNameTextView.setTextColor(mPrimaryTextColor);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition() - 1;
                if(position >= 0 && mFavoriteSubscribedUserData.size() > position) {
                    Intent intent = new Intent(mContext, RedditViewUserDetailActivity.class);
                    intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, mFavoriteSubscribedUserData.get(position).getName());
                    mContext.startActivity(intent);
                }
            });

            favoriteImageView.setOnClickListener(view -> {
                int position = getAdapterPosition() - 1;
                if(position >= 0 && mFavoriteSubscribedUserData.size() > position) {
                    if(mFavoriteSubscribedUserData.get(position).isFavorite()) {
                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                        mFavoriteSubscribedUserData.get(position).setFavorite(false);
                        FavoriteThing.unfavoriteUser(mExecutor, new Handler(), mOauthRetrofit,
                                mRedditDataRoomDatabase, mAccessToken,
                                mFavoriteSubscribedUserData.get(position),
                                new FavoriteThing.FavoriteThingListener() {
                                    @Override
                                    public void success() {
                                        int position = getAdapterPosition() - 1;
                                        if(position >= 0 && mFavoriteSubscribedUserData.size() > position) {
                                            mFavoriteSubscribedUserData.get(position).setFavorite(false);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                                    }

                                    @Override
                                    public void failed() {
                                        Toast.makeText(mContext, R.string.thing_unfavorite_failed, Toast.LENGTH_SHORT).show();
                                        int position = getAdapterPosition() - 1;
                                        if(position >= 0 && mFavoriteSubscribedUserData.size() > position) {
                                            mFavoriteSubscribedUserData.get(position).setFavorite(true);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                                    }
                                });
                    } else {
                        favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                        mFavoriteSubscribedUserData.get(position).setFavorite(true);
                        FavoriteThing.favoriteUser(mExecutor, new Handler(), mOauthRetrofit,
                                mRedditDataRoomDatabase, mAccessToken,
                                mFavoriteSubscribedUserData.get(position),
                                new FavoriteThing.FavoriteThingListener() {
                                    @Override
                                    public void success() {
                                        int position = getAdapterPosition() - 1;
                                        if(position >= 0 && mFavoriteSubscribedUserData.size() > position) {
                                            mFavoriteSubscribedUserData.get(position).setFavorite(true);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                                    }

                                    @Override
                                    public void failed() {
                                        Toast.makeText(mContext, R.string.thing_favorite_failed, Toast.LENGTH_SHORT).show();
                                        int position = getAdapterPosition() - 1;
                                        if(position >= 0 && mFavoriteSubscribedUserData.size() > position) {
                                            mFavoriteSubscribedUserData.get(position).setFavorite(false);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                                    }
                                });
                    }
                }
            });
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thing_icon_gif_image_view_item_subscribed_thing)
        GifImageView iconGifImageView;
        @BindView(R.id.thing_name_text_view_item_subscribed_thing)
        TextView userNameTextView;
        @BindView(R.id.favorite_image_view_item_subscribed_thing)
        ImageView favoriteImageView;

        UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            userNameTextView.setTextColor(mPrimaryTextColor);

            itemView.setOnClickListener(view -> {
                int offset = (mFavoriteSubscribedUserData != null && mFavoriteSubscribedUserData.size() > 0) ?
                        mFavoriteSubscribedUserData.size() + 2 : 0;
                int position = getAdapterPosition() - offset;
                if(position >= 0 && mSubscribedUserData.size() > position) {
                    Intent intent = new Intent(mContext, RedditViewUserDetailActivity.class);
                    intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, mSubscribedUserData.get(position).getName());
                    mContext.startActivity(intent);
                }
            });

            favoriteImageView.setOnClickListener(view -> {
                int offset = (mFavoriteSubscribedUserData != null && mFavoriteSubscribedUserData.size() > 0) ?
                        mFavoriteSubscribedUserData.size() + 2 : 0;
                int position = getAdapterPosition() - offset;

                if(position >= 0 && mSubscribedUserData.size() > position) {
                    if(mSubscribedUserData.get(position).isFavorite()) {
                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                        mSubscribedUserData.get(position).setFavorite(false);
                        FavoriteThing.unfavoriteUser(mExecutor, new Handler(), mOauthRetrofit,
                                mRedditDataRoomDatabase, mAccessToken,
                                mSubscribedUserData.get(position),
                                new FavoriteThing.FavoriteThingListener() {
                                    @Override
                                    public void success() {
                                        int position = getAdapterPosition() - offset;
                                        if(position >= 0 && mSubscribedUserData.size() > position) {
                                            mSubscribedUserData.get(position).setFavorite(false);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                                    }

                                    @Override
                                    public void failed() {
                                        Toast.makeText(mContext, R.string.thing_unfavorite_failed, Toast.LENGTH_SHORT).show();
                                        int position = getAdapterPosition() - offset;
                                        if(position >= 0 && mSubscribedUserData.size() > position) {
                                            mSubscribedUserData.get(position).setFavorite(true);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                                    }
                                });
                    } else {
                        favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                        mSubscribedUserData.get(position).setFavorite(true);
                        FavoriteThing.favoriteUser(mExecutor, new Handler(), mOauthRetrofit,
                                mRedditDataRoomDatabase, mAccessToken,
                                mSubscribedUserData.get(position),
                                new FavoriteThing.FavoriteThingListener() {
                                    @Override
                                    public void success() {
                                        int position = getAdapterPosition() - offset;
                                        if(position >= 0 && mSubscribedUserData.size() > position) {
                                            mSubscribedUserData.get(position).setFavorite(true);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                                    }

                                    @Override
                                    public void failed() {
                                        Toast.makeText(mContext, R.string.thing_favorite_failed, Toast.LENGTH_SHORT).show();
                                        int position = getAdapterPosition() - offset;
                                        if(position >= 0 && mSubscribedUserData.size() > position) {
                                            mSubscribedUserData.get(position).setFavorite(false);
                                        }
                                        favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                                    }
                                });
                    }
                }
            });
        }
    }

    class FavoriteUsersDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider) TextView dividerTextView;

        FavoriteUsersDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            dividerTextView.setText(R.string.favorites);
            dividerTextView.setTextColor(mSecondaryTextColor);
        }
    }

    class AllUsersDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider) TextView dividerTextView;

        AllUsersDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            dividerTextView.setText(R.string.all);
            dividerTextView.setTextColor(mSecondaryTextColor);
        }
    }
}
