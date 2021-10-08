package com.rackluxury.rollsroyce.video;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class VideoCheckerActivity extends AppCompatActivity implements PurchasesUpdatedListener {


    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private SharedPreferences prefs;
    private TextView people;
    private TextView purchasesRemaining;



    private BillingClient billingClient;
    private final List<String> skulist = new ArrayList<>();
    private final String categories = "video_checker";
    private TextView timer;
    private String TAG = "Main";
    private SharedPreferences coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_checker);
        people = findViewById(R.id.peopleNumVideoChecker);
        purchasesRemaining = findViewById(R.id.purchaseNumVideoChecker);

        Random randomPurchase = new Random();
        int valPurc = randomPurchase.nextInt(10);
        purchasesRemaining.setText(Integer.toString(valPurc));

        coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        Random random = new Random();
        int valPeop = random.nextInt(500); // save random number in an integer variable
        people.setText(Integer.toString(valPeop));

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        timer = findViewById(R.id.tvTimerVideo);
        Intent intent = new Intent(this, BroadcastServiceVideo.class);
        startService(intent);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("videoCheckerFirst", true);
        if (firstStart) {
            firstDialogue();
        }

        videoCheckerFunctionality();



    }
    private BroadcastReceiver broadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };
    @Override
    protected void onStop(){
        try {
            unregisterReceiver(broadcastReciever);
        }catch(Exception e){

        }
        super.onStop();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReciever);

    }
    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(broadcastReciever,new IntentFilter(BroadcastServiceVideo.COUNTDOWN_BR));

    }
    @Override
    protected void onDestroy(){
        stopService(new Intent(this, BroadcastServiceVideo.class));
        super.onDestroy();
    }
    private void updateGUI(Intent intent){
        if(intent.getExtras() != null){
            long millisUntilFinished = intent.getLongExtra("countdownVideo",300000);

            timer.setText(Long.toString(millisUntilFinished/1000));
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
            sharedPreferences.edit().putLong("timeVideo",millisUntilFinished).apply();
        }
    }
    private void firstDialogue(){
        LayoutInflater inflater = LayoutInflater.from(VideoCheckerActivity.this);
        View viewInformation = inflater.inflate(R.layout.alert_dialog_purchase_information, null);
        Button acceptButton = viewInformation.findViewById(R.id.btnOkAlertPurchaseInformation);
        final AlertDialog alertDialogInformation = new AlertDialog.Builder(VideoCheckerActivity.this)
                .setView(viewInformation)
                .show();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogInformation.dismiss();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("videoCheckerFirst", false);
                editor.apply();

            }
        });
    }


    private void videoCheckerFunctionality() {
        Toolbar toolbar = findViewById(R.id.toolbarVideoCheckerActivity);
        Button buttonVideoChecker = findViewById(R.id.btnVideoChecker);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        billingClient = BillingClient.newBuilder(VideoCheckerActivity.this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            //This method starts when user buys a categories
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                } else {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Toasty.error(VideoCheckerActivity.this, "Try Purchasing Again", Toast.LENGTH_LONG).show();
                    } else {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                            finish();
                            Intent openVideoFromVideoChecker = new Intent(VideoCheckerActivity.this, VideoActivity.class);
                            startActivity(openVideoFromVideoChecker);
                            Animatoo.animateSwipeRight(VideoCheckerActivity.this);
                        }
                    }
                }
            }
        }).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {


                } else {
                    Toasty.error(VideoCheckerActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toasty.error(VideoCheckerActivity.this, "Disconnected from the Billing Client", Toast.LENGTH_LONG).show();

            }
        });
        skulist.add(categories);
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skulist).setType(BillingClient.SkuType.INAPP);
        buttonVideoChecker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, List<SkuDetails> list) {
                        if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (final SkuDetails skuDetails : list) {
                                String sku = skuDetails.getSku();
                                String price = skuDetails.getPrice();
                                String description = skuDetails.getDescription();

                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetails)
                                        .build();
                                BillingResult responsecode = billingClient.launchBillingFlow(VideoCheckerActivity.this, flowParams);
                            }
                        }
                    }
                });
            }
        });
    }


    private void handlePurchase(Purchase purchase) {
        try {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (purchase.getSku().equals(categories)) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            Toasty.success(VideoCheckerActivity.this, "Request Acknowledged", Toast.LENGTH_LONG).show();

                        }
                    };
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                    //now you can purchase same categories again and again
                    //Here we give coins to user.

                    FirebaseMessaging.getInstance().unsubscribeFromTopic("purchase_video");

                    LayoutInflater inflater = LayoutInflater.from(VideoCheckerActivity.this);
                    View view = inflater.inflate(R.layout.alert_dialog_purchased, null);
                    Button acceptButton = view.findViewById(R.id.btnOkAlertPurchased);
                    final AlertDialog alertDialog = new AlertDialog.Builder(VideoCheckerActivity.this)
                            .setView(view)
                            .show();

                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            finish();
                            Intent openVideoFromVideoChecker = new Intent(VideoCheckerActivity.this, VideoActivity.class);
                            startActivity(openVideoFromVideoChecker);
                            Animatoo.animateSwipeRight(VideoCheckerActivity.this);
                        }
                    });

                    StorageReference imageReference1 = storageReference.child(firebaseAuth.getUid()).child("Video Purchased");
                    Uri uri1 = Uri.parse("android.resource://com.rackluxury.rolex/drawable/img_video_checker");
                    UploadTask uploadTask = imageReference1.putFile(uri1);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(VideoCheckerActivity.this, "Please Check Your Internet Connectivity", Toast.LENGTH_LONG).show();

                        }
                    });
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                            coinCount = coinCount + 100000;
                            SharedPreferences.Editor coinsEdit = coins.edit();
                            coinsEdit.putString("Coins", String.valueOf(coinCount));
                            coinsEdit.apply();
                            Toasty.success(VideoCheckerActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();

                        }
                    });


                }
            }
        } catch (Exception e) {
            Toasty.error(VideoCheckerActivity.this, "Transaction Failed", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Toasty.info(VideoCheckerActivity.this, "onPurchases Updated", Toast.LENGTH_LONG).show();

    }


    @Override
    public void onBackPressed() {
        finish();
        Intent openHomeFromVideoChecker = new Intent(VideoCheckerActivity.this, HomeActivity.class);
        startActivity(openHomeFromVideoChecker);
        Animatoo.animateSwipeLeft(VideoCheckerActivity.this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}