package com.rackluxury.rolex.reddit.apis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface VReddIt {
    @GET()
    Call<String> getRedirectUrl(@Url String vReddItUrl);
}
