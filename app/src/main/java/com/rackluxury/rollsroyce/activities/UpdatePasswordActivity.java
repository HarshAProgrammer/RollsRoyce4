package com.rackluxury.rollsroyce.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rackluxury.rollsroyce.R;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class UpdatePasswordActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener {

    private EditText newPassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextInputLayout textInputPassword;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "(?=.*[0-9])" +
                            "(?=.*[a-zA-Z])" +
                            "(?=.*[@#$%^&+=])" +
                            "(?=\\S+$)" +
                            ".{4,}"
            );
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        textInputPassword = findViewById(R.id.update_password_password_layout);
        Toolbar toolbar = findViewById(R.id.toolbarUpdatePasswordActivity);
        final TransitionButton update = findViewById(R.id.btnUpdatePassword);
        newPassword = findViewById(R.id.etNewPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        gestureDetector = new GestureDetector(UpdatePasswordActivity.this, this);



        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (validateUpdatePassword()) {
                    update.startAnimation();
                    update.setEnabled(false);
                    String userPasswordNew = newPassword.getText().toString();
                    firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                update.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {


                                        firebaseAuth.signOut();
                                        Intent intent = new Intent(UpdatePasswordActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                        Animatoo.animateWindmill(UpdatePasswordActivity.this);
                                        finish();

                                    }
                                });



                            } else {
                                update.setEnabled(true);
                                update.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                Toasty.error(UpdatePasswordActivity.this, "Password Update Failed", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        });


    }

    private boolean validateUpdatePassword() {
        boolean result;

        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();


        if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword.setError("Password should contain at least 4 characters with no white spaces and at least 1 digit, 1 letter and 1 special character.");
            result = false;
        } else if (null == activeNetwork) {
            textInputPassword.setError(null);
            setNoInternetDialogue();
            result = false;
        } else {
            textInputPassword.setError(null);
            result = true;
        }
        return result;


    }

    private void setNoInternetDialogue() {
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(UpdatePasswordActivity.this);
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
        Intent onBackUpdatePassword = new Intent(UpdatePasswordActivity.this, ProfileActivity.class);
        startActivity(onBackUpdatePassword);
        Animatoo.animateSlideDown(UpdatePasswordActivity.this);

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

                result = true;
            }
        } else {

            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                }
                result = true;
            }
        }

        return result;
    }


    private void onSwipeBottom() {
        finish();
        Intent onBackUpdatePassword = new Intent(UpdatePasswordActivity.this, ProfileActivity.class);
        startActivity(onBackUpdatePassword);
        Animatoo.animateSlideDown(UpdatePasswordActivity.this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}