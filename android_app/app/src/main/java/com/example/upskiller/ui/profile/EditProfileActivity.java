package com.example.upskiller.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.upskiller.R;
import com.example.upskiller.model.User;
import com.example.upskiller.model.response.AuthResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends BaseActivity {

    private ImageView ivAvatar;
    private TextInputLayout tilName;
    private TextInputEditText etName;
    private Uri selectedImageUri;

    // ── Image picker launcher ────────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this).load(selectedImageUri).circleCrop().into(ivAvatar);
                }
            });

    // ── Permission request launcher ──────────────────────────────────────────
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    openGallery();
                } else {
                    showError("Storage permission is required to pick a photo.");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }

        ivAvatar = findViewById(R.id.ivAvatar);
        tilName  = findViewById(R.id.tilName);
        etName   = findViewById(R.id.etName);

        // Pre-fill from session
        User user = getSession().getUser();
        if (user != null) {
            etName.setText(user.getName());
            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                Glide.with(this).load(user.getProfilePicture()).circleCrop().into(ivAvatar);
            }
        }

        findViewById(R.id.btnChangePicture).setOnClickListener(v -> pickImage());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());
    }

    // ── Image picking with permission check ──────────────────────────────────

    private void pickImage() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // ── Save profile ─────────────────────────────────────────────────────────

    private void saveProfile() {
        tilName.setError(null);
        String name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) { tilName.setError("Name required"); return; }

        showLoading(getString(R.string.loading));

        Map<String, String> body = new HashMap<>();
        body.put("name", name);

        ApiClient.get(this).updateProfileName(body)
                 .enqueue(new ApiCallback<AuthResponse>() {
                     @Override public void onSuccess(AuthResponse res) {
                         if (isFinishing() || isDestroyed()) return;
                         hideLoading();
                         if (res.getUser() != null) getSession().saveUser(res.getUser());
                         showMessage("Profile updated");
                         finish();
                     }
                     @Override public void onError(String message) {
                         if (isFinishing() || isDestroyed()) return;
                         hideLoading();
                         showError(message);
                     }
                 });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}
