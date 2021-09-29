package com.rackluxury.rollsroyce.facts;

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
import android.text.method.ScrollingMovementMethod;

import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.rackluxury.rollsroyce.BuildConfig;
import com.rackluxury.rollsroyce.R;
import com.ramotion.foldingcell.FoldingCell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import tyrantgit.explosionfield.ExplosionField;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class FactsDetailActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener {
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private static final String SHOWCASE_ID = "single facts detail";
    private static final int PERMISSION_STORAGE_CODE = 1000;
    String shareFactsDescription;
    private Toolbar toolbar;
    private ExplosionField explosionField;
    private GestureDetector gestureDetector;
    private PhotoView FactImage;
    private SharedPreferences prefs;
    private ConstraintLayout factsDetailLay;
    private Bitmap bitmap;
    private BitmapDrawable drawable;
    private SoundPool soundPool;
    private TextView FactTitle1;
    private int soundSaveImage;
    private int soundWallpaper;
    private int soundLike;
    private FileOutputStream outputStream;
    private ShimmerFrameLayout shimmerFrameLayout;
    private AnimatedVectorDrawable avd2;
    private AnimatedVectorDrawableCompat avd;
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
        setContentView(R.layout.activity_facts_detail);

        factsDetailLay = findViewById(R.id.conLayFactsDetail);
        toolbar = findViewById(R.id.toolbarFactDetailActivity);
        explosionField = ExplosionField.attach2Window(FactsDetailActivity.this);
        gestureDetector = new GestureDetector(FactsDetailActivity.this, this);
        TextView factDescription = findViewById(R.id.tvFactsDescription);
        FactTitle1 = findViewById(R.id.tvFactsTitle1);
        TextView FactTitle2 = findViewById(R.id.tvFactsTitle2);
        FactImage = findViewById(R.id.ivDetailFacts);
        shimmerFrameLayout = findViewById(R.id.ivShimDetailFacts);

        ImageView liker = findViewById(R.id.ivFactsDetailLiker);
        mainGreyHeart = findViewById(R.id.ivFactsDetailGreyHeart);
        cardViewLike = findViewById(R.id.cvFactsDetailLikerOptions);
        mainRedHeart = findViewById(R.id.ivFactsDetailRedHeart);
        heart = findViewById(R.id.ivFacDetailReactHeart);
        happy = findViewById(R.id.ivFacDetailReactHappy);
        love = findViewById(R.id.ivFacDetailReactLove);
        sad = findViewById(R.id.ivFacDetailReactSad);
        shocked = findViewById(R.id.ivFacDetailReactShocked);

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

                cardViewLike.setVisibility(View.VISIBLE);
                cardViewLike.startAnimation(reactionsOpeningAnimation);

                return false;
            }
        });

        shimmerFrameLayout.startShimmer();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setShimmer(null);
            }
        }, 1500);

        final FoldingCell fcFacts = findViewById(R.id.folding_cell_facts);


        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("factsDetailFirst", true);
        if (firstStart) {
            onFirst();
        }


        factDescription.setText(getIntent().getStringExtra("description"));
        FactTitle1.setText(getIntent().getStringExtra("title"));
        FactTitle2.setText(getIntent().getStringExtra("title"));
        FactImage.setImageResource(getIntent().getIntExtra("image", 1));
        shareFactsDescription = factDescription.getText().toString();
        factDescription.setMovementMethod(new ScrollingMovementMethod());

        new MaterialShowcaseView.Builder(this)
                .setTarget(fcFacts)
                .setDismissText("GOT IT")
                .setContentText("Tap to get More Information")
                .setContentTextColor(getResources().getColor(R.color.colorWhite))
                .setMaskColour(getResources().getColor(R.color.colorGreen))
                .setDelay(1000)
                .singleUse(SHOWCASE_ID)
                .withRectangleShape(true)
                .show();

        initToolbar();
        fcFacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explosionField.explode(FactImage);
                fcFacts.toggle(false);

            }
        });

    }

    public void onFirst() {

        Snackbar snackbar = Snackbar.make(factsDetailLay, "Swipe Down to Dismiss", Snackbar.LENGTH_LONG)
                .setDuration(10000)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("factsDetailFirst", false);
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
        Animatoo.animateSlideDown(FactsDetailActivity.this);

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Facts About us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.facts_detail_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_image_facts) {

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
        } else if (item.getItemId() == R.id.share_image_facts) {

            drawable = (BitmapDrawable) FactImage.getDrawable();
            bitmap = drawable.getBitmap();

            try {
                File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + "Cars from Rolls Royce.png");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                String shareImageSub = FactTitle1.getText().toString();
                intent.putExtra(Intent.EXTRA_SUBJECT, shareImageSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareFactsDescription);
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
        } else if (item.getItemId() == R.id.wallpaper_image_facts) {
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
                Toasty.error(FactsDetailActivity.this, "Permission denied...!", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void downloadImage() {
        soundPool.play(soundSaveImage, 1, 1, 0, 0, 1);
        drawable = (BitmapDrawable) FactImage.getDrawable();
        bitmap = drawable.getBitmap();
        File filePath = Environment.getExternalStorageDirectory();
        File dir = new File(filePath.getAbsolutePath() + "/Cars from Rolls Royce/");
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
        Toasty.success(FactsDetailActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();

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
            Toasty.success(FactsDetailActivity.this, "Wallpaper Set Successfully", Toast.LENGTH_LONG).show();
            soundPool.play(soundWallpaper, 1, 1, 0, 0, 1);

        } catch (IOException e) {
            Toasty.error(FactsDetailActivity.this, "Wallpaper Not Set", Toast.LENGTH_LONG).show();


        }

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

                result = true;
            }
        } else {

            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                }
                result = true;
            }
        }

        return result;
    }


    private void onSwipeBottom() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("factsDetailFirst", false);
        editor.apply();
        finish();
        Animatoo.animateSlideDown(FactsDetailActivity.this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}