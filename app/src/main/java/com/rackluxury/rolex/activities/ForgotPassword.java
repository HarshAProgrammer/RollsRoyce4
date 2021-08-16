package com.rackluxury.rolex.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.login.Login;
import com.rackluxury.rolex.R;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.royrodriguez.transitionbutton.TransitionButton;

import es.dmoral.toasty.Toasty;

public class ForgotPassword extends AppCompatActivity implements
        GestureDetector.OnGestureListener{

    private TextInputLayout textInputEmail;
    private Toolbar toolbar;
    private EditText passwordEmail;
    private TransitionButton resetPassword;
    private FirebaseAuth firebaseAuth;
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setupUIViewsForgotPassword();
        loadForgotPasswordFunctionality();






    }
    private void setupUIViewsForgotPassword() {
        textInputEmail = findViewById(R.id.forgot_password_email_layout);
        toolbar = findViewById(R.id.toolbarForgotPasswordActivity);
        passwordEmail = findViewById(R.id.etPasswordEmail);
        resetPassword = findViewById(R.id.btnPasswordReset);
        firebaseAuth = FirebaseAuth.getInstance();
        gestureDetector = new GestureDetector(ForgotPassword.this, this);


    }


    private void loadForgotPasswordFunctionality() {

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Forgot Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForgotPassword()){
                    resetPassword.startAnimation();
                    resetPassword.setEnabled(false);
                    String user_email = passwordEmail.getText().toString().trim();
                    firebaseAuth.sendPasswordResetEmail(user_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                resetPassword.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {
                                        Toasty.info(ForgotPassword.this, "Password Reset Email Sent To Registered Email Address", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                        Animatoo.animateSlideUp( ForgotPassword.this);
                                        finish();

                                    }
                                });

                            } else {
                                resetPassword.setEnabled(true);
                                resetPassword.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                Toasty.warning(ForgotPassword.this, "Register Your Email Address First", Toast.LENGTH_LONG).show();
                                Toasty.warning(ForgotPassword.this, "Check Your Internet Connectivity", Toast.LENGTH_LONG).show();


                            }
                        }
                    });
                }

            }
        });
        passwordEmail.addTextChangedListener(forgotPasswordTextWatcher);

    }
    private TextWatcher forgotPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = passwordEmail.getText().toString().trim();
            resetPassword.setEnabled(!emailInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    private boolean validateForgotPassword() {
        boolean result ;

        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();


        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Please enter a valid email address");
            result = false;
        } else if(null == activeNetwork){
            textInputEmail.setError(null);
            setNoInternetDialogue();
            result = false;
        }else{
            textInputEmail.setError(null);
            result = true;
        }
        return result;


    }
    private void setNoInternetDialogue() {
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(ForgotPassword.this);
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
        Animatoo.animateSlideUp(ForgotPassword.this);

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
                result = true;
            }
        } else {

            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY)> SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {

                } else {
                    onSwipeTop();
                }
                result = true;
            }
        }

        return result;
    }
    private void onSwipeTop() {
        finish();
        Animatoo.animateSlideUp(ForgotPassword.this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
