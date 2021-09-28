package com.rackluxury.rollsroyce.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.activities.NoInternetDialogue;
import com.rackluxury.rollsroyce.activities.models.MediaObject;
import com.rackluxury.rollsroyce.activities.util.Resources;
import com.rackluxury.rollsroyce.activities.util.VerticalSpacingItemDecorator;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class VideoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private VideoPlayerRecyclerView mRecyclerView;
    private ImageView backIcon;
    private int lastPosition;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (null == activeNetwork) {
            setNoInternetDialogue();
        }
        setupUIViews();
        initToolbar();
        initRVVideo();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setShimmer(null);
            }
        },1500);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPosVideo", 0);
        mRecyclerView.scrollToPosition(lastPosition);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = layoutManager.findFirstVisibleItemPosition();
            }
        });
    }

    private void setupUIViews() {
        toolbar = findViewById(R.id.toolbarVideoActivity);
        mRecyclerView = findViewById(R.id.rvVideoActivity);
        backIcon = findViewById(R.id.backIconVideo);
        shimmerFrameLayout =  findViewById(R.id.sflVideo);
    }
    private void initToolbar() {
        setSupportActionBar(toolbar);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSplit(VideoActivity.this);
            }
        });

    }
    private void setNoInternetDialogue() {
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(VideoActivity.this);
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



    private void initRVVideo(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(VideoActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);

        ArrayList<MediaObject> mediaObjects = new ArrayList<>(Arrays.asList(Resources.MEDIA_OBJECTS));
        mRecyclerView.setMediaObjects(mediaObjects);
        VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(mediaObjects, initGlide());
        mRecyclerView.setAdapter(adapter);
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(VideoActivity.this)
                .setDefaultRequestOptions(options);
    }


    @Override
    protected void onDestroy() {
        if(mRecyclerView!=null){
            mRecyclerView.releasePlayer();

        }
        super.onDestroy();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosVideo", lastPosition);
        e.apply();
    }
    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSplit(VideoActivity.this);

    }


}