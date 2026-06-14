package com.example.upskiller.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.upskiller.R;
import com.example.upskiller.model.User;
import com.example.upskiller.model.response.AuthResponse;
import com.example.upskiller.model.response.SkillStatsResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.session.SessionManager;
import com.example.upskiller.ui.auth.LoginActivity;
import com.example.upskiller.ui.skills.SkillsActivity;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvName, tvEmail, tvTotalSkills, tvBeginner, tvIntermediate, tvAdvanced;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ivAvatar       = view.findViewById(R.id.ivAvatar);
        tvName         = view.findViewById(R.id.tvName);
        tvEmail        = view.findViewById(R.id.tvEmail);
        tvTotalSkills  = view.findViewById(R.id.tvTotalSkills);
        tvBeginner     = view.findViewById(R.id.tvBeginner);
        tvIntermediate = view.findViewById(R.id.tvIntermediate);
        tvAdvanced     = view.findViewById(R.id.tvAdvanced);

        // Load cached user immediately
        User cached = SessionManager.getInstance(requireContext()).getUser();
        if (cached != null) bindUser(cached);

        // Fetch fresh profile + stats
        fetchProfile();
        fetchStats();

        view.findViewById(R.id.btnEditProfile).setOnClickListener(v ->
                startActivity(new Intent(getContext(), EditProfileActivity.class)));

        view.findViewById(R.id.btnManageSkills).setOnClickListener(v ->
                startActivity(new Intent(getContext(), SkillsActivity.class)));

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        view.findViewById(R.id.btnDeleteAccount).setOnClickListener(v -> confirmDeleteAccount());
    }

    @Override public void onResume() { super.onResume(); fetchProfile(); fetchStats(); }

    private void fetchProfile() {
        ApiClient.get(requireContext()).getProfile()
                 .enqueue(new ApiCallback<AuthResponse>() {
                     @Override public void onSuccess(AuthResponse body) {
                         if (!isAdded() || getContext() == null) return;
                         if (body.getUser() != null) {
                             SessionManager.getInstance(requireContext()).saveUser(body.getUser());
                             bindUser(body.getUser());
                         }
                     }
                     @Override public void onError(String message) { /* silent — cached shown */ }
                 });
    }

    private void fetchStats() {
        ApiClient.get(requireContext()).getSkillStats()
                 .enqueue(new ApiCallback<SkillStatsResponse>() {
                     @Override public void onSuccess(SkillStatsResponse body) {
                         if (!isAdded() || getView() == null) return;
                         tvTotalSkills.setText(String.valueOf(body.getTotalSkills()));
                         tvBeginner.setText(String.valueOf(body.getBeginner()));
                         tvIntermediate.setText(String.valueOf(body.getIntermediate()));
                         tvAdvanced.setText(String.valueOf(body.getAdvanced()));
                     }
                     @Override public void onError(String message) { }
                 });
    }

    private void bindUser(User user) {
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            Glide.with(this).load(user.getProfilePicture()).circleCrop().into(ivAvatar);
        }
    }

    private void logout() {
        SessionManager session = SessionManager.getInstance(requireContext());
        String refresh = session.getRefreshToken();
        Map<String, String> body = new HashMap<>();
        body.put("refresh_token", refresh != null ? refresh : "");

        ApiClient.get(requireContext()).logout(body).enqueue(new ApiCallback<Void>() {
            @Override public void onSuccess(Void b) { clearAndGoLogin(); }
            @Override public void onError(String m) { clearAndGoLogin(); } // logout anyway
        });
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage(getString(R.string.msg_confirm_delete_account))
                .setPositiveButton(R.string.btn_yes, (d, w) -> {
                    ApiClient.get(requireContext()).deleteAccount()
                             .enqueue(new ApiCallback<Void>() {
                                 @Override public void onSuccess(Void b) { clearAndGoLogin(); }
                                 @Override public void onError(String m) {
                                     Snackbar.make(requireView(), m, Snackbar.LENGTH_LONG).show();
                                 }
                             });
                })
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    private void clearAndGoLogin() {
        SessionManager.getInstance(requireContext()).clearSession();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
