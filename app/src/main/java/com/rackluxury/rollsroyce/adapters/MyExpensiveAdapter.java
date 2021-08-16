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
import com.rackluxury.rollsroyce.activities.ExpensiveDetailActivity;
import com.rackluxury.rollsroyce.activities.FavDBExpensive;

import java.util.ArrayList;
import java.util.List;

public class MyExpensiveAdapter extends RecyclerView.Adapter<MyExpensiveAdapter.ExpensiveViewHolder>{
    private final Context mContext;
    private int lastPosition = -1;
    private FirebaseAuth firebaseAuth;
    private FavDBExpensive favDB;
    private List<ExpensiveData> mExpensiveData;


    public MyExpensiveAdapter(Context mContext, List<ExpensiveData> mExpensiveData) {
        this.mContext = mContext;
        this.mExpensiveData = mExpensiveData;
    }


    @NonNull
    @Override
    public ExpensiveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        favDB = new FavDBExpensive(mContext);
        //create table on first
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("expensiveFavouriteFirst", true);
        if (firstStart) {
            createTableOnFirstStart();
        }
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expensive_recycler_row_item,viewGroup,false);
        return new ExpensiveViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ExpensiveViewHolder expensiveViewHolder, int i) {
        final ExpensiveData expensiveData = mExpensiveData.get(i);
        readCursorData(expensiveData, expensiveViewHolder);
        expensiveViewHolder.imageView.setImageResource(mExpensiveData.get(i).getExpensiveImage());
        expensiveViewHolder.mTitle.setText(mExpensiveData.get(i).getExpensiveName());
        expensiveViewHolder.mDescription.setText(mExpensiveData.get(i).getExpensiveDescription());
        expensiveViewHolder.mPrice.setText(mExpensiveData.get(i).getExpensivePrice());

        expensiveViewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openDetailActivityFromMContext = new Intent(mContext, ExpensiveDetailActivity.class);
                openDetailActivityFromMContext.putExtra("Image",mExpensiveData.get(expensiveViewHolder.getAdapterPosition()).getExpensiveImage());
                openDetailActivityFromMContext.putExtra("Name",mExpensiveData.get(expensiveViewHolder.getAdapterPosition()).getExpensiveName());
                openDetailActivityFromMContext.putExtra("Description",mExpensiveData.get(expensiveViewHolder.getAdapterPosition()).getExpensiveDescription());
                mContext.startActivity(openDetailActivityFromMContext);
                Animatoo.animateSwipeLeft(mContext);


            }
        });

        setAnimation(expensiveViewHolder.itemView,i);
    }
    public void setAnimation(View viewToAnimate,int position){
        if(position>lastPosition){
            ScaleAnimation HomeActivityAnimation = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            HomeActivityAnimation.setDuration(350);
            viewToAnimate.startAnimation(HomeActivityAnimation);
            lastPosition = position;
        }




    }
    @Override
    public int getItemCount() {
        return mExpensiveData.size();
    }
    public void filteredList(ArrayList<ExpensiveData> filterList) {

        mExpensiveData = filterList;
        notifyDataSetChanged();
    }
    private void likeClick (ExpensiveData expensiveData, Button favBtn, final TextView textLike) {

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference refLikeExpensive = FirebaseDatabase.getInstance().getReference().child(firebaseAuth.getUid()).child("likes").child("expensiveLikes");
        final DatabaseReference upvotesRefLikeExpensive = refLikeExpensive.child(expensiveData.getKey_id());

        if (expensiveData.getFavStatus().equals("0")) {

            expensiveData.setFavStatus("1");
            favDB.insertIntoTheDatabase(expensiveData.getExpensiveName(), expensiveData.getExpensiveImage(),
                    expensiveData.getKey_id(), expensiveData.getFavStatus());
            favBtn.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
            favBtn.setSelected(true);

            upvotesRefLikeExpensive.runTransaction(new Transaction.Handler() {
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
        } else if (expensiveData.getFavStatus().equals("1")) {
            expensiveData.setFavStatus("0");
            favDB.remove_fav(expensiveData.getKey_id());
            favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow_24dp);
            favBtn.setSelected(false);

            upvotesRefLikeExpensive.runTransaction(new Transaction.Handler() {
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
        editor.putBoolean("expensiveFavouriteFirst", false);
        editor.apply();
    }

    private void readCursorData(ExpensiveData expensiveData, ExpensiveViewHolder expensiveViewHolder) {
        Cursor cursor = favDB.read_all_data(expensiveData.getKey_id());
        SQLiteDatabase db = favDB.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String item_fav_status = cursor.getString(cursor.getColumnIndex(FavDBExpensive.FAVORITE_STATUS));
                expensiveData.setFavStatus(item_fav_status);

                //check fav status
                if (item_fav_status != null && item_fav_status.equals("1")) {
                    expensiveViewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                } else if (item_fav_status != null && item_fav_status.equals("0")) {
                    expensiveViewHolder.favBtn.setBackgroundResource(R.drawable.ic_favorite_shadow_24dp);
                }
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

    }
public class ExpensiveViewHolder extends RecyclerView.ViewHolder{
    final ImageView imageView;
    final TextView mTitle;
    final TextView mDescription;
    final TextView mPrice;
    final CardView mCardView;
    final SoundPool soundPool;
    final int soundLike;


    TextView likeCountTextView;
    Button favBtn;

    public ExpensiveViewHolder( View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.ivExpensiveRecyclerRowItemImage);
        mTitle  = itemView.findViewById(R.id.tvExpensiveRecyclerRowItemTitle);
        mDescription = itemView.findViewById(R.id.tvExpensiveRecyclerRowItemDescription);
        mPrice = itemView.findViewById(R.id.tvExpensiveRecyclerRowItemPrice);
        mCardView = itemView.findViewById(R.id.cvExpensiveRecyclerRowItemCard);


        favBtn = itemView.findViewById(R.id.btnFavExpensive);
        likeCountTextView = itemView.findViewById(R.id.tvLikeCountExpensive);


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
                ExpensiveData expensiveData = mExpensiveData.get(position);
                likeClick(expensiveData, favBtn, likeCountTextView);

                if(expensiveData.getFavStatus().equals("1")){
                    Snackbar snackbar = Snackbar.make(view, "Added to Favourites.", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(4000);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            soundPool.play(soundLike, 1, 1, 0, 0, 1);
                            int position = getAdapterPosition();
                            ExpensiveData expensiveData = mExpensiveData.get(position);

                            likeClick(expensiveData, favBtn, likeCountTextView);

                        }
                    });
                    snackbar.show();


                }else {
                    Snackbar snackbar = Snackbar.make(view, "Added to Favourites.", Snackbar.LENGTH_LONG);
                    snackbar.setDuration(4000);
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            soundPool.play(soundLike, 1, 1, 0, 0, 1);
                            int position = getAdapterPosition();
                            ExpensiveData expensiveData = mExpensiveData.get(position);

                            likeClick(expensiveData, favBtn, likeCountTextView);

                        }
                    });
                    snackbar.show();


                }


                Snackbar snackbar = Snackbar.make(view, "Added to Favourites.", Snackbar.LENGTH_LONG);
                snackbar.setDuration(10000);
                snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        soundPool.play(soundLike, 1, 1, 0, 0, 1);
                        int position = getAdapterPosition();
                        ExpensiveData expensiveData = mExpensiveData.get(position);

                        likeClick(expensiveData, favBtn, likeCountTextView);

                    }
                });
                snackbar.show();


            }
        });
    }
}
}
