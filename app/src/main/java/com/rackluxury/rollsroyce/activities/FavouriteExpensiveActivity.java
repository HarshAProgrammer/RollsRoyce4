package com.rackluxury.rollsroyce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.adapters.FavAdapterExpensive;

import java.util.ArrayList;
import java.util.List;

public class FavouriteExpensiveActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavDBExpensive favDB;
    private final List<FavItemExpensive> favItemListExpensive = new ArrayList<>();
    private Toolbar toolbar;
    private ImageView backIcon;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_expensive);

        toolbar = findViewById(R.id.toolbarExpensiveFavouritePage);
        backIcon = findViewById(R.id.backIconExpensiveFavourite);
        favDB = new FavDBExpensive(FavouriteExpensiveActivity.this);
        recyclerView = findViewById(R.id.rvExpensiveFavourite);
        lottieAnimationView = findViewById(R.id.lavFavouriteExpensive);

        initToolbar();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavouriteExpensiveActivity.this));
        loadData();


    }
    private void initToolbar() {
        setSupportActionBar(toolbar);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void loadData() {
        if (favItemListExpensive != null) {
            favItemListExpensive.clear();
            lottieAnimationView.setVisibility(View.GONE);
        }
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavDBExpensive.ITEM_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavDBExpensive.KEY_ID));
                int image = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavDBExpensive.ITEM_IMAGE)));
                FavItemExpensive favItem = new FavItemExpensive(title, id, image);
                favItemListExpensive.add(favItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        FavAdapterExpensive favAdapterExpensive = new FavAdapterExpensive(FavouriteExpensiveActivity.this, favItemListExpensive);

        recyclerView.setAdapter(favAdapterExpensive);
        int count = 0;
        count = recyclerView.getAdapter().getItemCount();
        if (count == 0){
            lottieAnimationView.setVisibility(View.VISIBLE);

        }

    }
    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSplit(FavouriteExpensiveActivity.this);
    }

}