package com.rackluxury.rollsroyce.reddit.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.reddit.activities.RedditLinkResolverActivity;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.settings.Translation;

public class TranslationFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity activity;
    private final int primaryTextColor;
    private final int secondaryTextColor;
    private final ArrayList<Translation> translationContributors;

    public TranslationFragmentRecyclerViewAdapter(Activity activity, CustomThemeWrapper customThemeWrapper) {
        this.activity = activity;
        primaryTextColor = customThemeWrapper.getPrimaryTextColor();
        secondaryTextColor = customThemeWrapper.getSecondaryTextColor();
        translationContributors = Translation.getTranslationContributors();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TranslationContributorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_translation_contributor, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TranslationContributorViewHolder) {
            Translation translation = translationContributors.get(position);
            if (translation.flagDrawableId < 0) {
                ((TranslationContributorViewHolder) holder).countryFlagImageView.setImageDrawable(null);
            } else {
                ((TranslationContributorViewHolder) holder).countryFlagImageView.setImageResource(translation.flagDrawableId);
            }
            ((TranslationContributorViewHolder) holder).languageNameTextView.setText(translation.language);
            ((TranslationContributorViewHolder) holder).contributorNamesTextView.setText(translation.contributors);
        }
    }

    @Override
    public int getItemCount() {
        return translationContributors.size();
    }

    class TranslationContributorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.country_flag_image_view_item_translation_contributor)
        ImageView countryFlagImageView;
        @BindView(R.id.language_name_text_view_item_translation_contributor)
        TextView languageNameTextView;
        @BindView(R.id.contributor_names_text_view_item_translation_contributor)
        TextView contributorNamesTextView;

        public TranslationContributorViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            languageNameTextView.setTextColor(primaryTextColor);
            contributorNamesTextView.setTextColor(secondaryTextColor);

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(activity, RedditLinkResolverActivity.class);
                intent.setData(Uri.parse("https://poeditor.com/join/project?hash=b2IRyfaJv6"));
                activity.startActivity(intent);
            });
        }
    }
}
