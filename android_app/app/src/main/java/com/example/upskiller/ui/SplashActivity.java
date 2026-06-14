package com.example.upskiller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.upskiller.R;
import com.example.upskiller.ui.auth.LoginActivity;
import com.example.upskiller.ui.base.BaseActivity;
import com.example.upskiller.ui.main.MainActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Class<?> destination = getSession().isLoggedIn()
                    ? MainActivity.class
                    : LoginActivity.class;

            Intent intent = new Intent(SplashActivity.this, destination);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1200);
    }
}
