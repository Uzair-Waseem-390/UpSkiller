package com.example.upskiller.ui.roadmap;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.Roadmap;
import com.example.upskiller.model.Topic;
import com.example.upskiller.model.request.TopicCompleteRequest;
import com.example.upskiller.model.response.TopicCompleteResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class RoadmapDetailActivity extends BaseActivity {

    public static final String EXTRA_ROADMAP_ID = "roadmap_id";

    private int roadmapId;
    private Roadmap currentRoadmap;
    private PhaseAdapter phaseAdapter;

    // Header views
    private TextView tvTitle, tvProgress;
    private Chip chipSkill, chipLevel;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmap_detail);

        roadmapId = getIntent().getIntExtra(EXTRA_ROADMAP_ID, -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle     = findViewById(R.id.tvTitle);
        tvProgress  = findViewById(R.id.tvProgress);
        chipSkill   = findViewById(R.id.chipSkill);
        chipLevel   = findViewById(R.id.chipLevel);
        progressBar = findViewById(R.id.progressBar);

        RecyclerView rvPhases = findViewById(R.id.rvPhases);
        rvPhases.setLayoutManager(new LinearLayoutManager(this));

        loadRoadmap();
    }

    private void loadRoadmap() {
        showLoading(getString(R.string.loading));
        ApiClient.get(this).getRoadmapDetail(roadmapId)
                 .enqueue(new ApiCallback<Roadmap>() {
                     @Override public void onSuccess(Roadmap body) {
                         hideLoading();
                         currentRoadmap = body;
                         bindHeader(body);
                         bindPhases(body);
                     }
                     @Override public void onError(String message) {
                         hideLoading();
                         showError(message);
                     }
                 });
    }

    private void bindHeader(Roadmap r) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(r.getTitle());
        tvTitle.setText(r.getTitle());
        chipSkill.setText(r.getTargetSkill());
        chipLevel.setText(capitalize(r.getTargetLevel()));
        progressBar.setProgress(r.getProgressPercentage());
        tvProgress.setText(getString(R.string.label_progress, r.getProgressPercentage()));
    }

    private void bindPhases(Roadmap r) {
        RecyclerView rvPhases = findViewById(R.id.rvPhases);
        if (r.getPhases() == null) return;

        if (phaseAdapter == null) {
            phaseAdapter = new PhaseAdapter(r.getPhases(), this::toggleTopic);
            rvPhases.setAdapter(phaseAdapter);
        } else {
            phaseAdapter.updatePhaseProgress(r.getPhases());
        }
    }

    private void toggleTopic(Topic topic, boolean isChecked) {
        ApiClient.get(this)
                 .markTopicComplete(topic.getId(), new TopicCompleteRequest(isChecked))
                 .enqueue(new ApiCallback<TopicCompleteResponse>() {
                     @Override public void onSuccess(TopicCompleteResponse body) {
                         currentRoadmap = body.getRoadmap();
                         bindHeader(currentRoadmap);
                         if (currentRoadmap.getPhases() != null) {
                             phaseAdapter.updatePhaseProgress(currentRoadmap.getPhases());
                         }
                     }
                     @Override public void onError(String message) {
                         showError(message);
                         // Revert optimistic update
                         topic.setCompleted(!isChecked);
                         phaseAdapter.notifyDataSetChanged();
                     }
                 });
    }

    private void deleteRoadmap() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Roadmap")
                .setMessage(getString(R.string.msg_confirm_delete_roadmap))
                .setPositiveButton(R.string.btn_yes, (d, w) -> {
                    showLoading(getString(R.string.loading));
                    ApiClient.get(this).deleteRoadmap(roadmapId)
                             .enqueue(new ApiCallback<Void>() {
                                 @Override public void onSuccess(Void body) {
                                     hideLoading();
                                     showMessage(getString(R.string.msg_roadmap_deleted));
                                     finish();
                                 }
                                 @Override public void onError(String message) {
                                     hideLoading();
                                     showError(message);
                                 }
                             });
                })
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Delete").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        if (item.getItemId() == 1) { deleteRoadmap(); return true; }
        return super.onOptionsItemSelected(item);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
