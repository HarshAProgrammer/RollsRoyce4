package com.rackluxury.rollsroyce.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.blog.BlogActivity;
import com.rackluxury.rolex.blog.BlogCheckerActivity;
import com.rackluxury.rolex.facts.FactsActivity;
import com.rackluxury.rolex.images.ImagesActivity;
import com.rackluxury.rolex.reddit.activities.RedditMainActivity;
import com.rackluxury.rolex.video.VideoActivity;
import com.rackluxury.rolex.video.VideoCheckerActivity;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;


public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {


    CardView oneDashboard;
    CardView twoDashboard;
    CardView threeDashboard;
    CardView fourDashboard;
    CardView fiveDashboard;
    CardView sixDashboard;
    CardView sevenDashboard;
    CardView eighthDashboard;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private long backPressedTime;
    ImageView greetImg;
    TextView greetText;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        setViews();
        greeting();

    }

    private void setViews() {
        oneDashboard = findViewById(R.id.homeDashboard);
        twoDashboard = findViewById(R.id.dailyLoginDashboard);
        threeDashboard = findViewById(R.id.redditDashboard);
        fourDashboard = findViewById(R.id.videoDashboard);
        fiveDashboard = findViewById(R.id.imagesDashboard);
        sixDashboard = findViewById(R.id.blogDashboard);
        sevenDashboard = findViewById(R.id.expensiveDashboard);
        eighthDashboard = findViewById(R.id.factsDashboard);

        oneDashboard.setOnClickListener(this);
        twoDashboard.setOnClickListener(this);
        threeDashboard.setOnClickListener(this);
        fourDashboard.setOnClickListener(this);
        fiveDashboard.setOnClickListener(this);
        sixDashboard.setOnClickListener(this);
        sevenDashboard.setOnClickListener(this);
        eighthDashboard.setOnClickListener(this);

        greetImg = findViewById(R.id.ivGreetDashboard);
        greetText = findViewById(R.id.tvGreetDashboard);
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("dashboardFirst", true);
        if (firstStart) {
            coinRewardToast();
        }


    }

    private void coinRewardToast() {

        Toasty.normal(DashboardActivity.this, "You have recieved 10,000 coins as a lucky user.", Toast.LENGTH_LONG, ContextCompat.getDrawable(DashboardActivity.this, R.drawable.coinmain)).show();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dashboardFirst", false);
        editor.apply();
    }

    private void greeting() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greetText.setText("Good Morning");
            greetImg.setImageResource(R.drawable.img_greet_half_morning);
        } else if (timeOfDay >= 12 && timeOfDay < 15) {
            greetText.setText("Good Afternoon");
            greetImg.setImageResource(R.drawable.img_greet_half_afternoon);
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            greetText.setText("Good Evening");
            greetImg.setImageResource(R.drawable.img_greet_half_without_sun);
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            greetText.setText("Good Night");
            greetText.setTextColor(Color.WHITE);
            greetImg.setImageResource(R.drawable.img_greet_half_night);
        }
    }

    @Override
    public void onClick(View v) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        switch (v.getId()) {
            case R.id.homeDashboard:
                Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
                startActivity(intent);
                Animatoo.animateSwipeRight(DashboardActivity.this);
                break;
            case R.id.dailyLoginDashboard:
                Intent intent2 = new Intent(DashboardActivity.this, com.rackluxury.rolex.activities.DailyLoginActivity.class);
                startActivity(intent2);
                Animatoo.animateSwipeRight(DashboardActivity.this);
                break;
            case R.id.redditDashboard:
                Intent intent3 = new Intent(DashboardActivity.this, RedditMainActivity.class);
                startActivity(intent3);
                Animatoo.animateSwipeRight(DashboardActivity.this);
                break;
            case R.id.videoDashboard:
                storageReference.child(firebaseAuth.getUid()).child("Video Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        finish();
                        Intent openVideoFromMain = new Intent(DashboardActivity.this, VideoActivity.class);
                        startActivity(openVideoFromMain);
                        Animatoo.animateSwipeRight(DashboardActivity.this);

                    }
                });
                storageReference.child(firebaseAuth.getUid()).child("Video Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseMessaging.getInstance().subscribeToTopic("purchase_video");
                        finish();
                        Intent openVideoCheckerFromMain = new Intent(DashboardActivity.this, VideoCheckerActivity.class);
                        startActivity(openVideoCheckerFromMain);
                        Animatoo.animateSwipeRight(DashboardActivity.this);

                    }
                });
                break;
            case R.id.imagesDashboard:
                Intent intent5 = new Intent(DashboardActivity.this, ImagesActivity.class);
                startActivity(intent5);
                Animatoo.animateSwipeRight(DashboardActivity.this);
                break;
            case R.id.blogDashboard:
                storageReference.child(firebaseAuth.getUid()).child("Blog Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        finish();
                        Intent openBlogFromMain = new Intent(DashboardActivity.this, BlogActivity.class);
                        startActivity(openBlogFromMain);
                        Animatoo.animateSwipeRight(DashboardActivity.this);

                    }
                });
                storageReference.child(firebaseAuth.getUid()).child("Blog Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseMessaging.getInstance().subscribeToTopic("purchase_blog");
                        finish();
                        Intent openBlogCheckerFromMain = new Intent(DashboardActivity.this, BlogCheckerActivity.class);
                        startActivity(openBlogCheckerFromMain);
                        Animatoo.animateSwipeRight(DashboardActivity.this);

                    }
                });
                break;
            case R.id.expensiveDashboard:
                storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        finish();
                        Intent openExpensiveFromMain = new Intent(DashboardActivity.this, ExpensiveActivity.class);
                        startActivity(openExpensiveFromMain);
                        Animatoo.animateSwipeRight(DashboardActivity.this);

                    }
                });
                storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseMessaging.getInstance().subscribeToTopic("purchase_expensive");
                        finish();
                        Intent openExpensiveCheckerFromMain = new Intent(DashboardActivity.this, ExpensiveCheckerActivity.class);
                        startActivity(openExpensiveCheckerFromMain);
                        Animatoo.animateSwipeRight(DashboardActivity.this);

                    }
                });
                break;
            case R.id.factsDashboard:
                Intent intent8 = new Intent(DashboardActivity.this, FactsActivity.class);
                startActivity(intent8);
                Animatoo.animateSwipeRight(DashboardActivity.this);
                break;

        }

    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toasty.normal(DashboardActivity.this, "Click Back again to Exit", Toast.LENGTH_SHORT, ContextCompat.getDrawable(DashboardActivity.this, R.drawable.ic_main_exit_toast)).show();
        }
        backPressedTime = System.currentTimeMillis();

    }


    public void Disappear(View view) {
        RelativeLayout rel = findViewById(R.id.exitLayout);
        rel.setVisibility(View.GONE);
    }
}