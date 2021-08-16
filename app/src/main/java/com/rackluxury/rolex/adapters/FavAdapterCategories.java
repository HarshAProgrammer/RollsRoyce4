package com.rackluxury.rolex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rackluxury.rolex.R;
import com.rackluxury.rolex.activities.FavDBCategories;
import com.rackluxury.rolex.activities.FavItemCategories;

import java.util.List;


public class FavAdapterCategories extends RecyclerView.Adapter<FavAdapterCategories.ViewHolder> {

    private Context context;
    private List<FavItemCategories> favItemListCategories;
    private FavDBCategories favDB;


    public FavAdapterCategories(Context context, List<FavItemCategories> favItemListCategories) {
        this.context = context;
        this.favItemListCategories = favItemListCategories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item_categories,
                parent, false);
        favDB = new FavDBCategories(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.favCardView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_view_animation));
        holder.favTextView.setText(favItemListCategories.get(position).getItem_title());
        holder.favImageView.setImageResource(favItemListCategories.get(position).getItem_image());
    }

    @Override
    public int getItemCount() {
        return favItemListCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView favTextView;
        ImageView favImageView;
        CardView favCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favCardView = itemView.findViewById(R.id.cvFavCategories);
            favTextView = itemView.findViewById(R.id.tvFavCategories);
            favImageView = itemView.findViewById(R.id.ivFavCategories);

        }
    }

}
