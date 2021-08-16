package com.rackluxury.rolex.reddit.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.rackluxury.rolex.BuildConfig;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.Infinity;
import com.rackluxury.rolex.reddit.SetAsWallpaperCallback;
import com.rackluxury.rolex.reddit.WallpaperSetter;
import com.rackluxury.rolex.reddit.asynctasks.SaveBitmapImageToFile;
import com.rackluxury.rolex.reddit.asynctasks.SaveGIFToFile;
import com.rackluxury.rolex.reddit.bottomsheetfragments.SetAsWallpaperBottomSheetFragment;
import com.rackluxury.rolex.reddit.font.ContentFontFamily;
import com.rackluxury.rolex.reddit.font.ContentFontStyle;
import com.rackluxury.rolex.reddit.font.FontFamily;
import com.rackluxury.rolex.reddit.font.FontStyle;
import com.rackluxury.rolex.reddit.font.TitleFontFamily;
import com.rackluxury.rolex.reddit.font.TitleFontStyle;
import com.rackluxury.rolex.reddit.services.RedditDownloadMediaService;
import com.rackluxury.rolex.reddit.utils.SharedPreferencesUtils;

import java.io.File;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewImageOrGifActivity extends AppCompatActivity implements SetAsWallpaperCallback {

    public static final String EXTRA_IMAGE_URL_KEY = "EIUK";
    public static final String EXTRA_GIF_URL_KEY = "EGUK";
    public static final String EXTRA_FILE_NAME_KEY = "EFNK";
    public static final String EXTRA_SUBREDDIT_OR_USERNAME_KEY = "ESOUK";
    public static final String EXTRA_POST_TITLE_KEY = "EPTK";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    @BindView(R.id.progress_bar_view_image_or_gif_activity)
    ProgressBar mProgressBar;
    @BindView(R.id.image_view_view_image_or_gif_activity)
    BigImageView mImageView;
    @BindView(R.id.load_image_error_linear_layout_view_image_or_gif_activity)
    LinearLayout mLoadErrorLinearLayout;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    Executor mExecutor;
    private boolean isActionBarHidden = false;
    private boolean isDownloading = false;
    private RequestManager glide;
    private String mImageUrl;
    private String mImageFileName;
    private String mSubredditName;
    private boolean isGif = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Infinity) getApplication()).getAppComponent().inject(this);

        getTheme().applyStyle(R.style.Theme_Normal, true);

        getTheme().applyStyle(FontStyle.valueOf(mSharedPreferences
                .getString(SharedPreferencesUtils.FONT_SIZE_KEY, FontStyle.Normal.name())).getResId(), true);

        getTheme().applyStyle(TitleFontStyle.valueOf(mSharedPreferences
                .getString(SharedPreferencesUtils.TITLE_FONT_SIZE_KEY, TitleFontStyle.Normal.name())).getResId(), true);

        getTheme().applyStyle(ContentFontStyle.valueOf(mSharedPreferences
                .getString(SharedPreferencesUtils.CONTENT_FONT_SIZE_KEY, ContentFontStyle.Normal.name())).getResId(), true);

        getTheme().applyStyle(FontFamily.valueOf(mSharedPreferences
                .getString(SharedPreferencesUtils.FONT_FAMILY_KEY, FontFamily.Default.name())).getResId(), true);

        getTheme().applyStyle(TitleFontFamily.valueOf(mSharedPreferences
                .getString(SharedPreferencesUtils.TITLE_FONT_FAMILY_KEY, TitleFontFamily.Default.name())).getResId(), true);

        getTheme().applyStyle(ContentFontFamily.valueOf(mSharedPreferences
                .getString(SharedPreferencesUtils.CONTENT_FONT_FAMILY_KEY, ContentFontFamily.Default.name())).getResId(), true);

        BigImageViewer.initialize(GlideImageLoader.with(this));

        setContentView(R.layout.activity_view_image_or_gif);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setHomeAsUpIndicator(upArrow);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparentActionBarAndExoPlayerControllerColor)));

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_VERTICALLY_TO_GO_BACK_FROM_MEDIA, true)) {
            Slidr.attach(this, new SlidrConfig.Builder().position(SlidrPosition.VERTICAL).distanceThreshold(0.125f).build());
        }

        glide = Glide.with(this);

        Intent intent = getIntent();
        mImageUrl = intent.getStringExtra(EXTRA_GIF_URL_KEY);
        if (mImageUrl == null) {
            isGif = false;
            mImageUrl = intent.getStringExtra(EXTRA_IMAGE_URL_KEY);
        }
        mImageFileName = intent.getStringExtra(EXTRA_FILE_NAME_KEY);
        String postTitle = intent.getStringExtra(EXTRA_POST_TITLE_KEY);
        mSubredditName = intent.getStringExtra(EXTRA_SUBREDDIT_OR_USERNAME_KEY);

        if (postTitle != null) {
            setTitle(Html.fromHtml(String.format("<small>%s</small>", postTitle)));
        } else {
            setTitle("");
        }

        mLoadErrorLinearLayout.setOnClickListener(view -> {
            mProgressBar.setVisibility(View.VISIBLE);
            mLoadErrorLinearLayout.setVisibility(View.GONE);
            loadImage();
        });

        mImageView.setOnClickListener(view -> {
            if (isActionBarHidden) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                isActionBarHidden = false;
            } else {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
                isActionBarHidden = true;
            }
        });

        mImageView.setImageViewFactory(new GlideImageViewFactory());

        mImageView.setImageLoaderCallback(new ImageLoader.Callback() {
            @Override
            public void onCacheHit(int imageType, File image) {

            }

            @Override
            public void onCacheMiss(int imageType, File image) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onSuccess(File image) {
                mProgressBar.setVisibility(View.GONE);

                final SubsamplingScaleImageView view = mImageView.getSSIV();

                if (view != null) {
                    view.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
                        @Override
                        public void onReady() {

                        }

                        @Override
                        public void onImageLoaded() {
                            view.setMinimumDpi(80);
                            view.setDoubleTapZoomDpi(240);
                            view.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_FIXED);
                            view.setQuickScaleEnabled(true);
                            view.resetScaleAndCenter();
                        }

                        @Override
                        public void onPreviewLoadError(Exception e) {

                        }

                        @Override
                        public void onImageLoadError(Exception e) {

                        }

                        @Override
                        public void onTileLoadError(Exception e) {

                        }

                        @Override
                        public void onPreviewReleased() {

                        }
                    });
                }
            }

            @Override
            public void onFail(Exception error) {
                mProgressBar.setVisibility(View.GONE);
                mLoadErrorLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        loadImage();
    }

    private void loadImage() {
        mImageView.showImage(Uri.parse(mImageUrl));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_image_or_gif_activity, menu);
        if (!isGif)
            menu.findItem(R.id.action_set_wallpaper_view_image_or_gif_activity).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_download_view_image_or_gif_activity) {
            if (isDownloading) {
                return false;
            }

            isDownloading = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    // Permission has already been granted
                    download();
                }
            } else {
                download();
            }

            return true;
        } else if (itemId == R.id.action_share_view_image_or_gif_activity) {
            if (isGif)
                shareGif();
            else
                shareImage();
            return true;
        } else if (itemId == R.id.action_set_wallpaper_view_image_or_gif_activity) {
            if (!isGif) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    SetAsWallpaperBottomSheetFragment setAsWallpaperBottomSheetFragment = new SetAsWallpaperBottomSheetFragment();
                    setAsWallpaperBottomSheetFragment.show(getSupportFragmentManager(), setAsWallpaperBottomSheetFragment.getTag());
                } else {
                    WallpaperSetter.set(mExecutor, new Handler(), mImageUrl, WallpaperSetter.BOTH_SCREENS, this,
                            new WallpaperSetter.SetWallpaperListener() {
                                @Override
                                public void success() {
                                    Toast.makeText(ViewImageOrGifActivity.this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void failed() {
                                    Toast.makeText(ViewImageOrGifActivity.this, R.string.error_set_wallpaper, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            return true;
        }

        return false;
    }

    private void download() {
        isDownloading = false;

        Intent intent = new Intent(this, RedditDownloadMediaService.class);
        intent.putExtra(RedditDownloadMediaService.EXTRA_URL, mImageUrl);
        intent.putExtra(RedditDownloadMediaService.EXTRA_MEDIA_TYPE, isGif ? RedditDownloadMediaService.EXTRA_MEDIA_TYPE_GIF : RedditDownloadMediaService.EXTRA_MEDIA_TYPE_IMAGE);
        intent.putExtra(RedditDownloadMediaService.EXTRA_FILE_NAME, mImageFileName);
        intent.putExtra(RedditDownloadMediaService.EXTRA_SUBREDDIT_NAME, mSubredditName);
        ContextCompat.startForegroundService(this, intent);
        Toast.makeText(this, R.string.download_started, Toast.LENGTH_SHORT).show();
    }

    private void shareImage() {
        glide.asBitmap().load(mImageUrl).into(new CustomTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (getExternalCacheDir() != null) {
                    Toast.makeText(ViewImageOrGifActivity.this, R.string.save_image_first, Toast.LENGTH_SHORT).show();
                    SaveBitmapImageToFile.SaveBitmapImageToFile(mExecutor, new Handler(), resource,
                            getExternalCacheDir().getPath(), mImageFileName,
                            new SaveBitmapImageToFile.SaveBitmapImageToFileListener() {
                                @Override
                                public void saveSuccess(File imageFile) {
                                    Uri uri = FileProvider.getUriForFile(ViewImageOrGifActivity.this,
                                            BuildConfig.APPLICATION_ID + ".provider", imageFile);
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareIntent.setType("image/*");
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                                }

                                @Override
                                public void saveFailed() {
                                    Toast.makeText(ViewImageOrGifActivity.this,
                                            R.string.cannot_save_image, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ViewImageOrGifActivity.this,
                            R.string.cannot_get_storage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    private void shareGif() {
        Toast.makeText(ViewImageOrGifActivity.this, R.string.save_gif_first, Toast.LENGTH_SHORT).show();
        glide.asGif().load(mImageUrl).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                if (getExternalCacheDir() != null) {
                    SaveGIFToFile.saveGifToFile(mExecutor, new Handler(), resource, getExternalCacheDir().getPath(), mImageFileName,
                            new SaveGIFToFile.SaveGIFToFileAsyncTaskListener() {
                                @Override
                                public void saveSuccess(File imageFile) {
                                    Uri uri = FileProvider.getUriForFile(ViewImageOrGifActivity.this,
                                            BuildConfig.APPLICATION_ID + ".provider", imageFile);
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareIntent.setType("image/*");
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                                }

                                @Override
                                public void saveFailed() {
                                    Toast.makeText(ViewImageOrGifActivity.this,
                                            R.string.cannot_save_gif, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ViewImageOrGifActivity.this,
                            R.string.cannot_get_storage, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }).submit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.no_storage_permission, Toast.LENGTH_SHORT).show();
                isDownloading = false;
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && isDownloading) {
                download();
            }
        }
    }

    @Override
    public void setToHomeScreen(int viewPagerPosition) {
        WallpaperSetter.set(mExecutor, new Handler(), mImageUrl, WallpaperSetter.HOME_SCREEN, this,
                new WallpaperSetter.SetWallpaperListener() {
                    @Override
                    public void success() {
                        Toast.makeText(ViewImageOrGifActivity.this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed() {
                        Toast.makeText(ViewImageOrGifActivity.this, R.string.error_set_wallpaper, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void setToLockScreen(int viewPagerPosition) {
        WallpaperSetter.set(mExecutor, new Handler(), mImageUrl, WallpaperSetter.LOCK_SCREEN, this,
                new WallpaperSetter.SetWallpaperListener() {
                    @Override
                    public void success() {
                        Toast.makeText(ViewImageOrGifActivity.this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed() {
                        Toast.makeText(ViewImageOrGifActivity.this, R.string.error_set_wallpaper, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void setToBoth(int viewPagerPosition) {
        WallpaperSetter.set(mExecutor, new Handler(), mImageUrl, WallpaperSetter.BOTH_SCREENS, this,
                new WallpaperSetter.SetWallpaperListener() {
                    @Override
                    public void success() {
                        Toast.makeText(ViewImageOrGifActivity.this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed() {
                        Toast.makeText(ViewImageOrGifActivity.this, R.string.error_set_wallpaper, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BigImageViewer.imageLoader().cancelAll();
    }
}
