package com.rackluxury.rollsroyce.reddit.bottomsheetfragments;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.SortType;
import com.rackluxury.rollsroyce.reddit.SortTypeSelectionCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPostSortTypeBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    @BindView(R.id.relevance_type_text_view_search_sort_type_bottom_sheet_fragment)
    TextView relevanceTypeTextView;
    @BindView(R.id.hot_type_text_view_search_sort_type_bottom_sheet_fragment)
    TextView hotTypeTextView;
    @BindView(R.id.top_type_text_view_search_sort_type_bottom_sheet_fragment)
    TextView topTypeTextView;
    @BindView(R.id.new_type_text_view_search_sort_type_bottom_sheet_fragment)
    TextView newTypeTextView;
    @BindView(R.id.comments_type_text_view_search_sort_type_bottom_sheet_fragment)
    TextView commentsTypeTextView;
    private Activity activity;
    public SearchPostSortTypeBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_post_sort_type_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        relevanceTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(SortType.Type.RELEVANCE.name());
            dismiss();
        });

        hotTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(SortType.Type.HOT.name());
            dismiss();
        });

        topTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(SortType.Type.TOP.name());
            dismiss();
        });

        newTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(new SortType(SortType.Type.NEW));
            dismiss();
        });

        commentsTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(SortType.Type.COMMENTS.name());
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}
