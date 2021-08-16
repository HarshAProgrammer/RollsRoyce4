package com.rackluxury.rolex.reddit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.postfilter.PostFilter;

public class PostFilterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_POST_FILTER = 2;

    private final OnItemClickListener onItemClickListener;
    private List<PostFilter> postFilterList;

    public interface OnItemClickListener {
        void onItemClick(PostFilter postFilter);
    }

    public PostFilterRecyclerViewAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_POST_FILTER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_fragment_header, parent, false));
        } else {
            return new PostFilterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_filter, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostFilterViewHolder) {
            ((PostFilterViewHolder) holder).textView.setText(postFilterList.get(position - 1).name);
        }
    }

    @Override
    public int getItemCount() {
        return postFilterList == null ? 1 : 1 + postFilterList.size();
    }

    public void setPostFilterList(List<PostFilter> postFilterList) {
        this.postFilterList = postFilterList;
        notifyDataSetChanged();
    }

    private class PostFilterViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public PostFilterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            itemView.setOnClickListener(view -> {
                onItemClickListener.onItemClick(postFilterList.get(getBindingAdapterPosition() - 1));
            });
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
