package com.rackluxury.rollsroyce.reddit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.rackluxury.rollsroyce.R;

public class CrashReportsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<String> crashReports;

    public CrashReportsRecyclerViewAdapter(List<String> crashReports) {
        this.crashReports = crashReports;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CrashReportViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crash_report, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CrashReportViewHolder) holder).crashReportTextView.setText(crashReports.get(position));
    }

    @Override
    public int getItemCount() {
        return crashReports == null ? 0 : crashReports.size();
    }

    private class CrashReportViewHolder extends RecyclerView.ViewHolder {
        TextView crashReportTextView;
        public CrashReportViewHolder(@NonNull View itemView) {
            super(itemView);
            crashReportTextView = (TextView) itemView;
        }
    }
}
