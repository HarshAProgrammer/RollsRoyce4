package com.rackluxury.rolex.reddit.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rolex.reddit.adapters.CustomThemeListingRecyclerViewAdapter;
import com.rackluxury.rolex.reddit.asynctasks.ChangeThemeName;
import com.rackluxury.rolex.reddit.asynctasks.DeleteTheme;
import com.rackluxury.rolex.reddit.asynctasks.GetCustomTheme;
import com.rackluxury.rolex.reddit.asynctasks.InsertCustomTheme;
import com.rackluxury.rolex.reddit.bottomsheetfragments.CreateThemeBottomSheetFragment;
import com.rackluxury.rolex.reddit.bottomsheetfragments.CustomThemeOptionsBottomSheetFragment;
import com.rackluxury.rolex.reddit.customtheme.CustomTheme;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeViewModel;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rolex.reddit.events.RecreateActivityEvent;
import com.rackluxury.rolex.reddit.Infinity;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.RedditDataRoomDatabase;
import com.rackluxury.rolex.reddit.utils.CustomThemeSharedPreferencesUtils;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class CustomThemeListingActivity extends BaseActivity implements
        CustomThemeOptionsBottomSheetFragment.CustomThemeOptionsBottomSheetFragmentListener,
        CreateThemeBottomSheetFragment.SelectBaseThemeBottomSheetFragmentListener {

    @BindView(R.id.coordinator_layout_custom_theme_listing_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_customize_theme_listing_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_customize_theme_listing_activity)
    Toolbar toolbar;
    @BindView(R.id.recycler_view_customize_theme_listing_activity)
    RecyclerView recyclerView;
    @BindView(R.id.fab_custom_theme_listing_activity)
    FloatingActionButton fab;
    @Inject
    @Named("default")
    SharedPreferences sharedPreferences;
    @Inject
    RedditDataRoomDatabase redditDataRoomDatabase;
    @Inject
    CustomThemeWrapper customThemeWrapper;
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
    Executor executor;
    public CustomThemeViewModel customThemeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_theme_listing);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CustomThemeListingRecyclerViewAdapter adapter = new CustomThemeListingRecyclerViewAdapter(this,
                CustomThemeWrapper.getPredefinedThemes(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                } else if (dy < 0) {
                    fab.show();
                }
            }
        });

        customThemeViewModel = new ViewModelProvider(this,
                new CustomThemeViewModel.Factory(redditDataRoomDatabase))
                .get(CustomThemeViewModel.class);
        customThemeViewModel.getAllCustomThemes().observe(this, adapter::setUserThemes);

        fab.setOnClickListener(view -> {
            CreateThemeBottomSheetFragment createThemeBottomSheetFragment = new CreateThemeBottomSheetFragment();
            createThemeBottomSheetFragment.show(getSupportFragmentManager(), createThemeBottomSheetFragment.getTag());
        });
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

    @Override
    public void changeName(String oldThemeName) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        EditText themeNameEditText = dialogView.findViewById(R.id.name_edit_text_edit_name_dialog);
        themeNameEditText.setText(oldThemeName);
        themeNameEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                .setTitle(R.string.edit_theme_name)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialogInterface, i)
                        -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(themeNameEditText.getWindowToken(), 0);
                    }
                    ChangeThemeName.changeThemeName(executor, redditDataRoomDatabase, oldThemeName,
                            themeNameEditText.getText().toString());
                })
                .setNegativeButton(R.string.cancel, null)
                .setOnDismissListener(dialogInterface -> {
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(themeNameEditText.getWindowToken(), 0);
                    }
                })
                .show();
    }

    @Override
    public void shareTheme(String themeName) {
        GetCustomTheme.getCustomTheme(executor, new Handler(), redditDataRoomDatabase, themeName,
                customTheme -> {
            if (customTheme != null) {
                String jsonModel = customTheme.getJSONModel();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("simple text", jsonModel);
                    clipboard.setPrimaryClip(clip);
                    Snackbar.make(coordinatorLayout, R.string.theme_copied, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(coordinatorLayout, R.string.copy_theme_faied, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, R.string.cannot_find_theme, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void delete(String themeName) {
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                .setTitle(R.string.delete_theme)
                .setMessage(getString(R.string.delete_theme_dialog_message, themeName))
                .setPositiveButton(R.string.yes, (dialogInterface, i)
                        -> DeleteTheme.deleteTheme(executor, new Handler(), redditDataRoomDatabase, themeName, (isLightTheme, isDarkTheme, isAmoledTheme) -> {
                            if (isLightTheme) {
                                CustomThemeSharedPreferencesUtils.insertThemeToSharedPreferences(
                                        CustomThemeWrapper.getIndigo(CustomThemeListingActivity.this), lightThemeSharedPreferences);
                            }
                            if (isDarkTheme) {
                                CustomThemeSharedPreferencesUtils.insertThemeToSharedPreferences(
                                        CustomThemeWrapper.getIndigoDark(CustomThemeListingActivity.this), darkThemeSharedPreferences);
                            }
                            if (isAmoledTheme) {
                                CustomThemeSharedPreferencesUtils.insertThemeToSharedPreferences(
                                        CustomThemeWrapper.getIndigoAmoled(CustomThemeListingActivity.this), amoledThemeSharedPreferences);
                            }
                            EventBus.getDefault().post(new RecreateActivityEvent());
                        }))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void shareTheme(CustomTheme customTheme) {
        if (customTheme != null) {
            String jsonModel = customTheme.getJSONModel();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("simple text", jsonModel);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(coordinatorLayout, R.string.theme_copied, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, R.string.copy_theme_faied, Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(coordinatorLayout, R.string.cannot_find_theme, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onRecreateActivityEvent(RecreateActivityEvent recreateActivityEvent) {
        ActivityCompat.recreate(this);
    }

    @Override
    public void importTheme() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            // If it does contain data, decide if you can handle the data.
            if (!clipboard.hasPrimaryClip()) {
                Snackbar.make(coordinatorLayout, R.string.no_data_in_clipboard, Snackbar.LENGTH_SHORT).show();
            } else if (clipboard.getPrimaryClipDescription() != null &&
                    !clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                // since the clipboard has data but it is not plain text
                Snackbar.make(coordinatorLayout, R.string.no_data_in_clipboard, Snackbar.LENGTH_SHORT).show();
            } else if (clipboard.getPrimaryClip() != null) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                String json = item.getText().toString();
                try {
                    CustomTheme customTheme = new Gson().fromJson(json, CustomTheme.class);
                    checkDuplicateAndImportTheme(customTheme, true);
                } catch (JsonSyntaxException e) {
                    Snackbar.make(coordinatorLayout, R.string.parse_theme_failed, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkDuplicateAndImportTheme(CustomTheme customTheme, boolean checkDuplicate) {
        InsertCustomTheme.insertCustomTheme(executor, new Handler(), redditDataRoomDatabase, lightThemeSharedPreferences,
                darkThemeSharedPreferences, amoledThemeSharedPreferences, customTheme, checkDuplicate,
                new InsertCustomTheme.InsertCustomThemeListener() {
                    @Override
                    public void success() {
                        Toast.makeText(CustomThemeListingActivity.this, R.string.import_theme_success, Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new RecreateActivityEvent());
                    }

                    @Override
                    public void duplicate() {
                        new MaterialAlertDialogBuilder(CustomThemeListingActivity.this, R.style.MaterialAlertDialogTheme)
                                .setTitle(R.string.duplicate_theme_name_dialog_title)
                                .setMessage(getString(R.string.duplicate_theme_name_dialog_message, customTheme.name))
                                .setPositiveButton(R.string.rename, (dialogInterface, i)
                                        -> {
                                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
                                    EditText themeNameEditText = dialogView.findViewById(R.id.name_edit_text_edit_name_dialog);
                                    themeNameEditText.setText(customTheme.name);
                                    themeNameEditText.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) {
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                    }
                                    new MaterialAlertDialogBuilder(CustomThemeListingActivity.this, R.style.MaterialAlertDialogTheme)
                                            .setTitle(R.string.edit_theme_name)
                                            .setView(dialogView)
                                            .setPositiveButton(R.string.ok, (editTextDialogInterface, i1)
                                                    -> {
                                                if (imm != null) {
                                                    imm.hideSoftInputFromWindow(themeNameEditText.getWindowToken(), 0);
                                                }
                                                if (!themeNameEditText.getText().toString().equals("")) {
                                                    customTheme.name = themeNameEditText.getText().toString();
                                                }
                                                checkDuplicateAndImportTheme(customTheme, true);
                                            })
                                            .setNegativeButton(R.string.cancel, null)
                                            .setOnDismissListener(editTextDialogInterface -> {
                                                if (imm != null) {
                                                    imm.hideSoftInputFromWindow(themeNameEditText.getWindowToken(), 0);
                                                }
                                            })
                                            .show();
                                })
                                .setNegativeButton(R.string.override, (dialogInterface, i) -> {
                                    checkDuplicateAndImportTheme(customTheme, false);
                                })
                                .show();
                    }
                });
    }
}
