package com.rackluxury.rollsroyce.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.rackluxury.rollsroyce.R;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.ramotion.foldingcell.FoldingCell;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class CompanyInfo extends AppCompatActivity {
    SliderLayout sliderLayout;
    private static final String SHOWCASE_ID = "single company";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);
        Toolbar toolbar = findViewById(R.id.toolbarCompanyInfoActivity);
        final FoldingCell fc1 = findViewById(R.id.folding_cell1);
        final FoldingCell fc2 = findViewById(R.id.folding_cell2);
        final FoldingCell fc3 = findViewById(R.id.folding_cell3);
        final FoldingCell fc4 = findViewById(R.id.folding_cell4);
        final FoldingCell fc5 = findViewById(R.id.folding_cell5);
        final FoldingCell fc6 = findViewById(R.id.folding_cell6);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Rolex Inc.");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        sliderLayout = findViewById(R.id.imageSliderCompanyInfo);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP);
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(3);
        setSliderViews();

        new MaterialShowcaseView.Builder(this)
                .setTarget(fc1)
                .setDismissText("GOT IT")
                .setContentText("Tap to get More Information")
                .setContentTextColor(getResources().getColor(R.color.colorWhite))
                .setMaskColour(getResources().getColor(R.color.colorGreen))
                .setDelay(1000)
                .withRectangleShape(true)
                .singleUse(SHOWCASE_ID)
                .show();

        fc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc1.toggle(false);

            }
        });
        fc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc2.toggle(false);
            }
        });
        fc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc3.toggle(false);
            }
        });
        fc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc4.toggle(false);
            }
        });
        fc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc5.toggle(false);
            }
        });
        fc6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc6.toggle(false);
            }
        });
    }

    private void setSliderViews() {
        for (int i = 0; i <= 5; i++) {

            DefaultSliderView sliderView = new DefaultSliderView(CompanyInfo.this);

            switch (i) {
                case 0:
                    sliderView.setDescription("Why are Rolex watches so valuable?");
                    sliderView.setImageDrawable(R.drawable.valuable_company);
                    break;
                case 1:
                    sliderView.setDescription("Leading innovation");
                    sliderView.setImageDrawable(R.drawable.innovation_company);
                    break;
                case 2:
                    sliderView.setDescription("Rare and precious materials");
                    sliderView.setImageDrawable(R.drawable.precious_company);
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.manufacturing_company);
                    sliderView.setDescription("Outstanding manufacturing process");
                    break;
                case 4:
                    sliderView.setImageDrawable(R.drawable.dedication_company);
                    sliderView.setDescription("Dedication, time and attention to details");
                    break;
                case 5:
                    sliderView.setImageDrawable(R.drawable.investment_company);
                    sliderView.setDescription("Are Rolex watches a good investment?");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);


            sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(SliderView sliderView) {

                }
            });


            sliderLayout.addSliderView(sliderView);
        }

    }
    @Override
    public void onBackPressed() {
        finish();
        Animatoo.animateSwipeLeft(CompanyInfo.this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
