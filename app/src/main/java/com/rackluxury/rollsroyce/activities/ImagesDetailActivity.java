package com.rackluxury.rollsroyce.activities;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.rackluxury.rollsroyce.BuildConfig;
import com.rackluxury.rollsroyce.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

import static com.rackluxury.rollsroyce.activities.ImagesActivity.EXTRA_URL;
import static com.rackluxury.rollsroyce.activities.ImagesActivity.EXTRA_VIEWS;
import static com.rackluxury.rollsroyce.activities.ImagesActivity.EXTRA_LIKES;
import static com.rackluxury.rollsroyce.activities.ImagesActivity.EXTRA_COMMENTS;
import static com.rackluxury.rollsroyce.activities.ImagesActivity.EXTRA_DOWNLOADS;

public class ImagesDetailActivity extends AppCompatActivity {

    private PhotoView photoView;
    private ConstraintLayout imageDetailLay;
    private SharedPreferences prefs;
    private FileOutputStream outputStream;
    private Bitmap bitmap;
    private BitmapDrawable drawable;
    private static final int PERMISSION_STORAGE_CODE = 1000;
    private SoundPool soundPool;
    private int soundSaveImage;
    private int soundWallpaper;
    private int soundLike;
    private AnimatedVectorDrawable avd2;
    private AnimatedVectorDrawableCompat avd;
    private ImageView mainGreyHeart;
    private ImageView liker;
    private CardView cardViewLike;
    private ImageView mainRedHeart;
    private ImageView heart;
    private ImageView love;
    private ImageView shocked;
    private ImageView sad;
    private ImageView happy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_detail);


        Toolbar toolbar = findViewById(R.id.toolbarImageDetailActivity);
        imageDetailLay = findViewById(R.id.conLayImageDetail);
        photoView = (PhotoView) findViewById(R.id.image_view_detail);
        TextView textViewViews = findViewById(R.id.tvViewImageDetail);
        TextView textViewLikes = findViewById(R.id.tvLikeImageDetail);
        TextView textViewComments = findViewById(R.id.tvCommentImageDetail);
        TextView textViewDownloads = findViewById(R.id.tvDownloadImageDetail);
        ImageView backIcon = findViewById(R.id.backIconImagesDetail);
        ImageView optionsIcon = findViewById(R.id.optionsIconImagesDetail);
        liker = findViewById(R.id.ivImagesDetailLiker);

        mainGreyHeart = findViewById(R.id.ivImagesDetailGreyHeart);
        cardViewLike = findViewById(R.id.cvImagesDetailLikerOptions);
        mainRedHeart = findViewById(R.id.ivImagesDetailRedHeart);
        heart = findViewById(R.id.ivImgDetailReactHeart);
        happy = findViewById(R.id.ivImgDetailReactHappy);
        love = findViewById(R.id.ivImgDetailReactLove);
        sad = findViewById(R.id.ivImgDetailReactSad);
        shocked = findViewById(R.id.ivImgDetailReactShocked);


        Animation reactBounceAnim = AnimationUtils.loadAnimation(this, R.anim.react_bounce_anim);

        final Drawable mrhDrawable = mainRedHeart.getDrawable();
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                soundPool.play(soundLike, 1, 1, 0, 0, 1);
                mainRedHeart.setAlpha(0.70f);

                if (mrhDrawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) mrhDrawable;
                    avd.start();
                } else if (mrhDrawable instanceof AnimatedVectorDrawable) {
                    avd2 = (AnimatedVectorDrawable) mrhDrawable;
                    avd2.start();

                }
                heart.startAnimation(reactBounceAnim);
            }
        });


        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundLike, 1, 1, 0, 0, 1);
                happy.startAnimation(reactBounceAnim);
            }
        });
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundLike, 1, 1, 0, 0, 1);

                love.startAnimation(reactBounceAnim);
            }
        });
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundLike, 1, 1, 0, 0, 1);
                sad.startAnimation(reactBounceAnim);
            }
        });
        shocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundLike, 1, 1, 0, 0, 1);
                shocked.startAnimation(reactBounceAnim);
            }
        });


        final Drawable drawable = mainGreyHeart.getDrawable();

        liker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainGreyHeart.setAlpha(0.70f);
                soundPool.play(soundLike, 1, 1, 0, 0, 1);

                if(drawable instanceof AnimatedVectorDrawableCompat){
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                }else if(drawable instanceof AnimatedVectorDrawable){
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();

                }
            }
        });

        Animation reactionsOpeningAnimation = AnimationUtils.loadAnimation(this, R.anim.like_reactions_animations);
        liker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                cardViewLike.setVisibility(View.VISIBLE);
                cardViewLike.startAnimation(reactionsOpeningAnimation);

                return false;
            }
        });




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
        soundSaveImage = soundPool.load(this, R.raw.sound_save_image, 1);
        soundWallpaper = soundPool.load(this, R.raw.sound_set_wallpaper, 1);
        soundLike = soundPool.load(this, R.raw.sound_like, 1);




        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Animatoo.animateSwipeRight(ImagesDetailActivity.this);
            }
        });


        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("imagesDetailFirst", true);
        if (firstStart) {
            onFirst();
        }

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(EXTRA_URL);

        int viewCount = intent.getIntExtra(EXTRA_VIEWS, 0);
        int likeCount = intent.getIntExtra(EXTRA_LIKES, 0);
        int commentCount = intent.getIntExtra(EXTRA_COMMENTS, 0);
        int downloadCount = intent.getIntExtra(EXTRA_DOWNLOADS, 0);
        String strViewCount = String.valueOf(viewCount);
        String strLikeCount = String.valueOf(likeCount);
        String strCommentCount = String.valueOf(commentCount);
        String strDownloadCount = String.valueOf(downloadCount);


        Picasso.get().load(imageUrl).fit().centerInside().into(photoView);
        textViewViews.setText(strViewCount);
        textViewLikes.setText(strLikeCount);
        textViewComments.setText(strCommentCount);
        textViewDownloads.setText(strDownloadCount);

        SlidrConfig config = new SlidrConfig.Builder()
                .listener(new SlidrListener() {
                    @Override
                    public void onSlideStateChanged(int state) {

                    }

                    @Override
                    public void onSlideChange(float percent) {

                    }

                    @Override
                    public void onSlideOpened() {

                    }

                    @Override
                    public boolean onSlideClosed() {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("imagesDetailFirst", false);
                        editor.apply();
                        return false;
                    }
                }).build();

        Slidr.attach(this, config);

        registerForContextMenu(optionsIcon);




    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_images_detail) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_STORAGE_CODE);

                } else {
                    downloadImage();
                }

            } else {
                downloadImage();
            }

            return true;
        } else if (item.getItemId() == R.id.share_images_detail) {

            drawable = (BitmapDrawable) photoView.getDrawable();
            bitmap = drawable.getBitmap();

            try {
                File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + "Cars from RollsRoyce.png");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();

                file.setReadable(true, false);
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/png");

                startActivity(Intent.createChooser(intent, "Share image via"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (item.getItemId() == R.id.wallpaper_images_detail) {
            setWallpaper();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Choose your option");
        getMenuInflater().inflate(R.menu.images_detail_menu, menu);
    }

    public void onFirst() {
        Snackbar snackbar = Snackbar.make(imageDetailLay, "Swipe Right to Dismiss", Snackbar.LENGTH_LONG)
                .setDuration(10000)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("imagesDetailFirst", false);
                        editor.apply();
                    }
                })
                .setActionTextColor(Color.WHITE)
                .setTextColor(Color.WHITE);

        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSwipeRight(ImagesDetailActivity.this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            } else {
                Toasty.error(ImagesDetailActivity.this, "Permission denied...!", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void downloadImage() {

        soundPool.play(soundSaveImage, 1, 1, 0, 0, 1);
        drawable = (BitmapDrawable) photoView.getDrawable();
        bitmap = drawable.getBitmap();
        File filePath = Environment.getExternalStorageDirectory();
        File dir = new File(filePath.getAbsolutePath() + "/Cars from RollsRoyce/");
        dir.mkdir();
        File file = new File(dir, System.currentTimeMillis() + ".jpg");
        try {
            outputStream = new FileOutputStream(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            sendBroadcast(intent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        Toasty.success(ImagesDetailActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        drawable = (BitmapDrawable) photoView.getDrawable();
        bitmap = drawable.getBitmap();
        try {
            wallpaperManager.setBitmap(bitmap);
            Toasty.success(ImagesDetailActivity.this, "Wallpaper Set Successfully", Toast.LENGTH_LONG).show();
            soundPool.play(soundWallpaper, 1, 1, 0, 0, 1);


        } catch (IOException e) {
            Toasty.error(ImagesDetailActivity.this, "Wallpaper Not Set", Toast.LENGTH_LONG).show();


        }

    }

}