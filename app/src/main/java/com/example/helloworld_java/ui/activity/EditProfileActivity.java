package com.example.helloworld_java.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld_java.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etEmail;
    private Button btnSave;
    private Button btnReturn;
    private SharedPreferences userSp;
    private static final String TOAST_ACCOUNT_EMPTY = "请输入6-12位账号（字母/数字）";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initViews();
        userSp = getSharedPreferences("user_info", 0);
        String currentUser = userSp.getString("current_user", "");
        String currentEmail = userSp.getString("current_email", currentUser + "@example.com");
        etUsername.setText(currentUser);
        etEmail.setText(currentEmail);
        bindClickListeners();
    }
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        btnSave = findViewById(R.id.btn_save);
        btnReturn=findViewById(R.id.btn_return);
    }
    private void bindClickListeners() {
        btnSave.setOnClickListener(v -> saveProfile());
        btnReturn.setOnClickListener(v ->finish());
    }

    private void saveProfile() {
        String newUsername = Objects.requireNonNull(etUsername.getText()).toString().trim();
        String newEmail = Objects.requireNonNull(etEmail.getText()).toString().trim();

        if (!checkAccountValid(newUsername)) {
            Toast.makeText(this,TOAST_ACCOUNT_EMPTY,Toast.LENGTH_SHORT).show();
            return;
        }
        if (    !newEmail.contains("@")) {
            Toast.makeText(this, "请输入有效的邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        //  获取当前登录的旧用户名和旧密码
        String oldUsername = userSp.getString("current_user", "");
        String oldPassword = userSp.getString("current_password", ""); // 原密码（登录时存储的）
        // 用户名唯一性校验（跳过自己和自己重复的情况）newUsername也是变量，调用方法时是用户新输得account，实现键值对的对应
        if (!newUsername.equals(oldUsername)) {
            String registeredPwd = userSp.getString(newUsername, "");
            if (!registeredPwd.isEmpty()) {
                Toast.makeText(this, "该用户名已被注册，请更换", Toast.LENGTH_SHORT).show();
                return;
            }
            userSp.edit().putString(newUsername, oldPassword).apply();
            userSp.edit().remove(oldUsername).apply();
        }
        // 保存到SharedPreferences
        userSp.edit().putString("current_user", newUsername).putString("current_email", newEmail).putString("current_password", oldPassword).apply();
        Toast.makeText(this, "资料修改成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent); //ProfileFragment刷新
        finish();
    }
    private boolean checkAccountValid(String account) {
        if (account.isEmpty()) return false;
        String accountRegex = "^[A-Za-z0-9]{6,12}$";
        return account.matches(accountRegex);
    }

}