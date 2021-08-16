package com.rackluxury.rolex.reddit.adapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.movement.MovementMethodPlugin;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.Rule;
import com.rackluxury.rolex.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.rolex.reddit.bottomsheetfragments.UrlMenuBottomSheetFragment;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeWrapper;

public class RulesRecyclerViewAdapter extends RecyclerView.Adapter<RulesRecyclerViewAdapter.RuleViewHolder> {
    private Markwon markwon;
    private ArrayList<Rule> rules;
    private int mPrimaryTextColor;
    private int mSecondaryTextColor;

    public RulesRecyclerViewAdapter(AppCompatActivity activity, CustomThemeWrapper customThemeWrapper) {
        markwon = Markwon.builder(activity)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.linkResolver((view, link) -> {
                            Intent intent = new Intent(activity, RedditLinkResolverActivity.class);
                            Uri uri = Uri.parse(link);
                            intent.setData(uri);
                            activity.startActivity(intent);
                        });
                    }

                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.linkColor(customThemeWrapper.getLinkColor());
                    }
                })
                .usePlugin(MovementMethodPlugin.create(BetterLinkMovementMethod.linkify(Linkify.WEB_URLS, activity).setOnLinkLongClickListener((textView, url) -> {
                    if (activity != null && !activity.isDestroyed() && !activity.isFinishing()) {
                        UrlMenuBottomSheetFragment urlMenuBottomSheetFragment = new UrlMenuBottomSheetFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(UrlMenuBottomSheetFragment.EXTRA_URL, url);
                        urlMenuBottomSheetFragment.setArguments(bundle);
                        urlMenuBottomSheetFragment.show(activity.getSupportFragmentManager(), urlMenuBottomSheetFragment.getTag());
                    }
                    return true;
                })))
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .usePlugin(StrikethroughPlugin.create())
                .build();
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RuleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        holder.shortNameTextView.setText(rules.get(holder.getAdapterPosition()).getShortName());
        if (rules.get(holder.getAdapterPosition()).getDescriptionHtml() == null) {
            holder.descriptionMarkwonView.setVisibility(View.GONE);
        } else {
            markwon.setMarkdown(holder.descriptionMarkwonView, rules.get(holder.getAdapterPosition()).getDescriptionHtml());
        }
    }

    @Override
    public int getItemCount() {
        return rules == null ? 0 : rules.size();
    }

    @Override
    public void onViewRecycled(@NonNull RuleViewHolder holder) {
        super.onViewRecycled(holder);
        holder.descriptionMarkwonView.setVisibility(View.VISIBLE);
    }

    public void changeDataset(ArrayList<Rule> rules) {
        this.rules = rules;
        notifyDataSetChanged();
    }

    class RuleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.short_name_text_view_item_rule)
        TextView shortNameTextView;
        @BindView(R.id.description_markwon_view_item_rule)
        TextView descriptionMarkwonView;

        RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            shortNameTextView.setTextColor(mPrimaryTextColor);
            descriptionMarkwonView.setTextColor(mSecondaryTextColor);
        }
    }
}
