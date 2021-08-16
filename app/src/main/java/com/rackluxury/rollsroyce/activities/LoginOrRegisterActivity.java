package com.rackluxury.rollsroyce.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.databinding.ActivityLoginOrRegisterBinding;

public class LoginOrRegisterActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener {
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    ActivityLoginOrRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GestureDetector gestureDetector;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        gestureDetector = new GestureDetector(LoginOrRegisterActivity.this, this);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                }
            }
        };


        if (user != null) {
            finish();
            Intent openSplashFromIntro = new Intent(getApplicationContext(), FirstSplashScreenActivity.class);
            startActivity(openSplashFromIntro);
            Animatoo.animateSlideUp(LoginOrRegisterActivity.this);
        } else {
            binding = DataBindingUtil.setContentView(LoginOrRegisterActivity.this, R.layout.activity_login_or_register);
            prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            boolean firstStart = prefs.getBoolean("loginOrRegisterFirst", true);
            if (firstStart) {
                onFirst();
            }

        }


    }

    public void onFirst() {


        LayoutInflater inflater = LayoutInflater.from(LoginOrRegisterActivity.this);
        View view = inflater.inflate(R.layout.alert_dialog_registration, null);
        Button acceptButton = view.findViewById(R.id.btnAcceptAlertRegistration);
        Button cancelButton = view.findViewById(R.id.btnRejectAlertRegistration);
        final AlertDialog alertDialog = new AlertDialog.Builder(LoginOrRegisterActivity.this)
                .setView(view)
                .show();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("prefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("loginOrRegisterFirst", false)
                        .apply();
                alertDialog.dismiss();


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public void login(View view) {
        finish();
        startActivity(new Intent(LoginOrRegisterActivity.this, LoginActivity.class));
        Animatoo.animateSlideRight(LoginOrRegisterActivity.this);

    }

    public void getStarted(View view) {
        finish();
        startActivity(new Intent(LoginOrRegisterActivity.this, RegistrationActivity.class));
        Animatoo.animateSlideLeft(LoginOrRegisterActivity.this);
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

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
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
        startActivity(new Intent(LoginOrRegisterActivity.this, RegistrationActivity.class));
        Animatoo.animateSlideLeft(LoginOrRegisterActivity.this);
    }

    private void onSwipeRight() {
        finish();
        startActivity(new Intent(LoginOrRegisterActivity.this, LoginActivity.class));
        Animatoo.animateSlideRight(LoginOrRegisterActivity.this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}