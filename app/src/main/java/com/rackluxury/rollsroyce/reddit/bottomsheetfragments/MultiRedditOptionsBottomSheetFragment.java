package com.rackluxury.rollsroyce.reddit.bottomsheetfragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.activities.RedditEditMultiRedditActivity;
import com.rackluxury.rollsroyce.reddit.activities.RedditSubscribedThingListingActivity;
import com.rackluxury.rollsroyce.reddit.multireddit.MultiReddit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiRedditOptionsBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    public static final String EXTRA_MULTI_REDDIT = "EMR";

    @BindView(R.id.copy_multi_reddit_path_text_view_multi_reddit_options_bottom_sheet_fragment)
    TextView copyMultiredditPathTextView;
    @BindView(R.id.edit_multi_reddit_text_view_multi_reddit_options_bottom_sheet_fragment)
    TextView editMultiRedditTextView;
    @BindView(R.id.delete_multi_reddit_text_view_multi_reddit_options_bottom_sheet_fragment)
    TextView deleteMultiRedditTextView;
    private RedditSubscribedThingListingActivity redditSubscribedThingListingActivity;

    public MultiRedditOptionsBottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multi_reddit_options_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        MultiReddit multiReddit = getArguments().getParcelable(EXTRA_MULTI_REDDIT);

        copyMultiredditPathTextView.setOnClickListener(view -> {
            if (multiReddit != null) {
                ClipboardManager clipboard = (ClipboardManager) redditSubscribedThingListingActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("simple text", multiReddit.getPath());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(redditSubscribedThingListingActivity, multiReddit.getPath(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(redditSubscribedThingListingActivity, R.string.copy_multi_reddit_path_failed, Toast.LENGTH_SHORT).show();
                }
            }
            dismiss();
        });

        editMultiRedditTextView.setOnClickListener(view -> {
            if (multiReddit != null) {
                Intent editIntent = new Intent(redditSubscribedThingListingActivity, RedditEditMultiRedditActivity.class);
                editIntent.putExtra(RedditEditMultiRedditActivity.EXTRA_MULTI_PATH, multiReddit.getPath());
                startActivity(editIntent);
            }
            dismiss();
        });

        deleteMultiRedditTextView.setOnClickListener(view -> {
            redditSubscribedThingListingActivity.deleteMultiReddit(multiReddit);
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        redditSubscribedThingListingActivity = (RedditSubscribedThingListingActivity) context;
    }
}
