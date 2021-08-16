package com.rackluxury.rollsroyce.activities;

import java.util.Comparator;

public class ImageItem {
    private final String mImageUrl;
    private final String mCreator;
    private final int mViews;
    private final int mLikes;
    private final int mComments;
    private final int mDownloads;
    public ImageItem(String imageUrl, String creator, int views, int likes, int comments, int downloads) {
        mImageUrl = imageUrl;
        mCreator = creator;
        mViews = views;
        mLikes = likes;
        mComments = comments;
        mDownloads = downloads;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public String getCreator() {
        return mCreator;
    }
    public int getViewCount() {
        return mViews;
    }
    public int getLikeCount() {
        return mLikes;
    }
    public int getCommentCount() {
        return mComments;
    }
    public int getDownloadCount() {
        return mDownloads;
    }

    public static final Comparator<ImageItem> ByViews = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            int oneViews=one.mViews;
            int twoViews=two.mViews;
            return -Integer.compare(oneViews, twoViews);
        }
    };
    public static final Comparator<ImageItem> ByLikes = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            int oneLikes= one.mLikes;
            int twoLikes= two.mLikes;
            return -Integer.compare(oneLikes, twoLikes);
        }
    };
    public static final Comparator<ImageItem> ByComments = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            int oneComments=one.mComments;
            int twoComments=two.mComments;
            return -Integer.compare(oneComments, twoComments);
        }
    };
    public static final Comparator<ImageItem> ByDownloads = new Comparator<ImageItem>() {
        @Override
        public int compare(ImageItem one, ImageItem two) {
            int oneDownloads=one.mDownloads;
            int twoDownloads=two.mDownloads;
            return -Integer.compare(oneDownloads, twoDownloads);
        }
    };
}