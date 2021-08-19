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
        mExpensiveData = new ExpensiveData("Custom Rolls Royce Sweeptail", " \n" +
                "You can look, but you can’t touch. This one-of-a-kind car is entirely custom from stem to stern. Modeled after the yachts of the 1920s and 1930s, this car is entirely couture class and timeless beauty to boot. A single pane of glass tapers from front to the sweeping rear of the vehicle giving this particular Rolls-Royce a look that is so singular you cannot mistake it for any other car. Though the company hasn’t disclosed who purchased the car, they have money to burn and excellent taste.\n"
                , "12800000", R.drawable.first_expensive,"0","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Phantom Solid Gold ", "Swiss company Eurocash AG and Stuart Hughes collaborated to create this decadent and surprisingly safety-minded car. The Solid Gold Phantom isn’t just for show. With safety features certified by the German Government Beschussamt München, you can withstand a veritable siege in this car all without breaking a sweat. For the dictator with class, or the besieged and beloved public figure who needs the finest, but still worries about the assassination, this singular vehicle will get you from one place to another in the stylish safety to which you are accustomed."
                , "8200000", R.drawable.second_expensive,"1","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Double Pullman Silver Ghost Limousine ‘The Corgi’ ", "A limousine is just a limousine, but The Corgi is special. This century-plus old limo was designed with driver comfort in mind. When cars were newer, though it’s hard to give credence to the idea now, sometimes wealthy owners preferred to drive themselves. While most modern limo owners wouldn’t dream of driving themselves, the Corgi is richly appointed upfront as well as in the passenger cab."
                , "7300000", R.drawable.third_expensive,"2","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Two-Seater ", "Every Rolls-Royce is special, but this is the king of all Rolls Royces. This unique two-seater is the oldest ‘living’ model in existence. It is the great grandfather of every RR now cruising. When you picture a classic car in your head, you probably see something like this beauty. It was only the fourth car ever to wear the name Rolls-Royce. Hopefully, it’s sitting in a museum somewhere, but we couldn’t find a current location for the Two Seater."
                , "7200000", R.drawable.fourth_expensive,"3","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Hyperion Pininfarina ", "This truly singular, one-of-a-kind commission was so expensive that the original requester couldn’t afford to buy it when it was finished. The uniquely sporty-looking Rolls-Royce came up for sale back in 2009 because the buyer fell through. Their loss was someone’s gain, though we couldn’t ferret out who was fortunate enough to become the owner of this decadent powder blue vehicle."
                , "6000000", R.drawable.fifth_expensive,"4","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Ghost Diva Fenice Milano ", "Featuring golden door handles (literally), and 24k trim, the Fenice Milano is surely the Empress of Ghosts to the stately Silver Dukes. This incredible burgundy and gold masterpiece is enough to make a diva swoon. Far from a ‘gilded cage,’ this golden glory gives a girl the freedom of the road and the wind in her hair, but only if she can pay the price. At a lavish $3 Million, only a handful of the most affluent women will be driving these luxe lovelies."
                , "3000000", R.drawable.sixth_expensive,"5","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("The Robert Hall Phantom II Continental Sports Coupé (Freestone & Webb, 1933)", "This peerless car has such a long and storied history that we can’t possibly fit it all in. To give you just the briefest taste of the allure this specific vehicle holds we’ll relate the story of Anthony Gibbs who purchased the car in 1952. He saw it on the road ahead and fell so deeply in love with the car that he chased the driver to a railroad crossing, terrifying them until he finally convinced them to listen to his offer. The previous owner accepted, making Gibbs one of the long line of owners, and only the first to chase this car until he could buy it."
                , "2400000", R.drawable.seventh_expensive,"6","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Phantom II Special Town Car (1933)", "Timeless and magnificent, this town car is everything that a 1930s era millionaire could dream of. For those seeking a nod to simpler and more majestic automotive times, you’ll have to wait until the current owner sells this extravagant beauty. For now, you’ll sadly have to settle for merely looking at it in pictures. Someone snapped this car up almost a decade ago from Sothebys, and we haven’t seen it since."
                , "2300000", R.drawable.eighth_expensive,"7","0");
        myExpensiveList.add(mExpensiveData);
        mExpensiveData = new ExpensiveData("Rolls Royce Phantom Mansory Conquistador & Year of the Dragon Edition", "Whether you want the aerodynamic package add ons for your Phantom with the (usually) metallic Masonry edition, or you’re seeking something a little more special with the unique Year of the Dragon Edition, the Rolls Royce Phantoms are the best for a good reason. Phantoms are the most sumptuous cars out there in so many ways. Unfortunately, the Year of the Dragon Editions isn’t available, since they sold out within two months of their release in China."
                , "1200000", R.drawable.ninth_expensive,"8","0");
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