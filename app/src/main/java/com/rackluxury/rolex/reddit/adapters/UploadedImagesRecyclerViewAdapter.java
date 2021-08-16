package com.rackluxury.rolex.reddit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.UploadedImage;

public class UploadedImagesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<UploadedImage> uploadedImages;
    private ItemClickListener itemClickListener;

    public UploadedImagesRecyclerViewAdapter(ArrayList<UploadedImage> uploadedImages, ItemClickListener itemClickListener) {
        this.uploadedImages = uploadedImages;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UploadedImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_uploaded_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((UploadedImageViewHolder) holder).imageNameTextView.setText(uploadedImages.get(position).imageName);
        ((UploadedImageViewHolder) holder).imageUrlTextView.setText(uploadedImages.get(position).imageUrl);
    }

    @Override
    public int getItemCount() {
        return uploadedImages == null ? 0 : uploadedImages.size();
    }

    private class UploadedImageViewHolder extends RecyclerView.ViewHolder {
        TextView imageNameTextView;
        TextView imageUrlTextView;

        public UploadedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageNameTextView = itemView.findViewById(R.id.image_name_item_uploaded_image);
            imageUrlTextView = itemView.findViewById(R.id.image_url_item_uploaded_image);

            itemView.setOnClickListener(view -> {
                itemClickListener.onClick(uploadedImages.get(getBindingAdapterPosition()));
            });
        }
    }

    public interface ItemClickListener {
        void onClick(UploadedImage uploadedImage);
    }
}
