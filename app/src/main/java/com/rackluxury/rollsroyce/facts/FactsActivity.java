package com.rackluxury.rollsroyce.facts;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.activities.Model;

import java.util.ArrayList;
import java.util.List;

public class FactsActivity extends AppCompatActivity {

    private ConstraintLayout FactsLayout;
    ViewPager viewPagerFacts;
    AdapterFacts adapterFacts;
    List<Model> models;


    final ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    Integer[] colors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facts);

        Toolbar toolbar = findViewById(R.id.toolbarFactsActivity);
        FactsLayout = findViewById(R.id.factsActivity);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Facts About Us");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        factsMain();

    }

    private void factsMain() {
        models = new ArrayList<>();
        models.add(new Model(R.drawable.first_facts, "65% of all the Rolls Royce cars ever built are still on the road", "What would you term as optimal utilization of resources? The most mind-boggling fact is that 65% of all Rolls Royce cars ever built are still on the road today – customized, bejeweled, or pimped up! Many fans seem to attribute this to the powerful engine and the meticulous craftsmanship keeps the car going on and on.\n" +
                "11. Most Rolls Royce products are manufactured at its factory in Indianapolis, USA\n" +
                "The Rolls-Royce has gradually or sporadically shifted its base from London right down to Indianapolis, Indiana. Today, the leading industry supplier in the US builds and supplies more Rolls Royce products than anywhere in the world! A 4000-strong workforce toil towards manufacturing, assembly, test, engineering, and other staff support roles. Changing times, changing needs!\n"));
        models.add(new Model(R.drawable.second_facts, "The grille was etched with the initials of the craftsman who made the car", "Another fact to wonder about is that the Rolls Royce grille of the early cars was built completely by hand and lined up by eye by a craftsman who would inscribe his initials on it. This fact has less to do with consistency and more to do with damage handling. If the grille went bad, the initials would help them track the man down in England who made it and ship it for repairs."));
        models.add(new Model(R.drawable.third_facts, "An outraged Indian king used Rolls Royce cars as a garbage collector", "The majestic vehicle has also suffered the fate of a garbage collector! When Jai Singh, a Maharaja of a princely state of India was faced by an uncourteous salesman at the Rolls Royce showroom in London who suggested that he could not afford to purchase the car, he purchased ten, shipped them to India, and ordered to use them for collecting and transporting garbage! Many would refute that he still made the company some big bucks, but back in the day, the presumption that honor was more important than money, could have acted as a hit below the belt. Other royalty who treated the car like trash or for trash, you ask? It would be the Nizam of Hyderabad and Maharaja Bhupinder Singh, both with their outrage at the insults from the British."));
        models.add(new Model(R.drawable.fourth_facts, "Rolls Royce only uses bull skin for its upholstery", "Connoisseurs of pristine upholstery would rave about the fact that Rolls Royce only uses bull hides for upholstery since female cattle are prone to getting stretch marks during pregnancy. That’s just a glimpse of the impeccable detailing. Also, it takes 8 bulls to upholster a single Rolls Royce, that’s quite a number! Moreover, the bulls whose skin they use for the upholstery are raised in the region of Europe where the climate is too cold for mosquitoes to live – so you get blemish-free and cottony-soft seats to lay on!"));
        models.add(new Model(R.drawable.fifth_facts, "Rolls Royce has a special training program for chauffeurs", "A premium car calls for a premium driving experience. The folks at Rolls Royce empathize with this fact and they’ve come up with apt training to hone the skills of the chauffeurs for the premium cars – The Rolls Royce White Glove experience. Some of the etiquettes taught in the program include never greeting a guest with sunglasses on and never propping up the sunglasses on the head or the back of the neck when the guest is around. This and many more such etiquettes that chauffeurs tend to neglect. Keep the manners intact and be worthy of a handsome tip!"));
        models.add(new Model(R.drawable.sixth_facts, "There are more Rolls Royce’s in Hong Kong than anywhere else in the world", "Hong Kong has the most number of Rolls Royce cars per capita in the world is shaped by a factor of history. It was a British colony in the early decades and most of the British officers as well as businessmen had a Rolls Royce of their own because it was considered the ultimate status symbol. Colonization definitely had their merits for Rolls Royce, so to speak."));
        models.add(new Model(R.drawable.seventh_facts, "Only one man is entitled to paint the coach line of the Rolls Royce", "We’re not alien to the fact that Rolls Royce cars display a penchant for detailing in every nook and corner. You can faithfully announce it as the epitome of meticulousness and exclusivity when you know that the coach line is painted freehand by one man only – Mark Court. No robots or machines used, mind you!"));
        models.add(new Model(R.drawable.eighth_facts, "The iconic Spirit of Ecstasy on the hood cannot be flicked", "The front of the Rolls Royce starring the Spirit of Ecstasy – a hood unique to a Rolls Royce was prone to being flicked off its front in the early days. With the new variants, this booty hack is not quite a possibility – Thanks to the safety feature that allows the mascot to retract swiftly into the body of the car when inflicted with force. In case you wondered about the etymology of the term, it symbolizes the forbidden love affair of John Walter, editor for The Car Illustrated magazine, and his secretary, Eleanor Velasco Thorton. Sultry and saucy!"));
        models.add(new Model(R.drawable.ninth_facts, "Rolls Royce center caps do not rotate", "Call it one of the anomalies or idiosyncrasies, the Rolls Royce center caps do not rotate. They’re on bearings and always stay put so they can always be seen. I don’t know about you, but I’d call it pure style."));
        models.add(new Model(R.drawable.tenth_facts, "Rolls Royce was only selling chassis and motor and not the whole body until 1946", "We see Rolls-Royce as a homogenous brand, but did you know that up until 1946, they only sold the chassis and motor with a recommendation to use Barker & Co Ltd to build a customized body and coach? They weren’t really selling cars with complete bodywork. As for Barker & Co, they were a big shot coachbuilding company footed in London. One of their designs – The Silver Ghost is now the most valuable car in the world, weighing in at US$57 million."));
        models.add(new Model(R.drawable.eleventh_facts, "Most Rolls Royce products are manufactured at its factory in Indianapolis, USA", "The Rolls-Royce has gradually or sporadically shifted its base from London right down to Indianapolis, Indiana. Today, the leading industry supplier in the US builds and supplies more Rolls Royce products than anywhere in the world! A 4000-strong workforce toil towards manufacturing, assembly, test, engineering, and other staff support roles. Changing times, changing needs!"));
        adapterFacts = new AdapterFacts(models, this);

        viewPagerFacts = findViewById(R.id.viewPagerFacts);
        viewPagerFacts.setAdapter(adapterFacts);
        viewPagerFacts.setPadding(130, 0, 130, 0);

        colors = new Integer[]{
                getResources().getColor(R.color.colorFacts1),
                getResources().getColor(R.color.colorFacts2),
                getResources().getColor(R.color.colorFacts3),
                getResources().getColor(R.color.colorFacts4),
                getResources().getColor(R.color.colorFacts5),
                getResources().getColor(R.color.colorFacts6),
                getResources().getColor(R.color.colorFacts7),
                getResources().getColor(R.color.colorFacts8),
                getResources().getColor(R.color.colorFacts9),
                getResources().getColor(R.color.colorFacts10),
                getResources().getColor(R.color.colorFacts11)
        };

        viewPagerFacts.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (adapterFacts.getCount() - 1) && position < (colors.length - 1)) {
                    FactsLayout.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    FactsLayout.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSwipeLeft(FactsActivity.this);

    }
}