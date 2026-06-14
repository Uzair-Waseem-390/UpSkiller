package com.example.upskiller.ui.skills;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.Skill;
import com.google.android.material.chip.Chip;

import java.util.List;

public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.VH> {

    public interface Callbacks {
        void onEdit(Skill skill);
        void onDelete(Skill skill);
    }

    private final List<Skill> items;
    private final Callbacks callbacks;

    public SkillsAdapter(List<Skill> items, Callbacks callbacks) {
        this.items = items;
        this.callbacks = callbacks;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_skill, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Skill s = items.get(position);
        h.tvName.setText(s.getSkillName());
        h.chipLevel.setText(s.getLevelDisplay() != null ? s.getLevelDisplay() : capitalize(s.getLevel()));
        h.btnEdit.setOnClickListener(v -> callbacks.onEdit(s));
        h.btnDelete.setOnClickListener(v -> callbacks.onDelete(s));
    }

    @Override public int getItemCount() { return items.size(); }

    public void setData(List<Skill> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName;
        Chip chipLevel;
        ImageButton btnEdit, btnDelete;
        VH(View v) {
            super(v);
            tvName    = v.findViewById(R.id.tvSkillName);
            chipLevel = v.findViewById(R.id.chipLevel);
            btnEdit   = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
