package com.example.upskiller.ui.roadmap;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.Topic;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.VH> {

    public interface OnToggleListener { void onToggle(Topic topic, boolean isChecked); }

    private final List<Topic> topics;
    private final OnToggleListener listener;

    public TopicAdapter(List<Topic> topics, OnToggleListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_topic, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Topic t = topics.get(position);

        // Temporarily block listener to avoid triggering during bind
        h.cbTopic.setOnCheckedChangeListener(null);
        h.cbTopic.setChecked(t.isCompleted());

        // Strike-through when completed
        h.tvTitle.setPaintFlags(t.isCompleted()
                ? h.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : h.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        h.tvTitle.setText(t.getTitle());
        h.tvTitle.setAlpha(t.isCompleted() ? 0.5f : 1.0f);

        h.cbTopic.setOnCheckedChangeListener((btn, checked) -> {
            t.setCompleted(checked); // optimistic update
            notifyItemChanged(position);
            listener.onToggle(t, checked);
        });
    }

    @Override public int getItemCount() { return topics.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CheckBox cbTopic;
        TextView tvTitle;
        VH(View v) {
            super(v);
            cbTopic = v.findViewById(R.id.cbTopic);
            tvTitle = v.findViewById(R.id.tvTopicTitle);
        }
    }
}
