package com.rackluxury.rolex.reddit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rolex.reddit.adapters.CustomizeThemeRecyclerViewAdapter;
import com.rackluxury.rolex.reddit.asynctasks.GetCustomTheme;
import com.rackluxury.rolex.reddit.asynctasks.InsertCustomTheme;
import com.rackluxury.rolex.reddit.customtheme.CustomTheme;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeSettingsItem;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rolex.reddit.events.RecreateActivityEvent;
import com.rackluxury.rolex.reddit.Infinity;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;
import com.rackluxury.rolex.reddit.utils.CustomThemeSharedPreferencesUtils;

public class RedditCustomizeThemeActivity extends BaseActivity {

    public static final String EXTRA_THEME_TYPE = "ETT";
    public static final int EXTRA_LIGHT_THEME = CustomThemeSharedPreferencesUtils.LIGHT;
    public static final int EXTRA_DARK_THEME = CustomThemeSharedPreferencesUtils.DARK;
    public static final int EXTRA_AMOLED_THEME = CustomThemeSharedPreferencesUtils.AMOLED;
    public static final String EXTRA_THEME_NAME = "ETN";
    public static final String EXTRA_IS_PREDEFIINED_THEME = "EIPT";
    public static final String EXTRA_CREATE_THEME = "ECT";
    private static final String CUSTOM_THEME_SETTINGS_ITEMS_STATE = "CTSIS";
    private static final String THEME_NAME_STATE = "TNS";

    @BindView(R.id.coordinator_customize_theme_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_customize_theme_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_customize_theme_activity)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_customize_theme_activity)
    RecyclerView recyclerView;
    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;
    @Inject
    @Named("light_theme")
    SharedPreferences lightThemeSharedPreferences;
    @Inject
    @Named("dark_theme")
    SharedPreferences darkThemeSharedPreferences;
    @Inject
    @Named("amoled_theme")
    SharedPreferences amoledThemeSharedPreferences;
    @Inject
    RedditDataRoomDatabase redditDataRoomDatabase;
    @Inject
    CustomThemeWrapper customThemeWrapper;
    @Inject
    Executor mExecutor;

    private String themeName;
    private boolean isPredefinedTheme;
    private ArrayList<CustomThemeSettingsItem> customThemeSettingsItems;
    private CustomizeThemeRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_theme);

        ButterKnife.bind(this);

        applyCustomTheme();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getBooleanExtra(EXTRA_CREATE_THEME, false)) {
            setTitle(R.string.customize_theme_activity_create_theme_label);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            customThemeSettingsItems = savedInstanceState.getParcelableArrayList(CUSTOM_THEME_SETTINGS_ITEMS_STATE);
            themeName = savedInstanceState.getString(THEME_NAME_STATE);
        }

        int androidVersion = Build.VERSION.SDK_INT;

        if (customThemeSettingsItems == null) {
            if (getIntent().hasExtra(EXTRA_THEME_TYPE)) {
                int themeType = getIntent().getIntExtra(EXTRA_THEME_TYPE, EXTRA_LIGHT_THEME);
                GetCustomTheme.getCustomTheme(mExecutor, new Handler(), redditDataRoomDatabase, themeType, customTheme -> {
                    if (customTheme == null) {
                        isPredefinedTheme = true;
                        switch (themeType) {
                            case EXTRA_DARK_THEME:
                                customThemeSettingsItems = CustomThemeSettingsItem.convertCustomThemeToSettingsItem(
                                        RedditCustomizeThemeActivity.this,
                                        CustomThemeWrapper.getIndigoDark(RedditCustomizeThemeActivity.this),
                                        androidVersion);
                                themeName = getString(R.string.theme_name_indigo_dark);
                                break;
                            case EXTRA_AMOLED_THEME:
                                customThemeSettingsItems = CustomThemeSettingsItem.convertCustomThemeToSettingsItem(
                                        RedditCustomizeThemeActivity.this,
                                        CustomThemeWrapper.getIndigoAmoled(RedditCustomizeThemeActivity.this),
                                        androidVersion);
                                themeName = getString(R.string.theme_name_indigo_amoled);
                                break;
                            default:
                                customThemeSettingsItems = CustomThemeSettingsItem.convertCustomThemeToSettingsItem(
                                        RedditCustomizeThemeActivity.this,
                                        CustomThemeWrapper.getIndigo(RedditCustomizeThemeActivity.this),
                                        androidVersion);
                                themeName = getString(R.string.theme_name_indigo);
                        }
                    } else {
                        customThemeSettingsItems = CustomThemeSettingsItem.convertCustomThemeToSettingsItem(
                                RedditCustomizeThemeActivity.this, customTheme, androidVersion);
                        themeName = customTheme.name;
                    }

                    adapter = new CustomizeThemeRecyclerViewAdapter(this, themeName, isPredefinedTheme);
                    recyclerView.setAdapter(adapter);
                    adapter.setCustomThemeSettingsItem(customThemeSettingsItems);
                });
            } else {
                isPredefinedTheme = getIntent().getBooleanExtra(EXTRA_IS_PREDEFIINED_THEME, false);
                themeName = getIntent().getStringExtra(EXTRA_THEME_NAME);
                adapter = new CustomizeThemeRecyclerViewAdapter(this, themeName, isPredefinedTheme);
                recyclerView.setAdapter(adapter);
                if (isPredefinedTheme) {
                    customThemeSettingsItems = CustomThemeSettingsItem.convertCustomThemeToSettingsItem(
                            RedditCustomizeThemeActivity.this,
                            CustomThemeWrapper.getPredefinedCustomTheme(this, themeName),
                            androidVersion);

                    adapter = new CustomizeThemeRecyclerViewAdapter(this, themeName, isPredefinedTheme);
                    recyclerView.setAdapter(adapter);
                    adapter.setCustomThemeSettingsItem(customThemeSettingsItems);
                } else {
                    GetCustomTheme.getCustomTheme(mExecutor, new Handler(), redditDataRoomDatabase,
                            themeName, customTheme -> {
                                customThemeSettingsItems = CustomThemeSettingsItem.convertCustomThemeToSettingsItem(
                                        RedditCustomizeThemeActivity.this, customTheme, androidVersion);

                                adapter.setCustomThemeSettingsItem(customThemeSettingsItems);
                            });
                }
            }
        } else {
            adapter = new CustomizeThemeRecyclerViewAdapter(this, themeName, isPredefinedTheme);
            recyclerView.setAdapter(adapter);
            adapter.setCustomThemeSettingsItem(customThemeSettingsItems);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customize_theme_activity, menu);
        applyMenuItemTheme(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_preview_customize_theme_activity) {
            Intent intent = new Intent(this, RedditCustomThemePreviewActivity.class);
            intent.putParcelableArrayListExtra(RedditCustomThemePreviewActivity.EXTRA_CUSTOM_THEME_SETTINGS_ITEMS, customThemeSettingsItems);
            startActivity(intent);

            return true;
        } else if (itemId == R.id.action_save_customize_theme_activity) {
            if (adapter != null) {
                themeName = adapter.getThemeName();
                if (themeName.equals("")) {
                    Snackbar.make(coordinatorLayout, R.string.no_theme_name, Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                CustomTheme customTheme = CustomTheme.convertSettingsItemsToCustomTheme(customThemeSettingsItems, themeName);
                InsertCustomTheme.insertCustomTheme(mExecutor, new Handler(), redditDataRoomDatabase, lightThemeSharedPreferences,
                        darkThemeSharedPreferences, amoledThemeSharedPreferences, customTheme,
                        false, () -> {
                            Toast.makeText(RedditCustomizeThemeActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new RecreateActivityEvent());
                            finish();
                        });
            }

            return true;
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            outState.putParcelableArrayList(CUSTOM_THEME_SETTINGS_ITEMS_STATE, customThemeSettingsItems);
            outState.putString(THEME_NAME_STATE, adapter.getThemeName());
        }
    }

    @Override
    protected SharedPreferences getDefaultSharedPreferences() {
        return sharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return customThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        applyAppBarLayoutAndToolbarTheme(appBarLayout, toolbar);
    }
}
