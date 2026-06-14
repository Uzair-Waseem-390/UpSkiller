package com.example.upskiller.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.request.SkillRequest;
import com.google.android.material.chip.Chip;

import java.util.List;

public class OnboardingSkillAdapter extends RecyclerView.Adapter<OnboardingSkillAdapter.VH> {

    private final List<SkillRequest> items;
    private final OnRemoveListener listener;

    public interface OnRemoveListener { void onRemove(int position); }

    public OnboardingSkillAdapter(List<SkillRequest> items, OnRemoveListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_skill_onboarding, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SkillRequest s = items.get(position);
        h.tvName.setText(s.getSkillName());
        h.chipLevel.setText(capitalize(s.getLevel()));
        h.btnRemove.setOnClickListener(v -> listener.onRemove(h.getAdapterPosition()));
    }

    @Override public int getItemCount() { return items.size(); }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName;
        Chip chipLevel;
        ImageButton btnRemove;
        VH(View v) {
            super(v);
            tvName    = v.findViewById(R.id.tvSkillName);
            chipLevel = v.findViewById(R.id.chipLevel);
            btnRemove = v.findViewById(R.id.btnRemove);
        }
    }
}
