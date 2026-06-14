package com.example.upskiller.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.upskiller.R;
import com.example.upskiller.model.request.BulkSkillRequest;
import com.example.upskiller.model.request.SkillRequest;
import com.example.upskiller.model.response.SkillResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.ui.main.MainActivity;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SkillSetupActivity extends BaseActivity {

    private TextInputLayout tilSkillName;
    private TextInputEditText etSkillName;
    private ChipGroup chipGroupLevel;
    private final List<SkillRequest> pendingSkills = new ArrayList<>();
    private OnboardingSkillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_setup);

        tilSkillName  = findViewById(R.id.tilSkillName);
        etSkillName   = findViewById(R.id.etSkillName);
        chipGroupLevel = findViewById(R.id.chipGroupLevel);

        RecyclerView rv = findViewById(R.id.rvSkills);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OnboardingSkillAdapter(pendingSkills, pos -> {
            pendingSkills.remove(pos);
            adapter.notifyItemRemoved(pos);
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btnAddSkill).setOnClickListener(v -> addSkillToList());
        findViewById(R.id.btnGetStarted).setOnClickListener(v -> saveAndContinue());
        findViewById(R.id.btnSkip).setOnClickListener(v -> goToMain());
    }

    private void addSkillToList() {
        tilSkillName.setError(null);
        String name = etSkillName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            tilSkillName.setError("Enter a skill name");
            return;
        }
        String level = getSelectedLevel();
        pendingSkills.add(new SkillRequest(name, level));
        adapter.notifyItemInserted(pendingSkills.size() - 1);
        etSkillName.setText("");
    }

    private String getSelectedLevel() {
        int checkedId = chipGroupLevel.getCheckedChipId();
        if (checkedId == R.id.chipIntermediate) return "intermediate";
        if (checkedId == R.id.chipAdvanced)     return "advanced";
        return "beginner";
    }

    private void saveAndContinue() {
        if (pendingSkills.isEmpty()) { goToMain(); return; }

        showLoading(getString(R.string.loading));
        ApiClient.get(this)
                 .bulkCreateSkills(new BulkSkillRequest(pendingSkills))
                 .enqueue(new ApiCallback<SkillResponse>() {
                     @Override public void onSuccess(SkillResponse body) {
                         hideLoading();
                         goToMain();
                     }
                     @Override public void onError(String message) {
                         hideLoading();
                         showError(message);
                         goToMain(); // still proceed even on partial error
                     }
                 });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
