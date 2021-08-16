package com.rackluxury.rolex.reddit.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.recentsearchquery.RecentSearchQuery;
import com.rackluxury.rolex.reddit.utils.Utils;

public class SearchActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecentSearchQuery> recentSearchQueries;
    private int primaryTextColor;
    private Drawable historyIcon;
    private Drawable deleteIcon;
    private ItemOnClickListener itemOnClickListener;

    public interface ItemOnClickListener {
        void onClick(String query);
        void onDelete(RecentSearchQuery recentSearchQuery);
    }

    public SearchActivityRecyclerViewAdapter(Activity activity, CustomThemeWrapper customThemeWrapper,
                                             ItemOnClickListener itemOnClickListener) {
        this.primaryTextColor = customThemeWrapper.getPrimaryTextColor();
        this.historyIcon = Utils.getTintedDrawable(activity, R.drawable.ic_history_24dp, customThemeWrapper.getPrimaryIconColor());
        this.deleteIcon = Utils.getTintedDrawable(activity, R.drawable.ic_delete_24dp, customThemeWrapper.getPrimaryIconColor());
        this.itemOnClickListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentSearchQueryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_search_query, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecentSearchQueryViewHolder) {
            if (recentSearchQueries != null && !recentSearchQueries.isEmpty() && position < recentSearchQueries.size()) {
                ((RecentSearchQueryViewHolder) holder).recentSearchQueryTextView.setText(recentSearchQueries.get(position).getSearchQuery());
            }
        }
    }

    @Override
    public int getItemCount() {
        return recentSearchQueries == null ? 0 : recentSearchQueries.size();
    }

    public void setRecentSearchQueries(List<RecentSearchQuery> recentSearchQueries) {
        this.recentSearchQueries = recentSearchQueries;
        notifyDataSetChanged();
    }

    class RecentSearchQueryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recent_search_query_text_view_item_recent_search_query)
        TextView recentSearchQueryTextView;
        @BindView(R.id.delete_image_view_item_recent_search_query)
        ImageView deleteImageView;

        public RecentSearchQueryViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            recentSearchQueryTextView.setTextColor(primaryTextColor);
            recentSearchQueryTextView.setCompoundDrawablesWithIntrinsicBounds(historyIcon, null, null, null);
            deleteImageView.setImageDrawable(deleteIcon);

            itemView.setOnClickListener(view -> {
                if (recentSearchQueries != null && !recentSearchQueries.isEmpty()) {
                    itemOnClickListener.onClick(recentSearchQueries.get(getAdapterPosition()).getSearchQuery());
                }
            });

            deleteImageView.setOnClickListener(view -> {
                itemOnClickListener.onDelete(recentSearchQueries.get(getAdapterPosition()));
            });
        }
    }
}
