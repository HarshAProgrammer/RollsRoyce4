package com.rackluxury.rollsroyce.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rackluxury.rollsroyce.R;
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


        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Rolls Royce Inc.");
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

    }

    private void setSliderViews() {
        for (int i = 0; i <= 4; i++) {

            DefaultSliderView sliderView = new DefaultSliderView(CompanyInfo.this);

            switch (i) {
                case 0:
                    sliderView.setDescription("Classiest design and automotive mascot");
                    sliderView.setImageDrawable(R.drawable.first_company);
                    break;
                case 1:
                    sliderView.setDescription("Premium high-quality materials");
                    sliderView.setImageDrawable(R.drawable.second_company);
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.third_company);
                    sliderView.setDescription("Starlight roof for a ride like no other");
                    break;
                case 3:
                    sliderView.setImageDrawable(R.drawable.fourth_company);
                    sliderView.setDescription("Meter to show power being utilized");
                    break;
                case 4:
                    sliderView.setImageDrawable(R.drawable.fifth_company);
                    sliderView.setDescription("Minimalistic dashboard controls");
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
