package com.rackluxury.rolex.reddit.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rolex.reddit.adapters.TranslationFragmentRecyclerViewAdapter;
import com.rackluxury.rolex.reddit.customtheme.CustomThemeWrapper;
import com.rackluxury.rolex.reddit.Infinity;
import com.rackluxury.rolex.R;

public class TranslationFragment extends Fragment {

    @BindView(R.id.recycler_view_translation_fragment)
    RecyclerView recyclerView;
    @Inject
    CustomThemeWrapper customThemeWrapper;
    private Activity activity;

    public TranslationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_translation, container, false);

        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        ButterKnife.bind(this, rootView);

        TranslationFragmentRecyclerViewAdapter adapter = new TranslationFragmentRecyclerViewAdapter(activity, customThemeWrapper);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }
}