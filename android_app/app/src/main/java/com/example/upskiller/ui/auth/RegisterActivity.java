package com.example.upskiller.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.upskiller.R;
import com.example.upskiller.model.request.RegisterRequest;
import com.example.upskiller.model.response.AuthResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.ui.onboarding.SkillSetupActivity;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends BaseActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etName, etEmail, etPassword, etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilName    = findViewById(R.id.tilName);
        tilEmail   = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirmPassword);
        etName     = findViewById(R.id.etName);
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm  = findViewById(R.id.etConfirmPassword);

        findViewById(R.id.btnRegister).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);

        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm  = etConfirm.getText().toString();

        boolean valid = true;
        if (TextUtils.isEmpty(name))  { tilName.setError("Name is required");     valid = false; }
        if (TextUtils.isEmpty(email)) { tilEmail.setError("Email is required");    valid = false; }
        if (password.length() < 8)    { tilPassword.setError("Min 8 characters"); valid = false; }
        if (!password.equals(confirm)){ tilConfirm.setError("Passwords don't match"); valid = false; }
        if (!valid) return;

        showLoading(getString(R.string.loading));

        ApiClient.get(this)
                 .register(new RegisterRequest(email, name, password, confirm))
                 .enqueue(new ApiCallback<AuthResponse>() {
                     @Override public void onSuccess(AuthResponse body) {
                         hideLoading();
                         getSession().saveTokens(body.getTokens());
                         getSession().saveUser(body.getUser());
                         Intent intent = new Intent(RegisterActivity.this, SkillSetupActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(intent);
                         finish();
                     }
                     @Override public void onError(String message) {
                         hideLoading();
                         showError(message);
                     }
                 });
    }
}
