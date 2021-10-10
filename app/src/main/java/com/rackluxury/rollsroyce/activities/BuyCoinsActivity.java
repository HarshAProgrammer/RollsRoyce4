package com.rackluxury.rollsroyce.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rackluxury.rollsroyce.R;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class BuyCoinsActivity extends AppCompatActivity implements PurchasesUpdatedListener,
        GestureDetector.OnGestureListener {

    private Toolbar toolbar;
    private CardView buy_one;
    private CardView buy_two;
    private CardView buy_three;
    private BillingClient billingClient;
    private final List<String> skulist_one = new ArrayList<>();
    private final List<String> skulist_two = new ArrayList<>();
    private final List<String> skulist_three = new ArrayList<>();
    private final String coins_gold = "purchase_coins_gold";
    private final String coins_silver = "purchase_coins_silver";
    private final String coins_bronze = "purchase_coins_bronze";

    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GestureDetector gestureDetector;
    private SharedPreferences coins;
    private String currentCoins;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);

        setupUIViews();
        billingFunctionality();

    }

    private void setupUIViews() {
        coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        buy_one = findViewById(R.id.cvOneBuyCoin);
        buy_two = findViewById(R.id.cvTwoBuyCoin);
        buy_three = findViewById(R.id.cvThreeBuyCoin);
        gestureDetector = new GestureDetector(BuyCoinsActivity.this, this);

    }



    private void billingFunctionality() {
        billingClient = BillingClient.newBuilder(BuyCoinsActivity.this).enablePendingPurchases().setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (Purchase purchase : list) {
                        handlePurchase(purchase);
                    }
                } else {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                        Toasty.error(BuyCoinsActivity.this, "Try Purchasing Again", Toast.LENGTH_LONG).show();
                    } else {

                    }
                }
            }
        }).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {


                } else {
                    Toasty.error(BuyCoinsActivity.this, "Failed to connect", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toasty.error(BuyCoinsActivity.this, "Disconnected from the Client", Toast.LENGTH_LONG).show();

            }
        });
        skulist_one.add(coins_gold);
        final SkuDetailsParams.Builder paramsOne = SkuDetailsParams.newBuilder();
        paramsOne.setSkusList(skulist_one).setType(BillingClient.SkuType.INAPP);
        buy_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingClient.querySkuDetailsAsync(paramsOne.build(), new SkuDetailsResponseListener() {
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
                                BillingResult responsecode = billingClient.launchBillingFlow(BuyCoinsActivity.this, flowParams);
                            }
                        }
                    }
                });
            }
        });
        skulist_two.add(coins_silver);
        final SkuDetailsParams.Builder paramsSecond = SkuDetailsParams.newBuilder();
        paramsSecond.setSkusList(skulist_two).setType(BillingClient.SkuType.INAPP);
        buy_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingClient.querySkuDetailsAsync(paramsSecond.build(), new SkuDetailsResponseListener() {
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
                                BillingResult responsecode = billingClient.launchBillingFlow(BuyCoinsActivity.this, flowParams);
                            }
                        }
                    }
                });
            }
        });
        skulist_three.add(coins_bronze);
        final SkuDetailsParams.Builder paramsThree = SkuDetailsParams.newBuilder();
        paramsThree.setSkusList(skulist_three).setType(BillingClient.SkuType.INAPP);
        buy_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                billingClient.querySkuDetailsAsync(paramsThree.build(), new SkuDetailsResponseListener() {
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
                                BillingResult responsecode = billingClient.launchBillingFlow(BuyCoinsActivity.this, flowParams);
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
                if (purchase.getSku().equals(coins_gold)) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            Toasty.success(BuyCoinsActivity.this, "Request Acknowledged", Toast.LENGTH_LONG).show();

                        }
                    };


                    LayoutInflater inflater = LayoutInflater.from(BuyCoinsActivity.this);
                    View view = inflater.inflate(R.layout.alert_dialog_purchased, null);
                    Button acceptButton = view.findViewById(R.id.btnOkAlertPurchased);
                    final AlertDialog alertDialog = new AlertDialog.Builder(BuyCoinsActivity.this)
                            .setView(view)
                            .show();

                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                        }
                    });
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                    int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                    coinCount = coinCount + 15000;
                    SharedPreferences.Editor coinsEdit = coins.edit();
                    coinsEdit.putString("Coins", String.valueOf(coinCount));
                    coinsEdit.apply();
                    coins = getSharedPreferences("Rewards", MODE_PRIVATE);
                    currentCoins = coins.getString("Coins", "0");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.child("Coins").setValue(currentCoins);

                    Toasty.success(BuyCoinsActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();
                }
                if (purchase.getSku().equals(coins_silver)) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            Toasty.success(BuyCoinsActivity.this, "Request Acknowledged", Toast.LENGTH_LONG).show();

                        }
                    };


                    LayoutInflater inflater = LayoutInflater.from(BuyCoinsActivity.this);
                    View view = inflater.inflate(R.layout.alert_dialog_purchased, null);
                    Button acceptButton = view.findViewById(R.id.btnOkAlertPurchased);
                    final AlertDialog alertDialog = new AlertDialog.Builder(BuyCoinsActivity.this)
                            .setView(view)
                            .show();

                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                        }
                    });
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);
                    int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                    coinCount = coinCount + 14000;
                    SharedPreferences.Editor coinsEdit = coins.edit();
                    coinsEdit.putString("Coins", String.valueOf(coinCount));
                    coinsEdit.apply();
                    coins = getSharedPreferences("Rewards", MODE_PRIVATE);
                    currentCoins = coins.getString("Coins", "0");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.child("Coins").setValue(currentCoins);

                    Toasty.success(BuyCoinsActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();
                }
                if (purchase.getSku().equals(coins_bronze)) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                            Toasty.success(BuyCoinsActivity.this, "Request Acknowledged", Toast.LENGTH_LONG).show();

                        }
                    };


                    LayoutInflater inflater = LayoutInflater.from(BuyCoinsActivity.this);
                    View view = inflater.inflate(R.layout.alert_dialog_purchased, null);
                    Button acceptButton = view.findViewById(R.id.btnOkAlertPurchased);
                    final AlertDialog alertDialog = new AlertDialog.Builder(BuyCoinsActivity.this)
                            .setView(view)
                            .show();

                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                        }
                    });
                    billingClient.consumeAsync(consumeParams, consumeResponseListener);

                    int coinCount = Integer.parseInt(coins.getString("Coins", "0"));
                    coinCount = coinCount + 5000;
                    SharedPreferences.Editor coinsEdit = coins.edit();
                    coinsEdit.putString("Coins", String.valueOf(coinCount));
                    coinsEdit.apply();
                    coins = getSharedPreferences("Rewards", MODE_PRIVATE);
                    currentCoins = coins.getString("Coins", "0");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reference.child("Coins").setValue(currentCoins);
                    Toasty.success(BuyCoinsActivity.this, "Purchase Successful", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toasty.error(BuyCoinsActivity.this, "Transaction Failed", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Toasty.info(BuyCoinsActivity.this, "onPurchases Updated", Toast.LENGTH_LONG).show();

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
        Animatoo.animateSwipeRight(BuyCoinsActivity.this);

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
        Animatoo.animateSwipeLeft(BuyCoinsActivity.this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}