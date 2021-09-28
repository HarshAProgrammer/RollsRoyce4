package com.rackluxury.rollsroyce.facts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.rackluxury.rollsroyce.R;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rackluxury.rollsroyce.activities.Model;

import java.util.List;

public class AdapterFacts extends PagerAdapter {

    private final List<Model> models;
    private final Context context;

    public AdapterFacts(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_facts, container, false);

        ImageView imageView;
        TextView title, desc;

        imageView = view.findViewById(R.id.image_item_fact);
        title = view.findViewById(R.id.title_item_fact);
        desc = view.findViewById(R.id.desc_item_fact);

        imageView.setImageResource(models.get(position).getImage());
        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getDesc());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FactsDetailActivity.class);

                intent.putExtra("description", models.get(position).getDesc());
                intent.putExtra("title", models.get(position).getTitle());
                intent.putExtra("image", models.get(position).getImage());
                context.startActivity(intent);
                Animatoo.animateSlideUp(context);

            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
