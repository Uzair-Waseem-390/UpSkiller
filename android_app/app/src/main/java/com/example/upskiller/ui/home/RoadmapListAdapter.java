package com.example.upskiller.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.Roadmap;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class RoadmapListAdapter extends RecyclerView.Adapter<RoadmapListAdapter.VH> {

    public interface OnItemClickListener { void onClick(Roadmap roadmap); }

    private final List<Roadmap> items;
    private final OnItemClickListener listener;

    public RoadmapListAdapter(List<Roadmap> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_roadmap, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Roadmap r = items.get(position);
        h.tvTitle.setText(r.getTitle());
        h.chipSkill.setText(r.getTargetSkill());
        h.chipLevel.setText(capitalize(r.getTargetLevel()));
        h.progressBar.setProgress(r.getProgressPercentage());
        h.tvProgress.setText(h.itemView.getContext()
                .getString(R.string.label_progress, r.getProgressPercentage()));
        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override public int getItemCount() { return items.size(); }

    public void updateData(List<Roadmap> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvProgress;
        Chip chipSkill, chipLevel;
        LinearProgressIndicator progressBar;

        VH(View v) {
            super(v);
            tvTitle     = v.findViewById(R.id.tvTitle);
            tvProgress  = v.findViewById(R.id.tvProgress);
            chipSkill   = v.findViewById(R.id.chipSkill);
            chipLevel   = v.findViewById(R.id.chipLevel);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
