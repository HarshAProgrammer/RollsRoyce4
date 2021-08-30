package com.rackluxury.rollsroyce.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rackluxury.rollsroyce.R;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener {


    private CallbackManager mCallbackManager;
    private static final String TAG = "FacebookAuthentication";
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private EditText loginEmail;
    private EditText loginPassword;
    private TextView Info;
    private Button Login;
    private int counter = 5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword;


    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;
    private SpinKitView spinKitView;


    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputEmail = findViewById(R.id.login_email_layout);
        textInputPassword = findViewById(R.id.login_password_layout);
        loginEmail = findViewById(R.id.etEmailLogin);
        loginPassword = findViewById(R.id.etPasswordLogin);
        Login = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.tvForgotPasswordLogin);
        Info = findViewById(R.id.tvInfoLogin);
        Info.setText(getResources().getString(R.string.info_old_login));
        userRegistration = findViewById(R.id.tvRegisterLogin);
        LoginButton facebookLoginButton = findViewById(R.id.facebook_login_button);
        gestureDetector = new GestureDetector(LoginActivity.this, this);
        spinKitView = findViewById(R.id.spin_kit_login);



        firebaseAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookLoginButton.setReadPermissions("email", "public_profile");

        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");


            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError" + error);

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                }
            }
        };
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) ;
                firebaseAuth.signOut();
            }
        };


        loadLoginFunctionality();
        loginEmail.addTextChangedListener(loginTextWatcher);
        loginPassword.addTextChangedListener(loginTextWatcher);


    }
    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = loginEmail.getText().toString().trim();
            String passwordInput = loginPassword.getText().toString().trim();
            Login.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void handleFacebookToken(AccessToken token) {
        Log.d(TAG, "handleFacebookToken" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (task.isSuccessful()) {
                    Log.d(TAG, "sign in with credential: successful");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    setFacebookLoginDialogue();
                    Handler handler = new Handler();
                    int TRANSITION_SCREEN_LOADING_TIME = 4500;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Intent openHomeActivityFromLogin = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(openHomeActivityFromLogin);
                            Toasty.success(LoginActivity.this, "Facebook Login Successful", Toast.LENGTH_LONG).show();
                            Animatoo.animateSlideUp(LoginActivity.this);
                        }
                    }, TRANSITION_SCREEN_LOADING_TIME);

                } else {
                    Log.d(TAG, "sign in with credential: failed", task.getException());
                    Toasty.error(LoginActivity.this, "Facebook Authentication Failed", Toast.LENGTH_LONG).show();

                    FirebaseUser user = null;

                }

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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


    private void loadLoginFunctionality() {
        Login.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                if (validateLogin()) {
                    spinKitView.setVisibility(View.VISIBLE);
                    Login.setEnabled(false);
                    validate(loginEmail.getText().toString(), loginPassword.getText().toString());

                }
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View v) {

                openRegistrationActivityFromLogin();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openForgotPasswordActivityFromLogin = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(openForgotPasswordActivityFromLogin);
                Animatoo.animateSlideDown(LoginActivity.this);

            }
        });

    }


    private boolean validateLogin() {
        boolean result;

        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();


        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Please enter a valid email address");
            result = false;
        }else if (null == activeNetwork) {
            textInputEmail.setError(null);
            setNoInternetDialogue();
            result = false;
        } else {
            textInputEmail.setError(null);
            result = true;
        }
        return result;


    }

    private void setNoInternetDialogue() {
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(LoginActivity.this);
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


    public void openRegistrationActivityFromLogin() {
        Intent openRegistrationActivityFromLogin = new Intent(LoginActivity.this, com.rackluxury.rollsroyce.activities.RegistrationActivity.class);
        startActivity(openRegistrationActivityFromLogin);
        finish();
        Animatoo.animateSwipeLeft(LoginActivity.this);

    }

    private void validate(String userEmail, String userPassword) {

        Boolean result = false;

        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();


        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    checkEmailVerification();
                } else {

                    Toasty.error(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                    counter--;
                    spinKitView.setVisibility(View.GONE);
                    Info.setText(getResources().getString(R.string.info_new_login) + counter);
                    if (counter == 0) {
                        Login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toasty.error(LoginActivity.this, "You Have Exceeded Maximum Number of Tries!", Toast.LENGTH_LONG).show();
                                Login.setEnabled(false);
                            }
                        });
                    }
                }
            }
        });


    }

    private void checkEmailVerification() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean email_flag = user.isEmailVerified();
        if (email_flag) {
            spinKitView.setVisibility(View.GONE);
            setLoginDialogue();
            Handler handler = new Handler();
            int TRANSITION_SCREEN_LOADING_TIME = 4500;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toasty.success(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    finish();
                    Intent openHomeActivityFromLogin = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(openHomeActivityFromLogin);
                    Animatoo.animateSlideUp(LoginActivity.this);


                }
            }, TRANSITION_SCREEN_LOADING_TIME);

        } else {
            Login.setEnabled(false);
            spinKitView.setVisibility(View.GONE);
            Toasty.info(LoginActivity.this, "Verify Your Email Address", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }


    }

    private void setLoginDialogue() {
        final LoginDialogue loginDialogue = new LoginDialogue(LoginActivity.this);
        loginDialogue.startLoginDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void setFacebookLoginDialogue() {
        final FacebookLoginDialogue facebookLoginDialogue = new FacebookLoginDialogue(LoginActivity.this);
        facebookLoginDialogue.startFacebookLoginDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                facebookLoginDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSlideLeft(LoginActivity.this);

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
        startActivity(new Intent(LoginActivity.this, LoginOrRegisterActivity.class));
        Animatoo.animateSlideLeft(LoginActivity.this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}


