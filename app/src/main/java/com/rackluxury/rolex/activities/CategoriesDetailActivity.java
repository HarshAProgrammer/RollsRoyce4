package com.rackluxury.rolex.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.FileProvider;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrListener;
import com.rackluxury.rolex.BuildConfig;
import com.rackluxury.rolex.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class CategoriesDetailActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private static final String SHOWCASE_ID = "single categories detail";
    TextView categoriesName;
    TextView categoriesDescription;
    ImageView categoriesImage;
    String shareCategoriesImageDescription;
    private Toolbar toolbar;
    private FileOutputStream outputStream;
    private Bitmap bitmap;
    private BitmapDrawable drawable;
    private MotionLayout categoriesDetailLay;
    private SharedPreferences prefs;
    private SoundPool soundPool;
    private int soundSave;
    private int soundWallpaper;
    private int soundLike;
    private AnimatedVectorDrawable avd2;
    private AnimatedVectorDrawableCompat avd;
    private ImageView liker;
    private ImageView mainGreyHeart;
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
        setContentView(R.layout.activity_categories_detail);
        setUpUIViewsDetailActivity();
        setTransitionDialogue();
        getInformationFromMain();
        setBitmap();
        initToolbar();
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean introStart = prefs.getBoolean("categoriesDetailFirst", true);
        if (introStart) {
            onFirstIntro();
        }




        new MaterialShowcaseView.Builder(this)
                .setTarget(categoriesName)
                .setDismissText("GOT IT")
                .setContentText("Swipe Up to get more Information.")
                .setContentTextColor(getResources().getColor(R.color.colorWhite))
                .setMaskColour(getResources().getColor(R.color.colorGreen))
                .withRectangleShape(true)
                .setDelay(1000)
                .singleUse(SHOWCASE_ID)
                .show();

        

    }

    private void setUpUIViewsDetailActivity() {
        toolbar = findViewById(R.id.toolbarCategoriesDetailActivity);
        categoriesDetailLay = findViewById(R.id.motionLayCategoriesDetail);
        categoriesName = findViewById(R.id.tvCategoriesDetailName);
        categoriesDescription = findViewById(R.id.tvCategoriesDetailDescription);
        categoriesImage = findViewById(R.id.ivCategoriesDetailImage);
        liker = findViewById(R.id.ivCategoriesDetailLiker);
        mainGreyHeart = findViewById(R.id.ivCategoriesDetailGreyHeart);
        cardViewLike = findViewById(R.id.cvCategoriesLikerOptions);
        mainRedHeart = findViewById(R.id.ivCategoriesDetailRedHeart);
        heart = findViewById(R.id.ivCatDetailReactHeart);
        happy = findViewById(R.id.ivCatDetailReactHappy);
        love = findViewById(R.id.ivCatDetailReactLove);
        sad = findViewById(R.id.ivCatDetailReactSad);
        shocked = findViewById(R.id.ivCatDetailReactShocked);





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
        soundLike = soundPool.load(this, R.raw.sound_like, 1);
        soundSave = soundPool.load(this, R.raw.sound_save_image, 1);
        soundWallpaper = soundPool.load(this, R.raw.sound_set_wallpaper, 1);

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
                soundPool.play(soundLike, 1, 1, 0, 0, 1);
                mainGreyHeart.setAlpha(0.70f);
                prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                boolean likeStart = prefs.getBoolean("categoriesDetailFirstLike", true);
                if (likeStart) {
                    onFirstLike();
                }


                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    avd.start();
                } else if (drawable instanceof AnimatedVectorDrawable) {
                    avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();

                }
            }
        });
        Animation reactionsOpeningAnimation = AnimationUtils.loadAnimation(this, R.anim.like_reactions_animations);
        liker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                //Showing card view with reactionsOpeningAnimation
                cardViewLike.setVisibility(View.VISIBLE);
                cardViewLike.startAnimation(reactionsOpeningAnimation);

                return false;
            }
        });


        Typeface detailCategoriesDescriptionFont = Typeface.createFromAsset(CategoriesDetailActivity.this.getAssets(), "fonts/OpenSansCondensed-Light.ttf");
        categoriesDescription.setTypeface(detailCategoriesDescriptionFont);

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
                        editor.putBoolean("categoriesDetailFirst", false);
                        editor.apply();
                        return false;
                    }
                }).build();

        Slidr.attach(this, config);


    }

    public void onFirstIntro() {
        Toasty.info(CategoriesDetailActivity.this, "Swipe Right to Dismiss", Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("categoriesDetailFirst", false);
        editor.apply();
    }
    public void onFirstLike() {
        Toasty.info(CategoriesDetailActivity.this, "Long Hold for other Reactions.", Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("categoriesDetailFirstLike", false);
        editor.apply();
    }


    private void setTransitionDialogue() {
        final TransitionDialogue transitionDialogue = new TransitionDialogue(CategoriesDetailActivity.this);
        transitionDialogue.startTransitionDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 700;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                transitionDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void getInformationFromMain() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            categoriesImage.setImageResource(mBundle.getInt("Image"));
            categoriesName.setText(mBundle.getString("Name"));
            categoriesDescription.setText(mBundle.getString("Description"));
            shareCategoriesImageDescription = categoriesDescription.getText().toString();

        }
    }


    private void setBitmap() {
        drawable = (BitmapDrawable) categoriesImage.getDrawable();
        bitmap = drawable.getBitmap();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoriesName.getText().toString());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categories_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_image_categories) {

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
        } else if (item.getItemId() == R.id.share_image_categories) {

            drawable = (BitmapDrawable) categoriesImage.getDrawable();
            bitmap = drawable.getBitmap();

            try {
                File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + "Watches from Rolex.png");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                String shareImageSub = categoriesName.getText().toString();
                intent.putExtra(Intent.EXTRA_SUBJECT, shareImageSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareCategoriesImageDescription);
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
        } else if (item.getItemId() == R.id.wallpaper_image_categories) {
            setWallpaper();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            } else {
                Toasty.error(CategoriesDetailActivity.this, "Permission denied...!", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void downloadImage() {
        soundPool.play(soundSave, 1, 1, 0, 0, 1);
        drawable = (BitmapDrawable) categoriesImage.getDrawable();
        bitmap = drawable.getBitmap();
        File filePath = Environment.getExternalStorageDirectory();
        File dir = new File(filePath.getAbsolutePath() + "/Watches From Rolex/");
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
        Toasty.success(CategoriesDetailActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();

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
        try {
            wallpaperManager.setBitmap(bitmap);
            soundPool.play(soundWallpaper, 1, 1, 0, 0, 1);
            Toasty.success(CategoriesDetailActivity.this, "Wallpaper Set Successfully", Toast.LENGTH_LONG).show();


        } catch (IOException e) {
            Toasty.error(CategoriesDetailActivity.this, "Wallpaper Not Set", Toast.LENGTH_LONG).show();


        }

    }

    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSwipeRight(CategoriesDetailActivity.this);
    }

}