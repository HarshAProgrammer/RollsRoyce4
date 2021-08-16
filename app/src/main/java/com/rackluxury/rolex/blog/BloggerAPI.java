package com.rackluxury.rolex.blog;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class BloggerAPI {

    public static final String key = "AIzaSyBcigA-cfl2QP1zaTaZxCOdn6wF_ti3xhg";
    public static final String url = "https://www.googleapis.com/blogger/v3/blogs/8017089139017449406/posts/";

    public static PostService postService = null;

    public static PostService getService()
    {
        if(postService == null)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            postService = retrofit.create(PostService.class);
        }
        return postService;
    }

    public interface PostService {
        @GET
        Call<PostListBlog> getPostList(@Url String url);
    }


}
