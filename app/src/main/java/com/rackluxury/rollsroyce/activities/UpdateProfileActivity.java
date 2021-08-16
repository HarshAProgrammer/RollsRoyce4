package com.rackluxury.rollsroyce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.adapters.UserProfile;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class UpdateProfileActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener{

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private EditText newUserName, newUserEmail, newUserPhoneNo;
    private FirebaseAuth firebaseAuth;
    private ImageView updateProfilePic;
    private static final int PICK_IMAGE = 123;
    Uri imagePath;
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                updateProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        textInputEmail = findViewById(R.id.update_profile_email_layout);
        textInputUsername = findViewById(R.id.update_profile_username_layout);
        Toolbar toolbar = findViewById(R.id.toolbarProfileUpdateActivity);
        newUserName = findViewById(R.id.etNameUpdateProfile);
        newUserEmail = findViewById(R.id.etEmailUpdateProfile);
        newUserPhoneNo = findViewById(R.id.etPhoneNoUpdateProfile);
        final Button save = findViewById(R.id.btnUpdateProfile);
        updateProfilePic = findViewById(R.id.ivProfileUpdate);
        gestureDetector = new GestureDetector(UpdateProfileActivity.this,this);




        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Change Profile Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newUserName.setText(userProfile.getUserName());
                newUserPhoneNo.setText(userProfile.getUserPhoneNo());
                newUserEmail.setText(userProfile.getUserEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(UpdateProfileActivity.this,databaseError.getCode(), Toast.LENGTH_LONG).show();

            }
        });

        final StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(updateProfilePic);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateUpdateProfile()) {
                    save.setEnabled(false);

                    String name = newUserName.getText().toString();
                    String email = newUserEmail.getText().toString();
                    String phone_no = newUserPhoneNo.getText().toString();


                    UserProfile userProfile = new UserProfile(name, email, phone_no);

                    databaseReference.setValue(userProfile);

                    StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");  //User id/Images/Profile Pic.jpg
                    UploadTask uploadTask = imageReference.putFile(imagePath);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(UpdateProfileActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Toasty.success(UpdateProfileActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                    finish();
                }
            }
        });

        updateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("images/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
                Animatoo.animateDiagonal(UpdateProfileActivity.this);
            }
        });
    }
    private boolean validateUpdateProfile() {
        boolean result ;

        String usernameInput = textInputUsername.getEditText().getText().toString().trim();
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();



        if (usernameInput.isEmpty()) {
            textInputUsername.setError("Field can't be empty");
            result = false;
        } else if (usernameInput.length() > 15) {
            textInputUsername.setError(null);
            textInputUsername.setError("Username too long");
            result = false;
        }else if(emailInput.isEmpty()){
            textInputUsername.setError(null);
            textInputEmail.setError("Field can't be empty");
            result = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            textInputEmail.setError(null);
            textInputEmail.setError("Please enter a valid email address");
            result = false;
        }else if(imagePath == null){
            result = false;
            textInputEmail.setError(null);
            Toasty.warning(UpdateProfileActivity.this, "Please Upload a Profile Pic ", Toast.LENGTH_LONG).show();
        }else if(null == activeNetwork){
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
        final NoInternetDialogue noInternetDialogue = new NoInternetDialogue(UpdateProfileActivity.this);
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
        Intent onBackUpdateProfile = new Intent(UpdateProfileActivity.this,ProfileActivity.class);
        startActivity(onBackUpdateProfile);
        Animatoo.animateSlideUp(UpdateProfileActivity.this);

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
        Intent onBackUpdateProfile = new Intent(UpdateProfileActivity.this,ProfileActivity.class);
        startActivity(onBackUpdateProfile);
        Animatoo.animateSlideUp(UpdateProfileActivity.this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
