package com.rackluxury.rollsroyce.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.adapters.ExpensiveData;
import com.rackluxury.rollsroyce.adapters.MyExpensiveAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ExpensiveActivity extends AppCompatActivity {
    RecyclerView expensiveRecyclerView;
    List<ExpensiveData> myExpensiveList;
    ExpensiveData mExpensiveData;

    private SharedPreferences prefs;

    private Toolbar toolbar;
    private ImageView backIcon;

    private MyExpensiveAdapter myExpensiveAdapter;

    private int soundLike;


    private SoundPool soundPool;

    final List<ExpensiveData> favouriteExpensive = new ArrayList<>();
    final List<ExpensiveData> deletedExpensive = new ArrayList<>();
    private int lastPosition;
    private ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expensive);
        setUpUIViewsExpensiveActivity();
        setExpensiveDialogue();
        initToolbar();
        loadMainData();
        itemTouchExpensive();


        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("expensiveFirst", true);
        if (firstStart) {
            setPurchaseSuccessDialogue();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setShimmer(null);

            }
        },1500);







        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        expensiveRecyclerView.setLayoutManager(layoutManager);
        expensiveRecyclerView.setHasFixedSize(true);
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPosExpensive", 0);
        expensiveRecyclerView.scrollToPosition(lastPosition);

        expensiveRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = layoutManager.findFirstVisibleItemPosition();
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosExpensive", lastPosition);
        e.apply();
    }

    private void setUpUIViewsExpensiveActivity() {
        toolbar = findViewById(R.id.toolbarExpensivePage);
        backIcon = findViewById(R.id.backIconExpensive);
        expensiveRecyclerView = findViewById(R.id.rvExpensiveRecycler);
        shimmerFrameLayout =  findViewById(R.id.sflExpensive);

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

    }
    private void setPurchaseSuccessDialogue() {
        final PurchaseSuccessDialogue purchaseSuccessDialogue = new PurchaseSuccessDialogue(ExpensiveActivity.this);
        purchaseSuccessDialogue.startPurchaseSuccessDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 4000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                purchaseSuccessDialogue.dismissDialogue();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("expensiveFirst", false);
                editor.apply();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void setExpensiveDialogue() {
        final ExpensiveDialogue expensiveDialogue = new ExpensiveDialogue(ExpensiveActivity.this);
        expensiveDialogue.startExpensiveDialogue();
        Handler handler = new Handler();
        int TRANSITION_SCREEN_TIME = 1500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                expensiveDialogue.dismissDialogue();
            }
        }, TRANSITION_SCREEN_TIME);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent openHomeFromExpensive = new Intent(ExpensiveActivity.this,HomeActivity.class);
                startActivity(openHomeFromExpensive);
                Animatoo.animateSwipeLeft(ExpensiveActivity.this);
            }
        });
    }

    private void loadMainData() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ExpensiveActivity.this, 1);
        expensiveRecyclerView.setLayoutManager(gridLayoutManager);
        myExpensiveList = new ArrayList<>();
        mExpensiveData = new ExpensiveData("Rolls Royce Cosmograph Daytona-Paul Newman", "The dream of any watch collector would be to own this exact Rolls Royce Paul Newman Daytona produced in 1968. It is the first Rolls Royce Daytona watch fitted with an “exotic dial.” It also has a tachymeter bezel and features a personal engraving by Newman’s wife that reads, “Drive Carefully, Me” on its stainless-steel case-back."
                , "17752500", R.drawable.newman_expensive,"0","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Cosmograph Daytona-The Unicorn", "This exquisite chronograph is the only Daytona of its kind (hence its nickname). It is entirely crafted in white gold and sports a black dial and a bark-finished bracelet. It was owned by John Goldberger, one of the most prominent collectors in the world, who sold it to raise funds for charity, explains Boutros."
                , "6132618", R.drawable.unicorn_expensive,"1","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Cellini-Bao Dai triple calendar moonphase ", "This superbly-preserved, 18-karat yellow-gold automatic Rolls Royce was previously owned by Vietnamese emperor Bao Dai. It has a triple calendar with moon phase dial that is also set with diamonds at 12, 3, 5, 8, and 10 o’clock. Rolls Royce only produced that triple calendar and moon phase watch for a couple of years starting in 1950 before it re-introduced them again in 2016."
                , "5232479", R.drawable.bao_dai_expensive,"2","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Cosmograph Daytona-The Legend", "This 18-karat yellow gold Oyster Cosmograph \"Paul Newman,\" aka The Legend, is one of only three known similar watches on the market. Its most fascinating feature is the dial—a “creamy lemon shade,” as Phillips put it in its catalogue—in sharp contrast with the ivory dials that were fitted to stainless steel Paul Newmans at the time."
                , "3844312", R.drawable.legend_expensive,"3","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Cosmograph Daytona-The Neanderthal", "Produced for a few years only around 1965, this is the first “Cosmograph” wristwatch equipped with screw-down chronograph pushers for improved water resistance. Its unique black and white dial features oversized registers and black outer seconds marks set against a white track."
                , "3111497", R.drawable.neanderthal_expensive,"4","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Cellini-Antimagnetique", "The 1942 Antimagnetique Reference 4113 is the oldest Rolls Royce on our list after selling for nearly $2.5 million during a Phillips’ auction in Geneva in 2016. It comes in an extraordinarily large case considering its production year, at 44 mm. It is in fact, the largest case ever built by  RollsRoyce. Only 12 examples of the Reference 4113 are known to still exist today. The watch was initially produced as a gift to celebrate the victory of a racing team."
                , "2500000", R.drawable.antimagnetique_expensive,"5","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce GMT-Master-Marlon Brando’s Apocalypse Now", "This Rolls Royce GMT ref. 1675 worn by Marlon Brando in the movie Apocalypse Now was sold for $1.952 million at a Phillips auction in December 2019. The auction, appropriately named the Phillips Game Changers event, saw the Rolls Royce GMT-Master manufactured in 1972 sell well above the expected price."
                , "1952000", R.drawable.marlon_expensive,"6","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Cosmograph Daytona-Eric Clapton’s Oyster Albino ", "Eric Clapton’s Daytona, dubbed the Rolls Royce “Oyster Albino” Cosmograph, sold not once but twice for substantial amounts during public auctions. The first time was in 2003 in New York where the watch was bought for $505,000 during an auction organized by Sotheby’s. Twelve years later, in May 2015, Eric Clapton’s Daytona was sold once more but this time for $1.4 million at a Phillips auction in Geneva, nearly three times its previous price."
                , "1400000", R.drawable.clapton_expensive,"7","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Day-Date-Jack Nicklaus", "Rolls Royce gave this yellow gold Day-Date to legendary golfer Jack Nicklaus in 1967. He pretty much wore it every day since then. Jack Nicklaus, known as the Golden Bear, wore this Rolls Royce when he won 12 of his record 18 professional major championship titles. The watch was also often photographed when Nicklaus hoisted the championship trophy following his victories."
                , "1220000", R.drawable.nicklaus_expensive,"8","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce GMT Master II-Ice", "This Rolls Royce GMT-Master Ice is the watch that football superstar Cristiano Ronaldo wore during the 14th Dubai International Sports Conference. It is the most expensive new Rolls Royce you can buy. It retails for $485,350 but its price might vary depending on the market price for diamonds at the time of your purchase."
                , "485350", R.drawable.ice_expensive,"9","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Submariner-James Bond’s 1972", "Yet another Submariner in our list of the most expensive Rolls Royce watches ever sold. This one is the actual watch worn by Sir Roger Moore while playing James Bond in Live and Let Die in 1973. The watch was sold for $365,000 at a Phillips auction in Geneva in 2015."
                , "365000", R.drawable.james_bond_expensive,"10","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Pearlmaster-Platinum Diamond", "Unlike its modern sibling, the Rolls Royce Platinum Diamond Pearlmaster is based on the Day-Date collection instead of the Datejust. The limited-edition watch is made from meteorite diamond and counts 42 diamonds on its custom bezel. The 39mm dial watch was first released in 2011 for $277,850."
                , "277850", R.drawable.platinum_expensive,"11","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Submariner-Steve McQueen’s 1967", "You could argue that Steve McQueen’s 1967 Rolls Royce Submariner started the modern craving for expensive Rolls Royce watches. The watch is the first in our list to be sold for a high price at an auction in 2009. Organized by Antiquorum, the auction closed at $234,000, which was the highest price ever paid for a expensive Rolls Royce at the time."
                , "234000", R.drawable.steve_mcqueen_expensive,"12","0");
        myExpensiveList.add(mExpensiveData);
        myExpensiveAdapter = new MyExpensiveAdapter(ExpensiveActivity.this, myExpensiveList);
        expensiveRecyclerView.setAdapter(myExpensiveAdapter);
    }

    private void itemTouchExpensive() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(expensiveRecyclerView);
    }

    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();
            final ExpensiveData ExpensiveItem = myExpensiveList.get(position);
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    deletedExpensive.add(ExpensiveItem);
                    myExpensiveList.remove(position);
                    myExpensiveAdapter.notifyItemRemoved(position);
                    Snackbar.make(expensiveRecyclerView, "Deleted.", Snackbar.LENGTH_LONG).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    favouriteExpensive.add(ExpensiveItem);
                    myExpensiveAdapter.notifyItemRemoved(position);
                    soundPool.play(soundLike, 1, 1, 0, 0, 1);
                    Snackbar.make(expensiveRecyclerView, "Added to favourites.", Snackbar.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView expensiveRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(ExpensiveActivity.this, c, expensiveRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ExpensiveActivity.this, R.color.colorRed))
                    .addSwipeLeftActionIcon(R.drawable.ic_deleted_swipe_main)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ExpensiveActivity.this, R.color.colorGreen))
                    .addSwipeRightActionIcon(R.drawable.ic_favourite_swipe_main)
                    .setActionIconTint(ContextCompat.getColor(expensiveRecyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, expensiveRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expensive_menu, menu);
        getMenuInflater().inflate(R.menu.toolbar_search_expensive, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search_expensive);

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
        if (item.getItemId() == R.id.sort_expensive_price_low_to_high) {
            sortViewPriceLowToHigh();
            return true;
        } else if (item.getItemId() == R.id.sort_expensive_price_high_to_low) {
            sortViewPriceHighToLow();
            return true;
        } else if (item.getItemId() == R.id.sort_expensive_name_a_to_z) {
            sortViewNameAToZ();
            return true;
        } else if (item.getItemId() == R.id.sort_expensive_name_z_to_a) {
            sortViewNameZtoA();
            return true;
        } else if (item.getItemId() == R.id.favourite_expensive) {
            openExpensiveFav();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openExpensiveFav() {
        Intent view = new Intent(ExpensiveActivity.this, FavouriteExpensiveActivity.class);
        startActivity(view);
        Animatoo.animateSplit(ExpensiveActivity.this);
    }

    public void sortViewNameAToZ() {
        Collections.sort(myExpensiveList, ExpensiveData.ByNameAToZ);
        myExpensiveAdapter.notifyDataSetChanged();
    }

    public void sortViewNameZtoA() {
        Collections.sort(myExpensiveList, ExpensiveData.ByNameZToA);
        myExpensiveAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceLowToHigh() {
        Collections.sort(myExpensiveList, ExpensiveData.ByPriceLowToHigh);
        myExpensiveAdapter.notifyDataSetChanged();
    }

    public void sortViewPriceHighToLow() {
        Collections.sort(myExpensiveList, ExpensiveData.ByPriceHighToLow);
        myExpensiveAdapter.notifyDataSetChanged();
    }


    private void filter(String text) {

        ArrayList<ExpensiveData> filterList = new ArrayList<>();

        for (ExpensiveData item : myExpensiveList) {

            if (item.getExpensiveName().toLowerCase().contains(text.toLowerCase())) {

                filterList.add(item);

            }

        }

        myExpensiveAdapter.filteredList(filterList);

    }


    @Override
    public void onBackPressed() {
        finish();
        Intent openHomeFromExpensive = new Intent(ExpensiveActivity.this,HomeActivity.class);
        startActivity(openHomeFromExpensive);
        Animatoo.animateSwipeLeft(ExpensiveActivity.this);
    }
}