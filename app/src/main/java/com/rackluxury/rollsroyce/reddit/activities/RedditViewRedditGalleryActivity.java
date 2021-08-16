package com.rackluxury.rollsroyce.reddit.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.thefuntasty.hauler.DragDirection;
import com.thefuntasty.hauler.HaulerView;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.reddit.font.ContentFontFamily;
import com.rackluxury.rollsroyce.reddit.font.ContentFontStyle;
import com.rackluxury.rollsroyce.reddit.font.FontFamily;
import com.rackluxury.rollsroyce.reddit.font.FontStyle;
import com.rackluxury.rollsroyce.reddit.font.TitleFontFamily;
import com.rackluxury.rollsroyce.reddit.font.TitleFontStyle;
import com.rackluxury.rollsroyce.reddit.fragments.ViewRedditGalleryImageOrGifFragment;
import com.rackluxury.rollsroyce.reddit.fragments.ViewRedditGalleryVideoFragment;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.reddit.post.Post;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.SetAsWallpaperCallback;
import com.rackluxury.rollsroyce.reddit.utils.SharedPreferencesUtils;
import com.rackluxury.rollsroyce.reddit.WallpaperSetter;

public class RedditViewRedditGalleryActivity extends AppCompatActivity implements SetAsWallpaperCallback {

    public static final String EXTRA_REDDIT_GALLERY = "ERG";
    public static final String EXTRA_SUBREDDIT_NAME = "ESN";

    @BindView(R.id.hauler_view_view_reddit_gallery_activity)
    HaulerView haulerView;
    @BindView(R.id.view_pager_view_reddit_gallery_activity)
    ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ArrayList<Post.Gallery> gallery;
    private String subredditName;
    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;
    @Inject
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Infinity) getApplication()).getAppComponent().inject(this);

        getTheme().applyStyle(R.style.Theme_Normal, true);

        getTheme().applyStyle(FontStyle.valueOf(sharedPreferences
                .getString(SharedPreferencesUtils.FONT_SIZE_KEY, FontStyle.Normal.name())).getResId(), true);

        getTheme().applyStyle(TitleFontStyle.valueOf(sharedPreferences
                .getString(SharedPreferencesUtils.TITLE_FONT_SIZE_KEY, TitleFontStyle.Normal.name())).getResId(), true);

        getTheme().applyStyle(ContentFontStyle.valueOf(sharedPreferences
                .getString(SharedPreferencesUtils.CONTENT_FONT_SIZE_KEY, ContentFontStyle.Normal.name())).getResId(), true);

        getTheme().applyStyle(FontFamily.valueOf(sharedPreferences
                .getString(SharedPreferencesUtils.FONT_FAMILY_KEY, FontFamily.Default.name())).getResId(), true);

        getTheme().applyStyle(TitleFontFamily.valueOf(sharedPreferences
                .getString(SharedPreferencesUtils.TITLE_FONT_FAMILY_KEY, TitleFontFamily.Default.name())).getResId(), true);

        getTheme().applyStyle(ContentFontFamily.valueOf(sharedPreferences
                .getString(SharedPreferencesUtils.CONTENT_FONT_FAMILY_KEY, ContentFontFamily.Default.name())).getResId(), true);

        setContentView(R.layout.activity_view_reddit_gallery);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setHomeAsUpIndicator(upArrow);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparentActionBarAndExoPlayerControllerColor)));

        setTitle(" ");

        gallery = getIntent().getParcelableArrayListExtra(EXTRA_REDDIT_GALLERY);
        if (gallery == null || gallery.isEmpty()) {
            finish();
            return;
        }
        subredditName = getIntent().getStringExtra(EXTRA_SUBREDDIT_NAME);

        if (sharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_VERTICALLY_TO_GO_BACK_FROM_MEDIA, true)) {
            haulerView.setOnDragDismissedListener(dragDirection -> {
                int slide = dragDirection == DragDirection.UP ? R.anim.slide_out_up : R.anim.slide_out_down;
                finish();
                overridePendingTransition(0, slide);
            });
        } else {
            haulerView.setDragEnabled(false);
        }

        setupViewPager();
    }

    private void setupViewPager() {
        setToolbarTitle(0);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setToolbarTitle(position);
            }
        });
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
    }

    private void setToolbarTitle(int position) {
        if (gallery != null && position >= 0 && position < gallery.size()) {
            if (gallery.get(position).mediaType == Post.Gallery.TYPE_IMAGE) {
                setTitle(getString(R.string.view_reddit_gallery_activity_image_label, position + 1, gallery.size()));
            } else if (gallery.get(position).mediaType == Post.Gallery.TYPE_GIF) {
                setTitle(getString(R.string.view_reddit_gallery_activity_gif_label, position + 1, gallery.size()));
            } else {
                setTitle(getString(R.string.view_reddit_gallery_activity_video_label, position + 1, gallery.size()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }

    @Override
    public void setToHomeScreen(int viewPagerPosition) {
        if (gallery != null && viewPagerPosition >= 0 && viewPagerPosition < gallery.size()) {
            WallpaperSetter.set(executor, new Handler(), gallery.get(viewPagerPosition).url, WallpaperSetter.HOME_SCREEN, this,
                    new WallpaperSetter.SetWallpaperListener() {
                        @Override
                        public void success() {
                            Toast.makeText(RedditViewRedditGalleryActivity.this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failed() {
                            Toast.makeText(RedditViewRedditGalleryActivity.this, R.string.error_set_wallpaper, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void setToLockScreen(int viewPagerPosition) {
        if (gallery != null && viewPagerPosition >= 0 && viewPagerPosition < gallery.size()) {
            WallpaperSetter.set(executor, new Handler(), gallery.get(viewPagerPosition).url, WallpaperSetter.LOCK_SCREEN, this,
                    new WallpaperSetter.SetWallpaperListener() {
                        @Override
                        public void success() {
                            Toast.makeText(RedditViewRedditGalleryActivity.this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failed() {
                            Toast.makeText(RedditViewRedditGalleryActivity.this, R.string.error_set_wallpaper, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void setToBoth(int viewPagerPosition) {
        if (gallery != null && viewPagerPosition >= 0 && viewPagerPosition < gallery.size()) {
            WallpaperSetter.set(executor, new Handler(), gallery.get(viewPagerPosition).url, WallpaperSetter.BOTH_SCREENS, this,
                    new WallpaperSetter.SetWallpaperListener() {
                        @Override
                        public void success() {
                            Toast.makeText(RedditViewRedditGalleryActivity.this, R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failed() {
                            Toast.makeText(RedditViewRedditGalleryActivity.this, R.string.error_set_wallpaper, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public int getCurrentPagePosition() {
        return viewPager.getCurrentItem();
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Post.Gallery media = gallery.get(position);
            if (media.mediaType == Post.Gallery.TYPE_VIDEO) {
                ViewRedditGalleryVideoFragment fragment = new ViewRedditGalleryVideoFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(ViewRedditGalleryVideoFragment.EXTRA_REDDIT_GALLERY_VIDEO, media);
                bundle.putString(ViewRedditGalleryVideoFragment.EXTRA_SUBREDDIT_NAME, subredditName);
                fragment.setArguments(bundle);
                return fragment;
            } else {
                ViewRedditGalleryImageOrGifFragment fragment = new ViewRedditGalleryImageOrGifFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(ViewRedditGalleryImageOrGifFragment.EXTRA_REDDIT_GALLERY_MEDIA, media);
                bundle.putString(ViewRedditGalleryImageOrGifFragment.EXTRA_SUBREDDIT_NAME, subredditName);
                fragment.setArguments(bundle);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return gallery.size();
        }
    }
}
