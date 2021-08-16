package com.rackluxury.rollsroyce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rackluxury.rollsroyce.R;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class FirstSplashScreenActivity extends AppCompatActivity {



    Animation topAnim, bottomAnim;
    ImageView splashImage;
    TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first_splash_screen);

        setUpUIViewsSplashScreen();

        setSplashAnimation();

        openSecondSplashActivityFromFirst();


    }

    private void setUpUIViewsSplashScreen() {
        topAnim = AnimationUtils.loadAnimation(FirstSplashScreenActivity.this, R.anim.topsplashanimation);
        bottomAnim = AnimationUtils.loadAnimation(FirstSplashScreenActivity.this, R.anim.bottomsplashanimation);
        splashImage = findViewById(R.id.image_splash_first);
        splashText = findViewById(R.id.text_splash_first);
    }

    private void setSplashAnimation() {
        splashImage.setAnimation(topAnim);
        splashText.setAnimation(bottomAnim);


    }

    private void openSecondSplashActivityFromFirst() {
        int FIRST_SPLASH_SCREEN_TIME = 1400;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent openSecondSplashActivityFromFirst = new Intent(FirstSplashScreenActivity.this, SecondSplashScreenActivity.class);
                startActivity(openSecondSplashActivityFromFirst);
                finish();
                Animatoo.animateSlideUp(FirstSplashScreenActivity.this);
            }
        }, FIRST_SPLASH_SCREEN_TIME);
    }

    @Override
    public void onBackPressed() {

    }
}
