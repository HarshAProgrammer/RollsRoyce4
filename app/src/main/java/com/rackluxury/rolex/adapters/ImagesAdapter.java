package com.rackluxury.rolex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.activities.ImageItem;
import com.squareup.picasso.Picasso;


import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder> {
    private final Context mContext;
    private final List<ImageItem> mImagesList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ImagesAdapter(Context context, List<ImageItem> imageItem) {
        mContext = context;
        mImagesList = imageItem;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, int position) {
        ImageItem currentItem = mImagesList.get(position);



        String imageUrl = currentItem.getImageUrl();
        String creatorName = currentItem.getCreator();
        int viewCount = currentItem.getViewCount();


        String strViewCount = String.valueOf(viewCount);


        holder.mTextViewCreator.setText(creatorName);
        holder.mTextViewViews.setText(strViewCount);


        Picasso.get().load(imageUrl).fit().centerInside().into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mImageView;
        public final TextView mTextViewCreator;
        public final TextView mTextViewViews;


        public ImagesViewHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.ivImageImages);
            mTextViewCreator = itemView.findViewById(R.id.tvCreatorImages);
            mTextViewViews = itemView.findViewById(R.id.tvViewsImages);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}