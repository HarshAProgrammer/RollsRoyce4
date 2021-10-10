package com.rackluxury.rollsroyce.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.adapters.UserProfile;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {


    private UserProfile userProfile;
    private PhotoView profilePic;


    private TextView profileName, profilePhoneNo, profileEmail;
    private Button profileUpdate, changePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePic = findViewById(R.id.ivProfilePic);
        profileName = findViewById(R.id.tvProfileName);
        profilePhoneNo = findViewById(R.id.tvProfilePhoneNo);
        profileEmail = findViewById(R.id.tvProfileEmail);
        profileUpdate = findViewById(R.id.btnProfileUpdate);
        changePassword = findViewById(R.id.btnChangePassword);


        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null == activeNetwork) {
            setNoInternetDialogue();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();



        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(profilePic);
                final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
                displayDataEmailPassword(databaseReference);
                profileUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        profilePic.animate().alpha(0).translationX(-profileName.getHeight()).setDuration(1000);
                        profileName.animate().alpha(0).translationX(-profileName.getHeight()).setDuration(1000);
                        profileEmail.animate().alpha(0).translationX(-profileName.getHeight()).setDuration(1000);
                        profilePhoneNo.animate().alpha(0).translationX(-profileName.getHeight()).setDuration(1000);

                        Handler handler = new Handler();
                        int TRANSITION_SCREEN_LOADING_TIME = 1000;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
                                Animatoo.animateSlideDown(ProfileActivity.this);
                            }
                        }, TRANSITION_SCREEN_LOADING_TIME);
                    }
                });
                changePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        startActivity(new Intent(ProfileActivity.this, UpdatePasswordActivity.class));
                        Animatoo.animateSlideUp(ProfileActivity.this);
                    }
                });
            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user.getPhotoUrl() != null) {
                    String photoUrl = user.getPhotoUrl().toString();
                    photoUrl = photoUrl + "?type=large";
                    Picasso.get().load(photoUrl).into(profilePic);
                    displayDataFacebook();
                    changePassword.setVisibility(View.GONE);
                    changePassword.setEnabled(false);
                    profileUpdate.setVisibility(View.GONE);
                    profileUpdate.setEnabled(false);

                } else {
                    profilePic.setImageResource(R.drawable.splashscreen);
                }
            }
        });



    }

    private void setNoInternetDialogue() {
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(ProfileActivity.this);
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


    private void displayDataFacebook() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String displayName;
        String displayEmail;
        displayName = user.getDisplayName().trim();
        displayEmail = user.getEmail().trim();


        profileName.setText(displayName);
        profileEmail.setText(displayEmail);
        profilePhoneNo.setText("");
    }

    private void displayDataEmailPassword(DatabaseReference databaseReference) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);


                profileName.setText(userProfile.getUserName());
                profileEmail.setText(userProfile.getUserEmail());
                profilePhoneNo.setText(userProfile.getUserPhoneNo());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(ProfileActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();

            }
        });
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
        Animatoo.animateSwipeLeft(ProfileActivity.this);

    }


}
