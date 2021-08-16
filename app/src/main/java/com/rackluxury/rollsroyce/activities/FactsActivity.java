package com.rackluxury.rollsroyce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.os.Bundle;
import android.view.MenuItem;

import com.rackluxury.rollsroyce.R;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;

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
        models.add(new Model(R.drawable.most_expensive_facts, "The most expensive Rolex sold for $17.75 million.", "Known for its Hollywood pedigree, the 1968 Reference 6239 Daytona, made from stainless steel and leather, is one of the most iconic Rolex models. It sold for nearly $18 Million in October 2017 at Phillips’ inaugural watch auction in New York City."));
        models.add(new Model(R.drawable.gold_facts, "An in-house foundry makes all the gold for Rolex watches. ", "Rolex makes their gold. Because they control the categoriesion and machining of their gold, they can strictly ensure not only quality, but look of these extraordinary materials. Rolex is the only watchmaker that makes their gold or even has a real foundry in-house."));
        models.add(new Model(R.drawable.security_facts, "Rolex Headquarters outshines any high-level security prison.", "Rolex holds bars of Everose gold worth $1,000,000, so you can’t blame us for having employee fingerprint scanners, bank vault doors, iris scanners, and unmarked armoured trucks to move Rolex parts from location to location. "));
        models.add(new Model(R.drawable.steel_facts, "Rolex uses the most expensive stainless steel in the world, also known as 904L. ", "Other high-end brands use a stainless-steel grade (known as 316L) in their designs, but 904L is exclusive to Rolex. The steel is much more expensive, and said to be resistant to rust, corrosion, and pitting — a real problem for saltwater divers."));
        adapterFacts = new AdapterFacts(models, this);

        viewPagerFacts = findViewById(R.id.viewPagerFacts);
        viewPagerFacts.setAdapter(adapterFacts);
        viewPagerFacts.setPadding(130, 0, 130, 0);

        colors = new Integer[]{
                getResources().getColor(R.color.colorWhite),
                getResources().getColor(R.color.colorFacts2),
                getResources().getColor(R.color.colorFacts3),
                getResources().getColor(R.color.colorWhite)
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