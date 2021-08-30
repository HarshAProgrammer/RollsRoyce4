package com.rackluxury.rollsroyce.reddit.comment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import java.util.Locale;

import com.rackluxury.rollsroyce.reddit.SortType;
import retrofit2.Retrofit;

class CommentDataSourceFactory extends DataSource.Factory {
    private final Retrofit retrofit;
    private final Locale locale;
    private final String accessToken;
    private final String username;
    private SortType sortType;
    private final boolean areSavedComments;

    private CommentDataSource commentDataSource;
    private final MutableLiveData<CommentDataSource> commentDataSourceLiveData;

    CommentDataSourceFactory(Retrofit retrofit, Locale locale, @Nullable String accessToken,
                             String username, SortType sortType,
                             boolean areSavedComments) {
        this.retrofit = retrofit;
        this.locale = locale;
        this.accessToken = accessToken;
        this.username = username;
        this.sortType = sortType;
        this.areSavedComments = areSavedComments;
        commentDataSourceLiveData = new MutableLiveData<>();
    }

    @NonNull
    @Override
    public DataSource create() {
        commentDataSource = new CommentDataSource(retrofit, locale, accessToken, username, sortType,
                areSavedComments);
        commentDataSourceLiveData.postValue(commentDataSource);
        return commentDataSource;
    }

    public MutableLiveData<CommentDataSource> getCommentDataSourceLiveData() {
        return commentDataSourceLiveData;
    }

    CommentDataSource getCommentDataSource() {
        return commentDataSource;
    }

    void changeSortType(SortType sortType) {
        this.sortType = sortType;
    }
}
