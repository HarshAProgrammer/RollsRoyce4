package com.rackluxury.rolex.reddit.bottomsheetfragments;

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
import com.rackluxury.rolex.R;

public class FilteredThingFABMoreOptionsBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    public static final int FAB_OPTION_FILTER = 0;
    public static final int FAB_OPTION_HIDE_READ_POSTS = 1;

    @BindView(R.id.filter_text_view_filtered_thing_fab_more_options_bottom_sheet_fragment)
    TextView filterTextView;
    @BindView(R.id.hide_read_posts_text_view_filtered_thing_fab_more_options_bottom_sheet_fragment)
    TextView hideReadPostsTextView;
    private FABOptionSelectionCallback activity;

    public FilteredThingFABMoreOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_filtered_thing_fab_more_options_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        filterTextView.setOnClickListener(view -> {
            activity.fabOptionSelected(FAB_OPTION_FILTER);
            dismiss();
        });

        hideReadPostsTextView.setOnClickListener(view -> {
            activity.fabOptionSelected(FAB_OPTION_HIDE_READ_POSTS);
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (FABOptionSelectionCallback) context;
    }

    public interface FABOptionSelectionCallback {
        void fabOptionSelected(int option);
    }
}