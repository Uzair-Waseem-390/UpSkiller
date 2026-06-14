package com.example.upskiller.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.upskiller.R;
import com.example.upskiller.model.request.LoginRequest;
import com.example.upskiller.model.response.AuthResponse;
import com.example.upskiller.network.ApiClient;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.ui.main.MainActivity;
import com.example.upskiller.util.ApiCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends BaseActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilEmail    = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.tvRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            valid = false;
        }
        if (!valid) return;

        showLoading(getString(R.string.loading));

        ApiClient.get(this)
                 .login(new LoginRequest(email, password))
                 .enqueue(new ApiCallback<AuthResponse>() {
                     @Override public void onSuccess(AuthResponse body) {
                         hideLoading();
                         getSession().saveTokens(body.getTokens());
                         getSession().saveUser(body.getUser());
                         Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
