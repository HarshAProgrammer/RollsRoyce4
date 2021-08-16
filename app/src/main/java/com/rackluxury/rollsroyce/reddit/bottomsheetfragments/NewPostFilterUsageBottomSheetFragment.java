package com.rackluxury.rollsroyce.reddit.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.activities.PostFilterUsageListingActivity;
import com.rackluxury.rollsroyce.reddit.postfilter.PostFilterUsage;

public class NewPostFilterUsageBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    @BindView(R.id.home_text_view_new_post_filter_usage_bottom_sheet_fragment)
    TextView homeTextView;
    @BindView(R.id.subreddit_text_view_new_post_filter_usage_bottom_sheet_fragment)
    TextView subredditTextView;
    @BindView(R.id.user_text_view_new_post_filter_usage_bottom_sheet_fragment)
    TextView userTextView;
    @BindView(R.id.multireddit_text_view_new_post_filter_usage_bottom_sheet_fragment)
    TextView multiRedditTextView;
    @BindView(R.id.search_text_view_new_post_filter_usage_bottom_sheet_fragment)
    TextView searchTextView;
    private PostFilterUsageListingActivity activity;

    public NewPostFilterUsageBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_post_filter_usage_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        homeTextView.setOnClickListener(view -> {
            activity.newPostFilterUsage(PostFilterUsage.HOME_TYPE);
            dismiss();
        });

        subredditTextView.setOnClickListener(view -> {
            activity.newPostFilterUsage(PostFilterUsage.SUBREDDIT_TYPE);
            dismiss();
        });

        userTextView.setOnClickListener(view -> {
            activity.newPostFilterUsage(PostFilterUsage.USER_TYPE);
            dismiss();
        });

        multiRedditTextView.setOnClickListener(view -> {
            activity.newPostFilterUsage(PostFilterUsage.MULTIREDDIT_TYPE);
            dismiss();
        });

        searchTextView.setOnClickListener(view -> {
            activity.newPostFilterUsage(PostFilterUsage.SEARCH_TYPE);
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (PostFilterUsageListingActivity) context;
    }
}