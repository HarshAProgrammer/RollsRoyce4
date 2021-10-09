package com.rackluxury.rollsroyce.activities;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import es.dmoral.toasty.Toasty;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.adapters.CategoriesData;
import com.rackluxury.rollsroyce.adapters.MyCategoriesAdapter;
import com.rackluxury.rollsroyce.adapters.UserProfile;
import com.rackluxury.rollsroyce.blog.BlogActivity;
import com.rackluxury.rollsroyce.blog.BlogCheckerActivity;
import com.rackluxury.rollsroyce.facts.FactsActivity;
import com.rackluxury.rollsroyce.images.ImagesActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditMainActivity;
import com.rackluxury.rollsroyce.video.VideoActivity;
import com.rackluxury.rollsroyce.video.VideoCheckerActivity;
import com.rackluxury.rollsroyce.youtube.YouTubeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final List<CategoriesData> favouriteCategories = new ArrayList<>();
    final List<CategoriesData> deletedCategories = new ArrayList<>();
    RecyclerView categoriesRecyclerView;
    List<CategoriesData> myCategoriesList;
    CategoriesData mCategoriesData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    boolean isOpen = false;

    private int soundLike;

    private TextView coins2;
    public SharedPreferences coins;
    private String currentCoins;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef;

    private SoundPool soundPool;
    private int soundFBShare;
    private int soundFBLike;
    private int soundTwitterShare;

    private Toolbar toolbar;
    private MyCategoriesAdapter myCategoriesAdapter;
    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();
            final CategoriesData categoriesItem = myCategoriesList.get(position);
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedCategories.add(categoriesItem);
                    myCategoriesList.remove(position);
                    myCategoriesAdapter.notifyItemRemoved(position);
                    Snackbar.make(categoriesRecyclerView, "Deleted.", Snackbar.LENGTH_LONG).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    favouriteCategories.add(categoriesItem);
                    myCategoriesAdapter.notifyItemRemoved(position);
                    soundPool.play(soundLike, 1, 1, 0, 0, 1);
                    Snackbar.make(categoriesRecyclerView, "Added to favourites.", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView categoriesRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(HomeActivity.this, c, categoriesRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorRed))
                    .addSwipeLeftActionIcon(R.drawable.ic_deleted_swipe_main)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorGreen))
                    .addSwipeRightActionIcon(R.drawable.ic_favourite_swipe_main)
                    .setActionIconTint(ContextCompat.getColor(categoriesRecyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, categoriesRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    private FirebaseDatabase firebaseDatabase;
    private String ShareOnFacebookString;
    private String ShareOnTwitterString;
    private UserProfile userProfile;
    private ImageView navUserPhoto;
    private DrawerLayout drawer;
    private TextView navUsername, navUserMail;
    private int lastPosition;
    private FloatingActionButton fabMore, fabFav, fabVideos;
    private Animation fromBottom, toBottom, rotateOpen, rotateClose;
    ImageView greetImg;
    RelativeLayout greetLay;
    TextView greetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpUIViewsHomeActivity();
        initToolbar();
        setupNavigationDrawer();
        loadMainData();
        itemTouchCategories();


        coins = getSharedPreferences("Rewards", MODE_PRIVATE);
        currentCoins = coins.getString("Coins", "0");
        coins2 = findViewById(R.id.tvCoinsHome);
        coins2.setText(currentCoins);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("Coins").setValue(currentCoins);

        fromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.to_bottom_anim);
        rotateOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close_anim);

        fabVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeVideos();

            }
        });
        fabFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoriesFav();

            }
        });
        fabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    fabVideos.startAnimation(fromBottom);
                    fabFav.startAnimation(fromBottom);
                    fabMore.startAnimation(rotateOpen);

                    fabVideos.setClickable(true);
                    fabFav.setClickable(true);
                    isOpen = false;
                } else {
                    fabVideos.startAnimation(toBottom);
                    fabFav.startAnimation(toBottom);
                    fabMore.startAnimation(rotateClose);

                    fabVideos.setClickable(false);
                    fabFav.setClickable(false);
                    isOpen = true;
                }

            }
        });


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categoriesRecyclerView.setLayoutManager(layoutManager);
        categoriesRecyclerView.setHasFixedSize(true);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPosCategories", 0);
        categoriesRecyclerView.scrollToPosition(lastPosition);

        categoriesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = layoutManager.findFirstVisibleItemPosition();
            }
        });

        greeting();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                greetImg.animate().alpha(0).translationY(-greetImg.getHeight()).setDuration(1000);
                greetText.animate().alpha(0).translationY(-greetText.getHeight()).setDuration(1000);
                greetLay.animate().alpha(0).translationY(-greetLay.getHeight()).setDuration(1000);
                Handler handler = new Handler();
                int TRANSITION_SCREEN_LOADING_TIME = 1000;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        greetLay.setVisibility(View.GONE);

                    }
                }, TRANSITION_SCREEN_LOADING_TIME);
            }
        }, 1000);

    }

    private void greeting() {
        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greetText.setText("Good Morning");
            greetImg.setImageResource(R.drawable.img_greet_half_morning);
        } else if (timeOfDay >= 12 && timeOfDay < 15) {
            greetText.setText("Good Afternoon");
            greetImg.setImageResource(R.drawable.img_greet_half_afternoon);
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            greetText.setText("Good Evening");
            greetImg.setImageResource(R.drawable.img_greet_half_without_sun);
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            greetText.setText("Good Night");
            greetText.setTextColor(Color.WHITE);
            greetImg.setImageResource(R.drawable.img_greet_half_night);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosCategories", lastPosition);
        e.apply();
    }

    private void setUpUIViewsHomeActivity() {

        toolbar = findViewById(R.id.toolbarHomeActivity);
        categoriesRecyclerView = findViewById(R.id.rvCategoriesRecycler);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_username);
        navUserMail = headerView.findViewById(R.id.nav_user_mail);
        navUserPhoto = headerView.findViewById(R.id.nav_user_photo);
        fabMore = findViewById(R.id.fabMoreCategories);
        fabFav = findViewById(R.id.fabFavCategories);
        fabVideos = findViewById(R.id.fabVideosCategories);

        greetImg = findViewById(R.id.ivGreetHome);
        greetText = findViewById(R.id.tvGreetHome);
        greetLay = findViewById(R.id.rlGreetHome);


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
        soundTwitterShare = soundPool.load(this, R.raw.sound_share_on_twitter, 1);
        soundFBShare = soundPool.load(this, R.raw.sound_share_on_facebook, 1);
        soundFBLike = soundPool.load(this, R.raw.sound_like_us_on_facebook, 1);
        soundLike = soundPool.load(this, R.raw.sound_like, 1);


    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

    }

    private void setupNavigationDrawer() {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open_home, R.string.navigation_drawer_close_home);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        updateNavHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        getMenuInflater().inflate(R.menu.toolbar_search_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_main);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_home_price_low_to_high) {
            sortViewPriceLowToHigh();
            return true;
        } else if (item.getItemId() == R.id.sort_home_price_high_to_low) {
            sortViewPriceHighToLow();
            return true;
        } else if (item.getItemId() == R.id.sort_home_name_a_to_z) {
            sortViewNameAToZ();
            return true;
        } else if (item.getItemId() == R.id.sort_home_name_z_to_a) {
            sortViewNameZtoA();
            return true;
        } else if (item.getItemId() == R.id.favourite_categories) {
            openCategoriesFav();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCategoriesFav() {
        Intent view = new Intent(HomeActivity.this, FavouriteCategoriesActivity.class);
        startActivity(view);
        Animatoo.animateSplit(HomeActivity.this);
    }

    private void homeVideos() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Video Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                finish();
                Intent openVideoFromMain = new Intent(HomeActivity.this, VideoActivity.class);
                startActivity(openVideoFromMain);
                Animatoo.animateSplit(HomeActivity.this);

            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Video Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseMessaging.getInstance().subscribeToTopic("purchase_video");
                finish();
                Intent openVideoCheckerFromMain = new Intent(HomeActivity.this, VideoCheckerActivity.class);
                startActivity(openVideoCheckerFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });


    }

    public void sortViewNameAToZ() {
        Collections.sort(myCategoriesList, CategoriesData.ByNameAToZ);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    public void sortViewNameZtoA() {
        Collections.sort(myCategoriesList, CategoriesData.ByNameZToA);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceLowToHigh() {
        Collections.sort(myCategoriesList, CategoriesData.ByPriceLowToHigh);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceHighToLow() {
        Collections.sort(myCategoriesList, CategoriesData.ByPriceHighToLow);
        myCategoriesAdapter.notifyDataSetChanged();
    }

    private void filter(String text) {

        ArrayList<CategoriesData> filterList = new ArrayList<>();

        for (CategoriesData item : myCategoriesList) {

            if (item.getCategoriesName().toLowerCase().contains(text.toLowerCase())) {

                filterList.add(item);

            }

        }

        myCategoriesAdapter.filteredList(filterList);

    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.expensiveNavigation) {
            ExpensiveWatches();

        } else if (id == R.id.imagesNavigation) {
            Images();

        } else if (id == R.id.videoNavigation) {
            homeVideos();

        } else if (id == R.id.checkinNavigation) {
            homeCheckin();

        } else if (id == R.id.redeemNavigation) {
            homeRedeem();

        } else if (id == R.id.youtubeVideoNavigation) {
            youtubeVideos();

        } else if (id == R.id.blogNavigation) {
            Blog();

        } else if (id == R.id.redditNavigation) {
            Reddit();

        } else if (id == R.id.profileNavigation) {
            ProfileDisplay();

        } else if (id == R.id.billingNavigation) {
            InAppPurchase();

        } else if (id == R.id.factsNavigation) {
            FactsAboutUs();

        } else if (id == R.id.signOutNavigation) {
            SignOut();


        } else if (id == R.id.shareNavigation) {
            GeneralShareMain();

        } else if (id == R.id.facebookLikeNavigation) {
            LikeFacebookPage();

        } else if (id == R.id.facebookShareNavigation) {
            ShareOnFacebook();

        } else if (id == R.id.twitterShareNavigation) {
            ShareOnTwitter();
        } else if (id == R.id.companyNavigation) {
            openCompanyInfo();
        } else if (id == R.id.aboutUsNavigation) {

            aboutUs();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Blog() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Blog Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                finish();
                Intent openExpensiveFromMain = new Intent(HomeActivity.this, BlogActivity.class);
                startActivity(openExpensiveFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Blog Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseMessaging.getInstance().subscribeToTopic("purchase_blog");
                finish();
                Intent openBlogCheckerFromMain = new Intent(HomeActivity.this, BlogCheckerActivity.class);
                startActivity(openBlogCheckerFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });
    }

    private void Reddit() {
        Intent openBlogFromMain = new Intent(HomeActivity.this, RedditMainActivity.class);
        startActivity(openBlogFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void homeCheckin() {
        finish();
        Intent intent = new Intent(HomeActivity.this, DailyLoginActivity.class);
        startActivity(intent);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void homeRedeem() {
        finish();
        Intent intent = new Intent(HomeActivity.this, RedeemActivity.class);
        startActivity(intent);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void ExpensiveWatches() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                finish();
                Intent openExpensiveFromMain = new Intent(HomeActivity.this, ExpensiveActivity.class);
                startActivity(openExpensiveFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Expensive Purchased").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseMessaging.getInstance().subscribeToTopic("purchase_expensive");
                finish();
                Intent openExpensiveCheckerFromMain = new Intent(HomeActivity.this, ExpensiveCheckerActivity.class);
                startActivity(openExpensiveCheckerFromMain);
                Animatoo.animateSwipeRight(HomeActivity.this);

            }
        });

    }

    private void Images() {
        Intent openImagesFromMain = new Intent(HomeActivity.this, ImagesActivity.class);
        startActivity(openImagesFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);

    }

    private void youtubeVideos() {
        Intent openYoutubeVideoFromMain = new Intent(HomeActivity.this, YouTubeActivity.class);
        startActivity(openYoutubeVideoFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void ProfileDisplay() {
        Intent openProfileActivityFromMain = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(openProfileActivityFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void InAppPurchase() {
        Intent openBillingFromMain = new Intent(HomeActivity.this, BillingActivity.class);
        startActivity(openBillingFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void FactsAboutUs() {
        Intent openFactsFromMain = new Intent(HomeActivity.this, FactsActivity.class);
        startActivity(openFactsFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    private void SignOut() {

        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.alert_dialog_sign_out, null);
        Button acceptButton = view.findViewById(R.id.btnAcceptAlertSignOut);
        Button cancelButton = view.findViewById(R.id.btnRejectAlertSignOut);
        final AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                .setView(view)
                .show();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                finish();
                Intent openLoginActivityFromMain = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(openLoginActivityFromMain);
                Animatoo.animateSlideDown(HomeActivity.this);


            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


    }

    private void GeneralShareMain() {

        setShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_LOADING_TIME = 3400;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent GeneralShareMainIntent = new Intent(Intent.ACTION_SEND);
                final String appPackageName = getApplicationContext().getPackageName();
                String appLink = "https://play.google.com/store/apps/details?id=" + appPackageName;


                GeneralShareMainIntent.setType("Text/plain");
                String generalMainShareBody = "Rolls Royce:A Crown For Every Achievement      " +
                        "" +
                        appLink;
                String generalMainShareSub = "Rolls Royce App";
                GeneralShareMainIntent.putExtra(Intent.EXTRA_SUBJECT, generalMainShareSub);
                GeneralShareMainIntent.putExtra(Intent.EXTRA_TEXT, generalMainShareBody);
                startActivity(Intent.createChooser(GeneralShareMainIntent, "Share Via"));
                Animatoo.animateSpin(HomeActivity.this);
            }
        }, TRANSITION_SCREEN_LOADING_TIME);


    }

    private void setShareDialogue() {
        final ShareDialogue shareDialogue = new ShareDialogue(HomeActivity.this);
        shareDialogue.startShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 3000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shareDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void LikeFacebookPage() {
        try {
            soundPool.play(soundFBLike, 1, 1, 0, 0, 1);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "101799174956163"));
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + "101799174956163"));
            startActivity(intent);

        }

    }

    private void ShareOnFacebook() {
        setFacebookShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_LOADING_TIME = 4500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                    soundPool.play(soundFBShare, 1, 1, 0, 0, 1);

                    Intent shareOnFacebookIntent = new Intent(Intent.ACTION_SEND);
                    shareOnFacebookIntent.setType("text/plain");
                    final String appPackageName = getApplicationContext().getPackageName();
                    ShareOnFacebookString = "https://play.google.com/store/apps/details?id=" +
                            "" + appPackageName;
                    shareOnFacebookIntent.putExtra(Intent.EXTRA_TEXT, ShareOnFacebookString);
                    shareOnFacebookIntent.setPackage("com.facebook.katana");
                    startActivity(shareOnFacebookIntent);
                    Animatoo.animateSpin(HomeActivity.this);


                } catch (Exception FacebookException) {

                    Toasty.warning(HomeActivity.this, "Install the Facebook App", Toast.LENGTH_LONG).show();

                }
            }
        }, TRANSITION_SCREEN_LOADING_TIME);
    }

    private void setFacebookShareDialogue() {
        final FacebookShareDialogue facebookShareDialogue = new FacebookShareDialogue(HomeActivity.this);
        facebookShareDialogue.startFacebookShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                facebookShareDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void ShareOnTwitter() {
        setTwitterShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_LOADING_TIME = 4500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    soundPool.play(soundTwitterShare, 1, 1, 0, 0, 1);

                    Intent shareOnTwitterIntent = new Intent(Intent.ACTION_SEND);
                    shareOnTwitterIntent.setType("text/plain");
                    final String appPackageName = getApplicationContext().getPackageName();
                    ShareOnTwitterString = "https://play.google.com/store/apps/details?id=" + appPackageName;
                    shareOnTwitterIntent.putExtra(Intent.EXTRA_TEXT, "Rolls Royce:A Crown For Every Achievement      " +
                            "" + ShareOnTwitterString);
                    shareOnTwitterIntent.setPackage("com.twitter.android");
                    startActivity(shareOnTwitterIntent);
                    Animatoo.animateSpin(HomeActivity.this);


                } catch (Exception TwitterException) {

                    Toasty.warning(HomeActivity.this, "Install the Twitter App", Toast.LENGTH_LONG).show();

                }
            }
        }, TRANSITION_SCREEN_LOADING_TIME);
    }

    private void setTwitterShareDialogue() {
        final TwitterShareDialogue twitterShareDialogue = new TwitterShareDialogue(HomeActivity.this);
        twitterShareDialogue.startTwitterShareDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                twitterShareDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void aboutUs() {
        Intent openAboutUsFromMain = new Intent(HomeActivity.this, AboutUsActivity.class);
        startActivity(openAboutUsFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);

    }

    private void openCompanyInfo() {
        Intent openCompanyInfoActivityFromMain = new Intent(HomeActivity.this, CompanyInfo.class);
        startActivity(openCompanyInfoActivityFromMain);
        Animatoo.animateSwipeRight(HomeActivity.this);
    }

    public void updateNavHeader() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(navUserPhoto);
                final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
                displayDataEmailPassword(databaseReference);
            }
        });
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                navUserMail.setText(currentUser.getEmail());
                navUsername.setText(currentUser.getDisplayName());
                Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
            }
        });

    }

    private void displayDataEmailPassword(DatabaseReference databaseReference) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);


                navUsername.setText(userProfile.getUserName());
                navUserMail.setText(userProfile.getUserEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(HomeActivity.this, databaseError.getCode(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void itemTouchCategories() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(categoriesRecyclerView);
    }

    private void loadMainData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, 1);
        categoriesRecyclerView.setLayoutManager(gridLayoutManager);
        myCategoriesList = new ArrayList<>();
        mCategoriesData = new CategoriesData("2021 Rolls-Royce Cullinan", "•\tBase price: $340,350\n" +
                "•\tEngine: 6.8L twin-turbocharged V12\n" +
                "•\tPower: 563 hp @ 5,000 rpm\n" +
                "•\tTorque: 663 lb-ft @ 1,700 rpm\n" +
                "•\t0-60 mph: 4.8 s\n" +
                "•\tTop Speed: 155 mph\n" +
                "The newest addition to the Rolls-Royce lineup is the automaker’s only SUV. While unapologetically different from anything else on offer, it still stands out as a Rolls-Royce thanks to its distinguishable and familiar rectangular front grill. Its DNA is not just limited to outward appearances though, as the opulent interior design elements and phenomenal V12 performance that we become familiar with from the brand, live on in this all-terrain automobile. There are no big updates to the Cullinan for the 2021 model year.\n", "340350", R.drawable.first_categories, "0", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Rolls-Royce Ghost", "•\tBase price: $320,000\n" +
                "•\tEngine: 6.8L twin-turbocharged V12\n" +
                "•\tPower: 563 hp @ 5000 rpm\n" +
                "•\tTorque: 627 lb-ft @ 1600 rpm\n" +
                "•\t0-60 mph: 4.3 s\n" +
                "•\tTop Speed: 155 mph\n" +
                "The ‘entry-level’ Rolls-Royce was been completely redesigned for the 2021 model year and also carries some tweaks into 2021 too. It gets its unique chassis – it had been built upon the BMW 7-series frame until this change. The interior also offers generous space for both front- and rear-seat passengers to relax in comfort. The 2021 Ghost gets fancier appointments and more modern technology than before. The upgrades make this a clever luxury car that has no equal to any other brand.\n"
                , "320000", R.drawable.second_categories, "1", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2020 Rolls-Royce Phantom", "•\tBase price: $463,350\n" +
                "•\tEngine: 6.8L twin-turbocharged V12\n" +
                "•\tPower: 563 hp @ 5,000 rpm\n" +
                "•\tTorque: 663 lb-ft @ 1,700 rpm\n" +
                "•\t0-60 mph: 5.0 s\n" +
                "•\tTop Speed: 155 mph\n" +
                "The Rolls-Royce Phantom is the status symbol car to rule all status symbol cars. Armed with a new platform for the 2020 model year, the Phantom has also been designed to be fitted with an EV powertrain in the future. Many say that the Phantom has created its class of car for itself, and the new iteration looks to extend that trend for the long run; improving the model now and preparing it for relevancy in the more distant future. There are no announcement updates for the 2021 model year.\n"
                , "463350", R.drawable.third_categories, "2", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Rolls-Royce Wraith", "•\tBase price: $343,350\n" +
                "•\tEngine: 6.6L twin-turbocharged V12\n" +
                "•\tPower: 624 hp @ 5,600 rpm\n" +
                "•\tTorque: 605 lb-ft @ 1,500 rpm\n" +
                "•\t0-60 mph: 4.1 s\n" +
                "•\tTop Speed: 155 mph\n" +
                "Virtually unchanged for 2021, the current iteration of the Rolls-Royce Wraith shares the same BMW F01 platform and was based on the outgoing Ghost, and will continue to do so for at least the meantime. The Wraith is the sportier version of the two, having been purposed in a coupe guise and shorter wheelbase for starters. A power bump, some weight loss, and a specially tuned suspension, further its call for an improved performance focus.\n"
                , "343350", R.drawable.fourth_categories, "3", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Rolls-Royce Dawn", "•\tBase price: $368,850\n" +
                "•\tEngine: 6.8L twin-turbocharged V12\n" +
                "•\tPower: 563 hp @ 5,000 rpm\n" +
                "•\tTorque: 663 lb-ft @ 1,700 rpm\n" +
                "•\t0-60 mph: 4.8 s\n" +
                "•\tTop Speed: 155 mph\n" +
                "The Rolls-Royce Dawn is the soft-top version of the Wraith, but at the same time, it’s not. 80 percent of the Wraith’s body panels are unique, meaning that the Dawn is much more than just the coupe version with its roof sheared off. Mind you, the drop-top factor you get from the Dawn is still its most endearing feature, allowing you to partake in the Rolls-Royce wind-through-your-hair experience like no other car in the lineup can deliver. No changes for the 2021 model year.\n"

                , "368850", R.drawable.fifth_categories, "4", "0");
        myCategoriesList.add(mCategoriesData);
        mCategoriesData = new CategoriesData("2021 Rolls-Royce Phantom EWB", "•\tBase price: $484,425\n" +
                "•\tEngine: 6.8L twin-turbocharged V12\n" +
                "•\tPower: 563 hp @ 5000 rpm\n" +
                "•\tTorque: 627 lb-ft @ 1600 rpm\n" +
                "•\t0-60 mph: 4.3 s\n" +
                "•\tTop Speed: 155 mph\n" +
                "In this case, EWB stands for ‘extended wheelbase’, alluding to what could be duly described as a stretched-out Phantom. If one were to ever be chauffeured around in a car, this one would probably top most wishlists. Aside from the glaringly obvious, the EWB also has some unique offerings that can only be seen from inside the car, such as the “private gallery”. The aforementioned is created by a single panel of glass that spans the entire fascia to house a unique gallery displaying bespoke artwork.\n", "492425", R.drawable.sixth_categories, "5", "0");
        myCategoriesList.add(mCategoriesData);

        myCategoriesAdapter = new MyCategoriesAdapter(HomeActivity.this, myCategoriesList);
        categoriesRecyclerView.setAdapter(myCategoriesAdapter);
    }


}