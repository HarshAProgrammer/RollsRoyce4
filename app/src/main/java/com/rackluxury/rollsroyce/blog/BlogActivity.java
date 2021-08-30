package com.rackluxury.rollsroyce.blog;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.activities.HomeActivity;
import com.rackluxury.rollsroyce.activities.NoInternetDialogue;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BlogActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BlogPostAdapter adapter;
    private final List<BlogItem> blogItems = new ArrayList<>();
    private Boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private String token = "";
    private SpinKitView progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        recyclerView = findViewById(R.id.rvBlog);
        progress = findViewById(R.id.skBlog);
        adapter = new BlogPostAdapter(this, blogItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        recyclerView.setAdapter(adapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {
            setNoInternetDialogue();
        }
        setUpToolbar();



        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    getData();
                }
            }
        });
        getData();
    }


    private void setUpToolbar() {
        toolbar = findViewById(R.id.toolbarBlog);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Blog Posts");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }
    private void setNoInternetDialogue() {
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(BlogActivity.this);
        noInternetDialogue.startNoInternetDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                noInternetDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent openHomeFromBlog = new Intent(BlogActivity.this, HomeActivity.class);
        startActivity(openHomeFromBlog);
        Animatoo.animateSwipeLeft(BlogActivity.this);

    }


    private void getData() {
        String url = BloggerAPI.url + "?key=" + BloggerAPI.key;
        if (token != "") {
            url = url + "&pageToken=" + token;
        }
        if (token == null) {
            return;
        }
        progress.setVisibility(View.VISIBLE);


        final Call<PostListBlog> postList = BloggerAPI.getService().getPostList(url);
        postList.enqueue(new Callback<PostListBlog>() {
            @Override
            public void onResponse(Call<PostListBlog> call, Response<PostListBlog> response) {
                PostListBlog list = response.body();
                token = list.getNextPageToken();
                blogItems.addAll(list.getBlogItems());
                adapter.notifyDataSetChanged();
                progress.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        adapter.isShimmer = false;

                        adapter.notifyDataSetChanged();
                    }
                },1500);
            }

            @Override
            public void onFailure(Call<PostListBlog> call, Throwable t) {
                Toasty.error(BlogActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        });

    }
}
