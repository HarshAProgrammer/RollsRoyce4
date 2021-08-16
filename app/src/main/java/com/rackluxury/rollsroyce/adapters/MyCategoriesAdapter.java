package com.rackluxury.rollsroyce.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.activities.CategoriesDetailActivity;
import com.rackluxury.rollsroyce.activities.FavDBCategories;

import java.util.ArrayList;
import java.util.List;

public class MyCategoriesAdapter extends RecyclerView.Adapter<MyCategoriesAdapter.CategoriesViewHolder> {
    private final Context mContext;
    private int lastPosition = -1;
    private FirebaseAuth firebaseAuth;
    private FavDBCategories favDB;
    private List<CategoriesData> mCategoriesData;

    public MyCategoriesAdapter(Context mContext, List<CategoriesData> mCategoriesData) {
        this.mContext = mContext;
        this.mCategoriesData = mCategoriesData;
    }


    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        favDB = new FavDBCategories(mContext);
        //create table on first
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("categoriesFavouriteFirst", true);
        if (firstStart) {
            createTableOnFirstStart();
        }
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.categories_recycler_row_item, viewGroup, false);
        return new CategoriesViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriesViewHolder categoriesViewHolder, int i) {
        final CategoriesData categoriesData = mCategoriesData.get(i);
        readCursorData(categoriesData, categoriesViewHolder);
        categoriesViewHolder.imageView.setImageResource(mCategoriesData.get(i).getCategoriesImage());
        categoriesViewHolder.mTitle.setText(mCategoriesData.get(i).getCategoriesName());
        categoriesViewHolder.mDescription.setText(mCategoriesData.get(i).getCategoriesDescription());
        categoriesViewHolder.mPrice.setText(mCategoriesData.get(i).getCategoriesPrice());

        categoriesViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCategoriesDetailFromMContext = new Intent(mContext, CategoriesDetailActivity.class);
                openCategoriesDetailFromMContext.putExtra("Image", mCategoriesData.get(categoriesViewHolder.getAdapterPosition()).getCategoriesImage());
                openCategoriesDetailFromMContext.putExtra("Name", mCategoriesData.get(categoriesViewHolder.getAdapterPosition()).getCategoriesName());
                openCategoriesDetailFromMContext.putExtra("Description", mCategoriesData.get(categoriesViewHolder.getAdapterPosition()).getCategoriesDescription());
                mContext.startActivity(openCategoriesDetailFromMContext);
                Animatoo.animateSwipeLeft(mContext);


            }
        });

        setAnimation(categoriesViewHolder.itemView, i);
    }

    public void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation HomeActivityAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            HomeActivityAnimation.setDuration(350);
            viewToAnimate.startAnimation(HomeActivityAnimation);
            lastPosition = position;
        }


    }

    @Override
    public int getItemCount() {
        return mCategoriesData.size();
    }

    public void filteredList(ArrayList<CategoriesData> filterList) {

        mCategoriesData = filterList;
        notifyDataSetChanged();
    }


    private void likeClick(CategoriesData categoriesData, Button favBtn, final TextView textLike) {

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference refLikeCategories = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getUid()).child("likes").child("categoriesLikes");
        final DatabaseReference upvotesRefLikeCategories = refLikeCategories.child(categoriesData.getKey_id());

        if (categoriesData.getFavStatus().equals("0")) {

            categoriesData.setFavStatus("1");

            favDB.insertIntoTheDatabase(categoriesData.getCategoriesName(), categoriesData.getCategoriesImage(),
                    categoriesData.getKey_id(), categoriesData.getFavStatus());
            favBtn.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            favBtn.setSelected(true);

            upvotesRefLikeCategories.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull final MutableData mutableData) {
                    try {
                        Integer currentValue = mutableData.getValue(Integer.class);
                        if (currentValue == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(currentValue + 1);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    textLike.setText(mutableData.getValue().toString());
                                }
                            });
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    System.out.println("Transaction completed");
                }
            });
        } else if (categoriesData.getFavStatus().equals("1")) {
            categoriesData.setFavStatus("0");
            favDB.remove_fav(categoriesData.getKey_id());
            favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow_24dp);
            favBtn.setSelected(false);

            upvotesRefLikeCategories.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull final MutableData mutableData) {
                    try {
                        Integer currentValue = mutableData.getValue(Integer.class);
                        if (currentValue == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(currentValue - 1);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    textLike.setText(mutableData.getValue().toString());
                                }
                            });
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                    System.out.println("Transaction completed");
                }
            });
        }

    }

    private void createTableOnFirstStart() {
        favDB.insertEmpty();

        SharedPreferences prefs = mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("categoriesFavouriteFirst", false);
        editor.apply();
    }

    private void readCursorData(CategoriesData categoriesData, CategoriesViewHolder categoriesViewHolder) {
        Cursor cursor = favDB.read_all_data(categoriesData.getKey_id());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavDBCategories.FAVORITE_STATUS));
                categoriesData.setFavStatus(item_fav_status);

                //check fav status
                if (item_fav_status != null && item_fav_status.equals("1")) {
                    categoriesViewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                } else if (item_fav_status != null && item_fav_status.equals("0")) {
                    categoriesViewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow_24dp);
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView mTitle;
        final TextView mDescription;
        final TextView mPrice;
        final CardView mCardView;
        final SoundPool soundPool;
        final int soundLike;
        TextView likeCountTextView;
        Button favBtn;

        public CategoriesViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivCategoriesRecyclerRowItemImage);
            mTitle = itemView.findViewById(R.id.tvCategoriesRecyclerRowItemTitle);
            mDescription = itemView.findViewById(R.id.tvCategoriesRecyclerRowItemDescription);
            mPrice = itemView.findViewById(R.id.tvCategoriesRecyclerRowItemPrice);
            mCardView = itemView.findViewById(R.id.cvCategoriesRecyclerRowItemCard);
            favBtn = itemView.findViewById(R.id.btnFavCategories);
            likeCountTextView = itemView.findViewById(R.id.tvLikeCountCategories);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                soundPool = new SoundPool.Builder()
                        .setMaxStreams(1)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else {
                soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            }
            soundLike = soundPool.load(mContext, R.raw.sound_like, 1);


            //add to fav btn
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    soundPool.play(soundLike, 1, 1, 0, 0, 1);


                    int position = getAdapterPosition();
                    CategoriesData categoriesData = mCategoriesData.get(position);
                    likeClick(categoriesData, favBtn, likeCountTextView);



                    if(categoriesData.getFavStatus().equals("1")){

                        Snackbar snackbar = Snackbar.make(view, "Added to Favourites.", Snackbar.LENGTH_LONG);
                        snackbar.setDuration(4000);
                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                        snackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                soundPool.play(soundLike, 1, 1, 0, 0, 1);

                                int position = getAdapterPosition();
                                CategoriesData categoriesData = mCategoriesData.get(position);
                                likeClick(categoriesData, favBtn, likeCountTextView);

                            }

                        });
                        snackbar.show();

                    }else {
                        Snackbar snackbar = Snackbar.make(view, "Removed from Favourites.", Snackbar.LENGTH_LONG);
                        snackbar.setDuration(4000);
                        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                        snackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                soundPool.play(soundLike, 1, 1, 0, 0, 1);

                                int position = getAdapterPosition();
                                CategoriesData categoriesData = mCategoriesData.get(position);
                                likeClick(categoriesData, favBtn, likeCountTextView);

                            }

                        });
                        snackbar.show();

                    }


                }
            });
        }
        }
    }
