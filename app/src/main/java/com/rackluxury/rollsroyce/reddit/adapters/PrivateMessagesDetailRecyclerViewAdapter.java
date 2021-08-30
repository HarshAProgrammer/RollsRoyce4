package com.rackluxury.rollsroyce.reddit.adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewPrivateMessagesActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditViewUserDetailActivity;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.message.Message;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.rollsroyce.reddit.utils.Utils;

public class PrivateMessagesDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;
    private Message mMessage;
    private final RedditViewPrivateMessagesActivity mRedditViewPrivateMessagesActivity;
    private final RequestManager mGlide;
    private final Locale mLocale;
    private final String mAccountName;
    private final Markwon mMarkwon;
    private final boolean mShowElapsedTime;
    private final String mTimeFormatPattern;
    private final int mSecondaryTextColor;
    private final int mReceivedMessageTextColor;
    private final int mSentMessageTextColor;
    private final int mReceivedMessageBackgroundColor;
    private final int mSentMessageBackgroundColor;

    public PrivateMessagesDetailRecyclerViewAdapter(RedditViewPrivateMessagesActivity redditViewPrivateMessagesActivity,
                                                    SharedPreferences sharedPreferences, Locale locale,
                                                    Message message, String accountName,
                                                    CustomThemeWrapper customThemeWrapper) {
        mMessage = message;
        mRedditViewPrivateMessagesActivity = redditViewPrivateMessagesActivity;
        mGlide = Glide.with(redditViewPrivateMessagesActivity);
        mLocale = locale;
        mAccountName = accountName;
        mMarkwon = Markwon.builder(redditViewPrivateMessagesActivity)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.linkResolver((view, link) -> {
                            Intent intent = new Intent(redditViewPrivateMessagesActivity, RedditLinkResolverActivity.class);
                            Uri uri = Uri.parse(link);
                            intent.setData(uri);
                            redditViewPrivateMessagesActivity.startActivity(intent);
                        });
                    }

                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.linkColor(customThemeWrapper.getLinkColor());
                    }
                })
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .build();
        mShowElapsedTime = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ELAPSED_TIME_KEY, false);
        mTimeFormatPattern = sharedPreferences.getString(SharedPreferencesUtils.TIME_FORMAT_KEY, SharedPreferencesUtils.TIME_FORMAT_DEFAULT_VALUE);
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
        mReceivedMessageTextColor = customThemeWrapper.getReceivedMessageTextColor();
        mSentMessageTextColor = customThemeWrapper.getSentMessageTextColor();
        mReceivedMessageBackgroundColor = customThemeWrapper.getReceivedMessageBackgroundColor();
        mSentMessageBackgroundColor = customThemeWrapper.getSentMessageBackgroundColor();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return mMessage.getAuthor().equals(mAccountName) ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return mMessage.getReplies().get(position - 1).getAuthor().equals(mAccountName) ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            return new SentMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_message_sent, parent, false));
        } else {
            return new ReceivedMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_message_received, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message;
        if (holder.getAdapterPosition() == 0) {
            message = mMessage;
        } else {
            message = mMessage.getReplies().get(holder.getAdapterPosition() - 1);
        }
        if (message != null) {
            if (holder instanceof MessageViewHolder) {
                mMarkwon.setMarkdown(((MessageViewHolder) holder).messageTextView, message.getBody());

                ((MessageViewHolder) holder).messageTextView.setOnClickListener(view -> ((MessageViewHolder) holder).itemView.performClick());
                if (mShowElapsedTime) {
                    ((MessageViewHolder) holder).timeTextView.setText(Utils.getElapsedTime(mRedditViewPrivateMessagesActivity, message.getTimeUTC()));
                } else {
                    ((MessageViewHolder) holder).timeTextView.setText(Utils.getFormattedTime(mLocale, message.getTimeUTC(), mTimeFormatPattern));
                }

                ((MessageViewHolder) holder).messageTextView.setOnClickListener(view -> {
                    if (((MessageViewHolder) holder).timeTextView.getVisibility() != View.VISIBLE) {
                        ((MessageViewHolder) holder).timeTextView.setVisibility(View.VISIBLE);
                        mRedditViewPrivateMessagesActivity.delayTransition();
                    } else {
                        ((MessageViewHolder) holder).timeTextView.setVisibility(View.GONE);
                        mRedditViewPrivateMessagesActivity.delayTransition();
                    }
                });
            }

            if (holder instanceof SentMessageViewHolder) {
                ((SentMessageViewHolder) holder).messageTextView.setBackground(Utils.getTintedDrawable(mRedditViewPrivateMessagesActivity,
                        R.drawable.private_message_ballon, mSentMessageBackgroundColor));
            } else if (holder instanceof ReceivedMessageViewHolder) {
                mRedditViewPrivateMessagesActivity.fetchUserAvatar(message.getAuthor(), userAvatarUrl -> {
                    if (userAvatarUrl == null || userAvatarUrl.equals("")) {
                        mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                .into(((ReceivedMessageViewHolder) holder).userAvatarImageView);
                    } else {
                        mGlide.load(userAvatarUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                .error(mGlide.load(R.drawable.subreddit_default_icon)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                .into(((ReceivedMessageViewHolder) holder).userAvatarImageView);
                    }
                });

                ((ReceivedMessageViewHolder) holder).userAvatarImageView.setOnClickListener(view -> {
                    Intent intent = new Intent(mRedditViewPrivateMessagesActivity, RedditViewUserDetailActivity.class);
                    intent.putExtra(RedditViewUserDetailActivity.EXTRA_USER_NAME_KEY, message.getAuthor());
                    mRedditViewPrivateMessagesActivity.startActivity(intent);
                });

                ((ReceivedMessageViewHolder) holder).messageTextView.setBackground(
                        Utils.getTintedDrawable(mRedditViewPrivateMessagesActivity,
                                R.drawable.private_message_ballon, mReceivedMessageBackgroundColor));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMessage == null) {
            return 0;
        } else if (mMessage.getReplies() == null) {
            return 1;
        } else {
            return 1 + mMessage.getReplies().size();
        }
    }

    public void setMessage(Message message) {
        mMessage = message;
        notifyDataSetChanged();
    }

    public void addReply(Message reply) {
        int currentSize = getItemCount();

        if (mMessage != null) {
            mMessage.addReply(reply);
        } else {
            mMessage = reply;
        }

        notifyItemInserted(currentSize);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).messageTextView.setBackground(null);
            ((MessageViewHolder) holder).timeTextView.setVisibility(View.GONE);
        }
        if (holder instanceof ReceivedMessageViewHolder) {
            mGlide.clear(((ReceivedMessageViewHolder) holder).userAvatarImageView);
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timeTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setBaseView(TextView messageTextView, TextView timeTextView) {
            this.messageTextView = messageTextView;
            this.timeTextView = timeTextView;

            messageTextView.setTextColor(Color.WHITE);
            timeTextView.setTextColor(mSecondaryTextColor);
        }
    }

    class SentMessageViewHolder extends MessageViewHolder {
        @BindView(R.id.message_text_view_item_private_message_sent)
        TextView messageTextView;
        @BindView(R.id.time_text_view_item_private_message_sent)
        TextView timeTextView;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(messageTextView, timeTextView);

            messageTextView.setTextColor(mSentMessageTextColor);

        }
    }

    class ReceivedMessageViewHolder extends MessageViewHolder {
        @BindView(R.id.avatar_image_view_item_private_message_received)
        ImageView userAvatarImageView;
        @BindView(R.id.message_text_view_item_private_message_received)
        TextView messageTextView;
        @BindView(R.id.time_text_view_item_private_message_received)
        TextView timeTextView;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(messageTextView, timeTextView);

            messageTextView.setTextColor(mReceivedMessageTextColor);
        }
    }
}
