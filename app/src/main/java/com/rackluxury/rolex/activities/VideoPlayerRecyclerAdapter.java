package com.rackluxury.rolex.activities;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.activities.models.MediaObject;

import java.util.ArrayList;

public class VideoPlayerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<MediaObject> mediaObjects;
    private final RequestManager requestManager;
    boolean isShimmer = true;
    int shimmerNumber = 5;


    public VideoPlayerRecyclerAdapter(ArrayList<MediaObject> mediaObjects, RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_video_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        ((VideoPlayerViewHolder) viewHolder).onBind(mediaObjects.get(i), requestManager);

        try {


            if (isShimmer) {

                ((VideoPlayerViewHolder) viewHolder).shimmerFrameLayout.startShimmer();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ((VideoPlayerViewHolder) viewHolder).shimmerFrameLayout.stopShimmer();
                        ((VideoPlayerViewHolder) viewHolder).shimmerFrameLayout.setShimmer(null);

                    }
                },3000);


            } else {
                ((VideoPlayerViewHolder) viewHolder).shimmerFrameLayout.stopShimmer();
                ((VideoPlayerViewHolder) viewHolder).shimmerFrameLayout.setShimmer(null);

            }
        } catch (NullPointerException e) {
            System.out.print("NullPointerException Caught");
        }

    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

    public static class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

        final FrameLayout media_container;
        final TextView title;
        final ImageView thumbnail;
        final ImageView volumeControl;
        final ProgressBar progressBar;
        final View parent;
        RequestManager requestManager;
        ShimmerFrameLayout shimmerFrameLayout;

        public VideoPlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            shimmerFrameLayout = itemView.findViewById(R.id.sflVideo);
            media_container = itemView.findViewById(R.id.media_container_video_player_item);
            thumbnail = itemView.findViewById(R.id.ivThumbVideoItem);
            title = itemView.findViewById(R.id.tvTitleVideoItem);
            progressBar = itemView.findViewById(R.id.pbVideoItem);
            volumeControl = itemView.findViewById(R.id.ivVolumeVideoItem);
        }

        public void onBind(MediaObject mediaObject, RequestManager requestManager) {
            this.requestManager = requestManager;
            parent.setTag(this);
            title.setText(mediaObject.getTitle());
            this.requestManager
                    .load(mediaObject.getThumbnail())
                    .into(thumbnail);
        }


    }
}
