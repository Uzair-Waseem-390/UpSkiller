package com.example.upskiller.ui.roadmap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.upskiller.R;
import com.example.upskiller.model.request.RoadmapCreateRequest;
import com.example.upskiller.model.response.RoadmapResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreateRoadmapFragment extends Fragment {

    private TextInputLayout tilSkill;
    private TextInputEditText etSkill;
    private ChipGroup chipGroupLevel;
    private ProgressDialog progressDialog;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_roadmap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tilSkill       = view.findViewById(R.id.tilTargetSkill);
        etSkill        = view.findViewById(R.id.etTargetSkill);
        chipGroupLevel = view.findViewById(R.id.chipGroupLevel);

        view.findViewById(R.id.btnGenerate).setOnClickListener(v -> generate());
    }

    private void generate() {
        tilSkill.setError(null);
        String skill = etSkill.getText().toString().trim();
        if (TextUtils.isEmpty(skill)) {
            tilSkill.setError("Enter a target skill");
            return;
        }
        String level = getSelectedLevel();

        showLoading();
        ApiClient.getLongTimeout(requireContext())
                 .createRoadmap(new RoadmapCreateRequest(skill, level))
                 .enqueue(new ApiCallback<RoadmapResponse>() {
                     @Override public void onSuccess(RoadmapResponse body) {
                         if (!isAdded() || getContext() == null) return;
                         hideLoading();
                         Intent intent = new Intent(getContext(), RoadmapDetailActivity.class);
                         intent.putExtra(RoadmapDetailActivity.EXTRA_ROADMAP_ID, body.getRoadmap().getId());
                         startActivity(intent);
                         etSkill.setText("");
                     }
                     @Override public void onError(String message) {
                         if (!isAdded() || getView() == null) return;
                         hideLoading();
                         Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
                     }
                 });
    }

    private String getSelectedLevel() {
        int id = chipGroupLevel.getCheckedChipId();
        if (id == R.id.chipIntermediate) return "intermediate";
        if (id == R.id.chipAdvanced)     return "advanced";
        return "beginner";
    }

    private void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(getString(R.string.msg_generating));
        progressDialog.show();
    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
}
