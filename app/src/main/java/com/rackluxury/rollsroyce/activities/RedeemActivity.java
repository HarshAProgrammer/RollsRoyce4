package com.rackluxury.rollsroyce.activities;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.blog.BlogActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RedeemActivity extends AppCompatActivity {
    private TextView coins2;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef, mRefStatus;

    private int usercoin;
    private float x;
    private Integer integer;
    private StorageReference storageReference;
    private float usermoney;
    private int usermoneyCoins, usercoins;
    private SharedPreferences coins;

    ViewPager viewPagerRedeem;
    com.rackluxury.rollsroyce.activities.AdapterRedeem adapterRedeem;
    List<Model> models;
    private RelativeLayout layout;



    final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Integer[] colors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        layout = findViewById(R.id.layRedeem);
        coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        redeemChoice();

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        if (dpHeight > 700) {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        ImageView imageView = findViewById(R.id.imageView4);
        Button buyMore = findViewById(R.id.btnBuyMoreRedeem);

        buyMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RedeemActivity.this, com.rackluxury.rollsroyce.activities.BuyCoinsActivity.class);
                        startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        coins2 = (TextView) findViewById(R.id.textViewCoins);
        final TextView calcmoney = (TextView) findViewById(R.id.textView6);
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds
        FirebaseDatabase database11 = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user11 = mAuth.getCurrentUser();
        String userId11 = user11.getUid();
        mRef = database11.getReference().child(userId11);
        mRef.child("RedeemCoins").removeValue();
        mRef.child("RedeemUSD").removeValue();
        mRef.child("Coins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usercoin = Integer.parseInt(dataSnapshot.getValue(String.class));
                coins2.setText(String.valueOf(usercoin));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        integer = Integer.valueOf(coins2.getText().toString());
        FirebaseDatabase database22 = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user22 = mAuth.getCurrentUser();
        String userId22 = user22.getUid();
        mRef = database22.getReference().child(userId22);

        Button button = findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (integer < usercoin && integer > 10) {

                    mRef.child("RedeemCoins").setValue(String.valueOf(integer));
                    StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Blog Purchased");
                    Uri uri1 = Uri.parse("android.resource://com.rackluxury.rollsroyce/drawable/img_blog_checker");
                    UploadTask uploadTask = imageReference1.putFile(uri1);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(RedeemActivity.this, "Please Check Your Internet Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            Toasty.success(RedeemActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user1 = mAuth.getCurrentUser();
                            String userId = user1.getUid();
                            mRef = database.getReference().child(userId);
                            mRef.child("RedeemUSD").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    usermoney = Float.parseFloat(dataSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            mRef.child("RedeemCoins").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    usermoneyCoins = Integer.parseInt(dataSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            mRef.child("Coins").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    usercoins = Integer.parseInt(dataSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            final ProgressDialog dialog = new ProgressDialog(RedeemActivity.this);
                            dialog.setTitle("Sending Email");
                            dialog.setMessage("Please wait");
                            dialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Thread sender = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                dialog.dismiss();
                                                int result = usercoins - usermoneyCoins;
                                                mRef.child("RedeemCoins").removeValue();
                                                mRef.child("RedeemUSD").removeValue();

                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                mAuth = FirebaseAuth.getInstance();
                                                FirebaseUser user1 = mAuth.getCurrentUser();
                                                String userId = user1.getUid();
                                                mRefStatus = database.getReference().child("Redeem").push();
                                                mRefStatus.child("Status").setValue("Review");

                                                mRefStatus.child("MoneyUSD").setValue(String.valueOf(usermoney));

                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(userId).child("Redeem").push();
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("id", databaseReference.getKey());
                                                map.put("Redeem", usermoney);
                                                Calendar c = Calendar.getInstance();

                                                int day = c.get(Calendar.DAY_OF_MONTH);
                                                int month = c.get(Calendar.MONTH);
                                                int year = c.get(Calendar.YEAR);
                                                String date = day + ". " + month + ". " + year;
                                                map.put("Date", date);
                                                databaseReference.setValue(map);

                                                SharedPreferences.Editor coinsEdit = coins.edit();
                                                coinsEdit.putString("Coins", String.valueOf(result));
                                                coinsEdit.apply();

                                                Intent intent = new Intent(RedeemActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();

                                            } catch (Exception e) {
                                                Log.e("mylog", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                    sender.start();
                                }
                            }, 2500);


                        }
                    });

                } else {
                    Toasty.info(RedeemActivity.this, "You don't have so many coins", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    private void redeemChoice() {
        models = new ArrayList<>();
        models.add(new Model(R.drawable.redeem_01, "1978 Rolls-Royce Corniche I - coupe", "$ 98 336"));
        models.add(new Model(R.drawable.redeem_02, "2004 Rolls-Royce Phantom VII - 6.7 V12", "$ 98 336"));
        models.add(new Model(R.drawable.redeem_03, "2020 Rolls-Royce Ghost - New Ghost", "$ 128 257"));
        models.add(new Model(R.drawable.redeem_04, "1964 Rolls-Royce Silver Cloud III - SCT100 Non Division", "$ 439 506"));
        models.add(new Model(R.drawable.redeem_05, "1982 Rolls-Royce Corniche I - Series 2", "$ 121 972"));
        models.add(new Model(R.drawable.redeem_06, "2020 Rolls-Royce Silver Wraith - V12", "$ 71 669"));
        models.add(new Model(R.drawable.redeem_07, "1985 Rolls-Royce Corniche I", "P.O.R."));
        models.add(new Model(R.drawable.redeem_08, "1932 Rolls-Royce Phantom II", "$ 78 669"));
        models.add(new Model(R.drawable.redeem_09, "1977 Rolls-Royce Silver Wraith II", "P.O.R."));
        models.add(new Model(R.drawable.redeem_10, "1961 Rolls-Royce Silver Cloud II - Drophead Coupe", "$ 42 805"));
        models.add(new Model(R.drawable.redeem_11, "1968 Rolls-Royce Silver Shadow I", "$ 399 900"));
        models.add(new Model(R.drawable.redeem_12, "1963 Rolls-Royce Silver Cloud III - James Young SCT100 Baby...", "$ 34 489"));
        models.add(new Model(R.drawable.redeem_13, "1972 Rolls-Royce Silver Shadow I", "P.O.R."));
        models.add(new Model(R.drawable.redeem_14, "1933 Rolls-Royce 20/25 H.P.", "$ 86 189"));
        models.add(new Model(R.drawable.redeem_15, "2016 Rolls-Royce Phantom Drophead - HARMONY EDITION 1 OF 1", "$ 182 790"));
        models.add(new Model(R.drawable.redeem_16, "1969 Rolls-Royce Silver Shadow I - I", "$ 476 919"));
        models.add(new Model(R.drawable.redeem_17, "1976 Rolls-Royce Camargue", "$ 30 178"));
        models.add(new Model(R.drawable.redeem_18, "1959 Rolls-Royce Silver Cloud I", "P.O.R."));
        models.add(new Model(R.drawable.redeem_19, "2020 Rolls-Royce Dawn - Convertible", "$ 403 327"));




        adapterRedeem = new com.rackluxury.rollsroyce.activities.AdapterRedeem(models, this);

        viewPagerRedeem = findViewById(R.id.viewPagerRedeem);
        viewPagerRedeem.setAdapter(adapterRedeem);
        viewPagerRedeem.setPadding(130, 0, 130, 0);

        colors = new Integer[]{
                getResources().getColor(R.color.colorRedeem1),
                getResources().getColor(R.color.colorRedeem2),
                getResources().getColor(R.color.colorRedeem3),
                getResources().getColor(R.color.colorRedeem4),
                getResources().getColor(R.color.colorRedeem5),
                getResources().getColor(R.color.colorRedeem6),
                getResources().getColor(R.color.colorRedeem7),
                getResources().getColor(R.color.colorRedeem8),
                getResources().getColor(R.color.colorRedeem9),
                getResources().getColor(R.color.colorRedeem10),
                getResources().getColor(R.color.colorRedeem11),
                getResources().getColor(R.color.colorRedeem12),
                getResources().getColor(R.color.colorRedeem13),
                getResources().getColor(R.color.colorRedeem14),
                getResources().getColor(R.color.colorRedeem15),
                getResources().getColor(R.color.colorRedeem16),
                getResources().getColor(R.color.colorRedeem17),
                getResources().getColor(R.color.colorRedeem18),
                getResources().getColor(R.color.colorRedeem19)


        };

        viewPagerRedeem.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapterRedeem.getCount() - 1) && position < (colors.length - 1)) {
                    layout.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    layout.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void openBlog(View view) {
        Intent intent = new Intent(getApplicationContext(), BlogActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSwipeLeft(RedeemActivity.this);
    }
}
