package com.rackluxury.rollsroyce.reddit.bottomsheetfragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rollsroyce.reddit.adapters.FlairBottomSheetRecyclerViewAdapter;
import com.rackluxury.rollsroyce.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rollsroyce.reddit.FetchFlairs;
import com.rackluxury.rollsroyce.reddit.Flair;
import com.rackluxury.rollsroyce.reddit.Infinity;
import com.rackluxury.rollsroyce.R;
import com.rackluxury.rollsroyce.reddit.events.FlairSelectedEvent;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class FlairBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String EXTRA_ACCESS_TOKEN = "EAT";
    public static final String EXTRA_SUBREDDIT_NAME = "ESN";
    public static final String EXTRA_VIEW_POST_DETAIL_FRAGMENT_ID = "EPFI";
    @BindView(R.id.progress_bar_flair_bottom_sheet_fragment)
    ProgressBar progressBar;
    @BindView(R.id.error_text_view_flair_bottom_sheet_fragment)
    TextView errorTextView;
    @BindView(R.id.recycler_view_bottom_sheet_fragment)
    RecyclerView recyclerView;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    @Named("no_oauth")
    Retrofit mRetrofit;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    private String mAccessToken;
    private String mSubredditName;
    private Activity mActivity;
    private FlairBottomSheetRecyclerViewAdapter mAdapter;

    public FlairBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_flair_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        ((Infinity) mActivity.getApplication()).getAppComponent().inject(this);

        long viewPostFragmentId = getArguments().getLong(EXTRA_VIEW_POST_DETAIL_FRAGMENT_ID, -1);
        mAdapter = new FlairBottomSheetRecyclerViewAdapter(mActivity, mCustomThemeWrapper, flair -> {
            if (viewPostFragmentId <= 0) {
                //PostXXXActivity
                ((FlairSelectionCallback) mActivity).flairSelected(flair);
            } else {
                EventBus.getDefault().post(new FlairSelectedEvent(viewPostFragmentId, flair));
            }
            dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        mAccessToken = getArguments().getString(EXTRA_ACCESS_TOKEN);
        mSubredditName = getArguments().getString(EXTRA_SUBREDDIT_NAME);

        fetchFlairs();

        return rootView;
    }

    private void fetchFlairs() {
        FetchFlairs.fetchFlairsInSubreddit(mOauthRetrofit, mAccessToken,
                mSubredditName, new FetchFlairs.FetchFlairsInSubredditListener() {
                    @Override
                    public void fetchSuccessful(ArrayList<Flair> flairs) {
                        progressBar.setVisibility(View.GONE);
                        if (flairs == null || flairs.size() == 0) {
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText(R.string.no_flair);
                        } else {
                            errorTextView.setVisibility(View.GONE);
                            mAdapter.changeDataset(flairs);
                        }
                    }

                    @Override
                    public void fetchFailed() {
                        progressBar.setVisibility(View.GONE);
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText(R.string.error_loading_flairs);
                        errorTextView.setOnClickListener(view -> fetchFlairs());
                    }
                });
    }

    public interface FlairSelectionCallback {
        void flairSelected(Flair flair);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}
