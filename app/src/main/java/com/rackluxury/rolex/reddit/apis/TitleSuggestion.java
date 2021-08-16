package com.rackluxury.rolex.reddit.apis;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface TitleSuggestion {
    @GET()
    Call<String> getHtml(@Url String url);
}
