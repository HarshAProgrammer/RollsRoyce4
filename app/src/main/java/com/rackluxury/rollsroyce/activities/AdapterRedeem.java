package com.rackluxury.rollsroyce.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.rackluxury.rolex.R;

import java.util.List;

public class AdapterRedeem extends PagerAdapter {

    private final List<Model> models;
    private final Context context;

    public AdapterRedeem(List<Model> models, Context context) {
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
        View view = layoutInflater.inflate(R.layout.item_redeem, container, false);

        ImageView imageView;
        TextView title, price;

        imageView = view.findViewById(R.id.image_item_redeem);
        title = view.findViewById(R.id.title_item_redeem);
        price = view.findViewById(R.id.price_item_redeem);

        imageView.setImageResource(models.get(position).getImage());
        title.setText(models.get(position).getTitle());
        price.setText(models.get(position).getDesc());


        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
