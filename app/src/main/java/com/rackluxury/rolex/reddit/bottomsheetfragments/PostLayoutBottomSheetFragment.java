package com.rackluxury.rolex.reddit.bottomsheetfragments;


import android.app.Activity;
import android.content.Context;
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
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostLayoutBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    @BindView(R.id.card_layout_text_view_post_layout_bottom_sheet_fragment)
    TextView cardLayoutTextView;
    @BindView(R.id.card_layout_2_text_view_post_layout_bottom_sheet_fragment)
    TextView cardLayout2TextView;
    @BindView(R.id.compact_layout_text_view_post_layout_bottom_sheet_fragment)
    TextView compactLayoutTextView;
    @BindView(R.id.gallery_layout_text_view_post_layout_bottom_sheet_fragment)
    TextView galleryLayoutTextView;
    private Activity activity;
    public PostLayoutBottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_layot_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        cardLayoutTextView.setOnClickListener(view -> {
            ((PostLayoutSelectionCallback) activity).postLayoutSelected(SharedPreferencesUtils.POST_LAYOUT_CARD);
            dismiss();
        });
        compactLayoutTextView.setOnClickListener(view -> {
            ((PostLayoutSelectionCallback) activity).postLayoutSelected(SharedPreferencesUtils.POST_LAYOUT_COMPACT);
            dismiss();
        });
        galleryLayoutTextView.setOnClickListener(view -> {
            ((PostLayoutSelectionCallback) activity).postLayoutSelected(SharedPreferencesUtils.POST_LAYOUT_GALLERY);
            dismiss();
        });
        cardLayout2TextView.setOnClickListener(view -> {
            ((PostLayoutSelectionCallback) activity).postLayoutSelected(SharedPreferencesUtils.POST_LAYOUT_CARD_2);
            dismiss();
        });
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    public interface PostLayoutSelectionCallback {
        void postLayoutSelected(int postLayout);
    }

}
