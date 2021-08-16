package com.rackluxury.rolex.reddit.user;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;

import com.rackluxury.rolex.reddit.NetworkState;
import com.rackluxury.rolex.reddit.SortType;
import retrofit2.Retrofit;

public class UserListingDataSource extends PageKeyedDataSource<String, UserData> {

    private Retrofit retrofit;
    private String query;
    private SortType sortType;
    private boolean nsfw;

    private MutableLiveData<NetworkState> paginationNetworkStateLiveData;
    private MutableLiveData<NetworkState> initialLoadStateLiveData;
    private MutableLiveData<Boolean> hasUserLiveData;

    private LoadParams<String> params;
    private LoadCallback<String, UserData> callback;

    UserListingDataSource(Retrofit retrofit, String query, SortType sortType, boolean nsfw) {
        this.retrofit = retrofit;
        this.query = query;
        this.sortType = sortType;
        this.nsfw = nsfw;
        paginationNetworkStateLiveData = new MutableLiveData<>();
        initialLoadStateLiveData = new MutableLiveData<>();
        hasUserLiveData = new MutableLiveData<>();
    }

    MutableLiveData<NetworkState> getPaginationNetworkStateLiveData() {
        return paginationNetworkStateLiveData;
    }

    MutableLiveData<NetworkState> getInitialLoadStateLiveData() {
        return initialLoadStateLiveData;
    }

    MutableLiveData<Boolean> hasUserLiveData() {
        return hasUserLiveData;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, UserData> callback) {
        initialLoadStateLiveData.postValue(NetworkState.LOADING);

        FetchUserData.fetchUserListingData(retrofit, query, null, sortType.getType().value, nsfw,
                new FetchUserData.FetchUserListingDataListener() {
                    @Override
                    public void onFetchUserListingDataSuccess(ArrayList<UserData> UserData, String after) {
                        hasUserLiveData.postValue(UserData.size() != 0);

                        callback.onResult(UserData, null, after);
                        initialLoadStateLiveData.postValue(NetworkState.LOADED);
                    }

                    @Override
                    public void onFetchUserListingDataFailed() {
                        initialLoadStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error retrieving com.rackluxury.rolex.reddit.User list"));
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, UserData> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, UserData> callback) {
        this.params = params;
        this.callback = callback;

        if (params.key.equals("null") || params.key.equals("")) {
            return;
        }

        FetchUserData.fetchUserListingData(retrofit, query, params.key, sortType.getType().value, nsfw,
                new FetchUserData.FetchUserListingDataListener() {
                    @Override
                    public void onFetchUserListingDataSuccess(ArrayList<UserData> UserData, String after) {
                        callback.onResult(UserData, after);
                        paginationNetworkStateLiveData.postValue(NetworkState.LOADED);
                    }

                    @Override
                    public void onFetchUserListingDataFailed() {
                        paginationNetworkStateLiveData.postValue(new NetworkState(NetworkState.Status.FAILED, "Error retrieving com.rackluxury.rolex.reddit.User list"));
                    }
                });
    }

    void retryLoadingMore() {
        loadAfter(params, callback);
    }
}
