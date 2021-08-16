package com.rackluxury.rolex.reddit.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;

import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.activities.RedditPostGalleryActivity;

public class SelectOrCaptureImageBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    private RedditPostGalleryActivity mActivity;

    public SelectOrCaptureImageBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_or_capture_image_bottom_sheet, container, false);

        TextView selectImageTextView = rootView.findViewById(R.id.select_image_text_view_select_or_capture_image_bottom_sheet_fragment);
        TextView captureImageTextView = rootView.findViewById(R.id.capture_image_text_view_select_or_capture_image_bottom_sheet_fragment);

        selectImageTextView.setOnClickListener(view -> {
            mActivity.selectImage();
            dismiss();
        });

        captureImageTextView.setOnClickListener(view -> {
            mActivity.captureImage();
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (RedditPostGalleryActivity) context;
    }
}