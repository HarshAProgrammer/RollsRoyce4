package com.rackluxury.rollsroyce.activities;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.ybq.android.spinkit.SpinKitView;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.adapters.ImagesAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImagesActivity extends AppCompatActivity implements ImagesAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private ImageView backIcon;
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_CREATOR = "creatorName";
    public static final String EXTRA_VIEWS = "viewCount";
    public static final String EXTRA_LIKES = "likeCount";
    public static final String EXTRA_COMMENTS = "commentCount";
    public static final String EXTRA_DOWNLOADS = "downloadCount";
    private RecyclerView mRecyclerView;
    private ImagesAdapter mImagesAdapter;
    private List<ImageItem> mImagesList  = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private int lastPosition;
    private SpinKitView spinKitView;
    private ImagesAdapter adapter;
    private ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        toolbar = findViewById(R.id.toolbarImagesActivity);
        backIcon = findViewById(R.id.backIconImages);
        mRecyclerView = findViewById(R.id.rvImages);
        spinKitView = findViewById(R.id.spin_kit_images);
        adapter = new ImagesAdapter(this, mImagesList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setAdapter(adapter);

        shimmerFrameLayout = findViewById(R.id.sflImages);


        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null == activeNetwork) {
            setNoInternetDialogue();
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRequestQueue = Volley.newRequestQueue(this);

        initToolbar();

        parseJSON();

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPosImages", 0);
        mRecyclerView.scrollToPosition(lastPosition);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = layoutManager.findFirstVisibleItemPosition();
            }
        });

    }
    private void setNoInternetDialogue() {
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(ImagesActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosImages", lastPosition);
        e.apply();
    }
    private void initToolbar() {
        setSupportActionBar(toolbar);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSwipeLeft(ImagesActivity.this);
            }
        });
    }

    private void parseJSON() {
        spinKitView.setVisibility(View.VISIBLE);
        String url = "https://pixabay.com/api/?key=17888137-fb88a08720bbdf7baeeb4aa99&q=rolex&image_type=photo&pretty=true";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("hits");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String creatorName = hit.getString("user");
                                String imageUrl = hit.getString("webformatURL");
                                int viewCount = hit.getInt("views");
                                int likeCount = hit.getInt("likes");
                                int commentCount = hit.getInt("comments");
                                int downloadCount = hit.getInt("downloads");
                                mImagesList.add(new ImageItem(imageUrl, creatorName, viewCount, likeCount, commentCount, downloadCount));
                            }
                            mImagesAdapter = new ImagesAdapter(ImagesActivity.this, mImagesList);
                            spinKitView.setVisibility(View.GONE);
                            shimmerFrameLayout.startShimmer();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    shimmerFrameLayout.stopShimmer();
                                    shimmerFrameLayout.setShimmer(null);
                                }
                            },1500);

                            mRecyclerView.setAdapter(mImagesAdapter);
                            mImagesAdapter.setOnItemClickListener(ImagesActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinKitView.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, ImagesDetailActivity.class);
        ImageItem clickedItem = mImagesList.get(position);
        detailIntent.putExtra(EXTRA_URL, clickedItem.getImageUrl());
        detailIntent.putExtra(EXTRA_CREATOR, clickedItem.getCreator());
        detailIntent.putExtra(EXTRA_VIEWS, clickedItem.getViewCount());
        detailIntent.putExtra(EXTRA_LIKES, clickedItem.getLikeCount());
        detailIntent.putExtra(EXTRA_COMMENTS, clickedItem.getCommentCount());
        detailIntent.putExtra(EXTRA_DOWNLOADS, clickedItem.getDownloadCount());
        startActivity(detailIntent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.images_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_images_views) {
            sortImagesViews();
            return true;
        } else if (item.getItemId() == R.id.sort_images_likes) {
            sortImagesLikes();
            return true;
        } else if (item.getItemId() == R.id.sort_images_comments) {
            sortImagesComments();
            return true;
        } else if (item.getItemId() == R.id.sort_images_downloads) {
            sortImagesDownloads();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sortImagesViews() {
        Collections.sort(mImagesList, ImageItem.ByViews);
        mImagesAdapter.notifyDataSetChanged();
    }
    public void sortImagesLikes() {
        Collections.sort(mImagesList, ImageItem.ByLikes);
        mImagesAdapter.notifyDataSetChanged();
    }
    public void sortImagesComments() {
        Collections.sort(mImagesList, ImageItem.ByComments);
        mImagesAdapter.notifyDataSetChanged();
    }
    public void sortImagesDownloads() {
        Collections.sort(mImagesList, ImageItem.ByDownloads);
        mImagesAdapter.notifyDataSetChanged();
    }



    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSwipeLeft(ImagesActivity.this);
    }
}
