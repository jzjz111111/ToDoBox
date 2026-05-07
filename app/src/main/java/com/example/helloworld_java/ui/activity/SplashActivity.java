package com.example.helloworld_java.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld_java.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final String AUTO_LOGIN_SP = "auto_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkAutoLoginStatus();
    }

    private void checkAutoLoginStatus() {
        SharedPreferences autoLoginSp = getSharedPreferences(AUTO_LOGIN_SP, MODE_PRIVATE);
        boolean isAutoLogin = autoLoginSp.getBoolean("is_auto_login", false);
        new Handler().postDelayed(() -> {
            Intent intent;
            if (isAutoLogin) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
