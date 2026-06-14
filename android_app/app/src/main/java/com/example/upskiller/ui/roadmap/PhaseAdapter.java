package com.example.upskiller.ui.roadmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.Phase;
import com.example.upskiller.model.Topic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhaseAdapter extends RecyclerView.Adapter<PhaseAdapter.VH> {

    private final List<Phase> phases;
    private final TopicAdapter.OnToggleListener topicToggleListener;
    private final Set<Integer> expandedPositions = new HashSet<>();

    public PhaseAdapter(List<Phase> phases, TopicAdapter.OnToggleListener topicToggleListener) {
        this.phases = phases;
        this.topicToggleListener = topicToggleListener;
        // Expand the first phase by default
        if (!phases.isEmpty()) expandedPositions.add(0);
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_phase, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Phase phase = phases.get(position);
        boolean expanded = expandedPositions.contains(position);

        h.tvPhaseName.setText(phase.getName());
        h.tvPhasePercent.setText(phase.getCompletionPercentage() + "%");
        h.rvTopics.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.ivArrow.setRotation(expanded ? 180f : 0f);

        if (expanded && phase.getTopics() != null) {
            h.rvTopics.setLayoutManager(new LinearLayoutManager(h.rvTopics.getContext()));
            h.rvTopics.setAdapter(new TopicAdapter(phase.getTopics(), topicToggleListener));
        }

        h.phaseHeader.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (expandedPositions.contains(pos)) expandedPositions.remove(pos);
            else expandedPositions.add(pos);
            notifyItemChanged(pos);
        });
    }

    @Override public int getItemCount() { return phases.size(); }

    public void updatePhaseProgress(List<Phase> updatedPhases) {
        phases.clear();
        phases.addAll(updatedPhases);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        View phaseHeader;
        TextView tvPhaseName, tvPhasePercent;
        ImageView ivArrow;
        RecyclerView rvTopics;

        VH(View v) {
            super(v);
            phaseHeader    = v.findViewById(R.id.phaseHeader);
            tvPhaseName    = v.findViewById(R.id.tvPhaseName);
            tvPhasePercent = v.findViewById(R.id.tvPhasePercent);
            ivArrow        = v.findViewById(R.id.ivArrow);
            rvTopics       = v.findViewById(R.id.rvTopics);
        }
    }
}
