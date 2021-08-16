package com.rackluxury.rollsroyce.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.adapters.FavAdapterCategories;

import java.util.ArrayList;
import java.util.List;

public class FavouriteCategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavDBCategories favDB;
    private List<FavItemCategories> favItemListCategories = new ArrayList<>();
    private Toolbar toolbar;
    private ImageView backIcon;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_categories);

        toolbar = findViewById(R.id.toolbarCategoriesFavouritePage);
        backIcon = findViewById(R.id.backIconCategoriesFavourite);
        favDB = new FavDBCategories(FavouriteCategoriesActivity.this);
        recyclerView = findViewById(R.id.rvCategoriesFavourite);
        lottieAnimationView = findViewById(R.id.lavFavouriteCategories);

        initToolbar();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavouriteCategoriesActivity.this));
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
        if (favItemListCategories != null) {
            favItemListCategories.clear();
            lottieAnimationView.setVisibility(View.GONE);
        }
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavDBCategories.ITEM_TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavDBCategories.KEY_ID));
                int image = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavDBCategories.ITEM_IMAGE)));
                FavItemCategories favItem = new FavItemCategories(title, id, image);
                favItemListCategories.add(favItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        FavAdapterCategories favAdapterCategories = new FavAdapterCategories(FavouriteCategoriesActivity.this, favItemListCategories);

        recyclerView.setAdapter(favAdapterCategories);
        int count = 0;
        count = recyclerView.getAdapter().getItemCount();
        if (count == 0){
            lottieAnimationView.setVisibility(View.VISIBLE);

        }

    }
    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSplit(FavouriteCategoriesActivity.this);
    }

}