package com.example.helloworld_java.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.helloworld_java.ui.fragment.EfficiencyFragment;
import com.example.helloworld_java.utils.NotificationHelper;
import com.example.helloworld_java.ui.fragment.ProfileFragment;
import com.example.helloworld_java.R;
import com.example.helloworld_java.ui.fragment.FocusFragment;
import com.example.helloworld_java.ui.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationHelper.createNotificationChannel(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }
        setupBottomNavigation();
        if (savedInstanceState == null) {
            showHomeFragment();
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences autoLoginSp = getSharedPreferences("auto_login", MODE_PRIVATE);
        return autoLoginSp.getBoolean("is_auto_login", false);
    }

    private void redirectToLogin() {
        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(this::handleNavigation);
    }

    private boolean handleNavigation(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            showHomeFragment();
            return true;
        } else if (itemId == R.id.nav_focus) {
            showFocusFragment();
            return true;
        } else if (itemId == R.id.nav_efficiency) {
            showEfficiencyFragment();
            return true;
        } else if (itemId == R.id.nav_profile) {
            showProfileFragment();
            return true;
        }
        return false;
    }

    private void showHomeFragment() {
        replaceFragment(new HomeFragment());
        setTitle("我的待办");
    }

    private void showFocusFragment() {
        replaceFragment(new FocusFragment());
        setTitle("专注模式");
    }

    private void showEfficiencyFragment() {
        replaceFragment(new EfficiencyFragment());
        setTitle("效率统计");
    }

    private void showProfileFragment() {
        replaceFragment(new ProfileFragment());
        setTitle("个人中心");
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}