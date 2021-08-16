package com.rackluxury.rolex.reddit.bottomsheetfragments;


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
import com.rackluxury.rolex.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostTypeBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_LINK = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_GALLERY = 4;
    @BindView(R.id.text_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView textTypeTextView;
    @BindView(R.id.link_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView linkTypeTextView;
    @BindView(R.id.image_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView imageTypeTextView;
    @BindView(R.id.video_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView videoTypeTextView;
    @BindView(R.id.gallery_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView galleryTypeTextView;
    private Activity activity;

    public PostTypeBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_type_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        textTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_TEXT);
            dismiss();
        });

        linkTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_LINK);
            dismiss();
        });

        imageTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_IMAGE);
            dismiss();
        });

        videoTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_VIDEO);
            dismiss();
        });

        galleryTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_GALLERY);
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    public interface PostTypeSelectionCallback {
        void postTypeSelected(int postType);
    }

}
