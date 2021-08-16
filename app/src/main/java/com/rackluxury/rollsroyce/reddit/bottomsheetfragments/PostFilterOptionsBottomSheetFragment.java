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
import com.rackluxury.rollsroyce.reddit.activities.RedditPostFilterPreferenceActivity;
import com.rackluxury.rollsroyce.reddit.postfilter.PostFilter;

public class PostFilterOptionsBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    @BindView(R.id.edit_text_view_post_filter_options_bottom_sheet_fragment)
    TextView editTextView;
    @BindView(R.id.apply_to_text_view_post_filter_options_bottom_sheet_fragment)
    TextView applyToTextView;
    @BindView(R.id.delete_text_view_post_filter_options_bottom_sheet_fragment)
    TextView deleteTextView;
    public static final String EXTRA_POST_FILTER = "EPF";
    private RedditPostFilterPreferenceActivity activity;

    public PostFilterOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_filter_options_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        PostFilter postFilter = getArguments().getParcelable(EXTRA_POST_FILTER);

        editTextView.setOnClickListener(view -> {
            activity.editPostFilter(postFilter);
            dismiss();
        });

        applyToTextView.setOnClickListener(view -> {
            activity.applyPostFilterTo(postFilter);
            dismiss();
        });

        deleteTextView.setOnClickListener(view -> {
            activity.deletePostFilter(postFilter);
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (RedditPostFilterPreferenceActivity) context;
    }
}