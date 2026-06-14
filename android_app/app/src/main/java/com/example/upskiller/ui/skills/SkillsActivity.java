package com.example.upskiller.ui.skills;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.Skill;
import com.example.upskiller.model.request.SkillRequest;
import com.example.upskiller.model.response.SkillResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SkillsActivity extends BaseActivity implements SkillsAdapter.Callbacks {

    private SkillsAdapter adapter;
    private final List<Skill> skills = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView rv = findViewById(R.id.rvSkills);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SkillsAdapter(skills, this);
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddSkill);
        fab.setOnClickListener(v -> showSkillDialog(null));

        loadSkills();
    }

    private void loadSkills() {
        showLoading(getString(R.string.loading));
        ApiClient.get(this).getSkills().enqueue(new ApiCallback<List<Skill>>() {
            @Override public void onSuccess(List<Skill> body) {
                hideLoading();
                adapter.setData(body);
            }
            @Override public void onError(String message) {
                hideLoading();
                showError(message);
            }
        });
    }

    private void showSkillDialog(Skill existing) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_skill, null);
        TextInputLayout til     = dialogView.findViewById(R.id.tilSkillName);
        TextInputEditText et    = dialogView.findViewById(R.id.etSkillName);
        ChipGroup chipGroup     = dialogView.findViewById(R.id.chipGroupLevel);

        if (existing != null) {
            et.setText(existing.getSkillName());
            int chipId = "intermediate".equals(existing.getLevel()) ? R.id.chipIntermediate
                       : "advanced".equals(existing.getLevel())     ? R.id.chipAdvanced
                       : R.id.chipBeginner;
            chipGroup.check(chipId);
        }

        new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Add Skill" : "Edit Skill")
                .setView(dialogView)
                .setPositiveButton(R.string.btn_save, (d, w) -> {
                    til.setError(null);
                    String name = et.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) { til.setError("Required"); return; }
                    String level = getLevel(chipGroup);
                    if (existing == null) createSkill(name, level);
                    else updateSkill(existing.getId(), name, level);
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private String getLevel(ChipGroup cg) {
        int id = cg.getCheckedChipId();
        if (id == R.id.chipIntermediate) return "intermediate";
        if (id == R.id.chipAdvanced)     return "advanced";
        return "beginner";
    }

    private void createSkill(String name, String level) {
        showLoading(getString(R.string.loading));
        ApiClient.get(this).createSkill(new SkillRequest(name, level))
                 .enqueue(new ApiCallback<SkillResponse>() {
                     @Override public void onSuccess(SkillResponse body) {
                         hideLoading();
                         loadSkills();
                         showMessage(getString(R.string.msg_skill_added));
                     }
                     @Override public void onError(String message) {
                         hideLoading();
                         showError(message);
                     }
                 });
    }

    private void updateSkill(int id, String name, String level) {
        showLoading(getString(R.string.loading));
        ApiClient.get(this).updateSkill(id, new SkillRequest(name, level))
                 .enqueue(new ApiCallback<SkillResponse>() {
                     @Override public void onSuccess(SkillResponse body) {
                         hideLoading();
                         loadSkills();
                     }
                     @Override public void onError(String message) {
                         hideLoading();
                         showError(message);
                     }
                 });
    }

    @Override
    public void onEdit(Skill skill) { showSkillDialog(skill); }

    @Override
    public void onDelete(Skill skill) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.msg_confirm_delete_skill))
                .setPositiveButton(R.string.btn_yes, (d, w) -> {
                    showLoading(getString(R.string.loading));
                    ApiClient.get(this).deleteSkill(skill.getId())
                             .enqueue(new ApiCallback<Void>() {
                                 @Override public void onSuccess(Void body) {
                                     hideLoading();
                                     loadSkills();
                                     showMessage(getString(R.string.msg_skill_deleted));
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

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
