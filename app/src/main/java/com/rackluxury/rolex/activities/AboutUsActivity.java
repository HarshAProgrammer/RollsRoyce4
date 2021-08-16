package com.rackluxury.rolex.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rackluxury.rolex.R;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import tyrantgit.explosionfield.ExplosionField;

public class AboutUsActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener{
    private Toolbar toolbar;
    private ExplosionField explosionField;
    private CardView privacyCardView, rateCardView, termsCardView, feedbackCardView;
    private TextView versionTextView;
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;
    private ImageView privacyImage, termsImage, rateImage,feedbackImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        setupUIViews();
        initToolbar();
        loadAboutUsFunctionality();


    }
    private void setupUIViews() {
        toolbar = findViewById(R.id.toolbarAboutUsActivity);
        rateCardView = findViewById(R.id.cvRateAbout);
        privacyCardView = findViewById(R.id.cvPrivacyAbout);
        termsCardView = findViewById(R.id.cvTermsAbout);
        feedbackCardView = findViewById(R.id.cvFeedbackAbout);
        privacyImage = findViewById(R.id.ivPrivacyAbout);
        termsImage = findViewById(R.id.ivTermsAbout);
        rateImage = findViewById(R.id.ivRateAbout);
        feedbackImage = findViewById(R.id.ivFeedbackAbout);
        versionTextView = findViewById(R.id.tvVersionAbout);
        gestureDetector = new GestureDetector(AboutUsActivity.this, this);
        explosionField = ExplosionField.attach2Window(AboutUsActivity.this);


    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("About Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }


    private void loadAboutUsFunctionality() {
        try {
            PackageInfo pInfo = this.getApplicationContext().getPackageManager().getPackageInfo(AboutUsActivity.this.getPackageName(), 0);
            String version = pInfo.versionName;
            versionTextView.setText(getResources().getString(R.string.version_about_us) + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        rateCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(rateImage);
                rateMe();
            }
        });

        privacyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(privacyImage);
                String url = "https://rolexwatchessa.blogspot.com/2020/08/watches-from-rolex-privacy-policy.html";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        termsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(termsImage);
                String url = "https://rolexwatchessa.blogspot.com/2020/08/watches-from-rolex-terms-and-condition.html";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        feedbackCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explosionField.explode(feedbackImage);
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "rolexwatchessa@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Theme of mail");
                startActivity(Intent.createChooser(intent, "Select post client"));
            }
        });
    }


    private void rateMe() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSwipeLeft(AboutUsActivity.this);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getY() - downEvent.getY();
        float diffX = moveEvent.getX() - downEvent.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {

            if (Math.abs(diffX)> SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {

                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        }

        return result;
    }

    private void onSwipeLeft() {
        finish();
        Animatoo.animateSwipeLeft(AboutUsActivity.this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}