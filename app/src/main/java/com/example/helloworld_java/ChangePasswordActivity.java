package com.example.helloworld_java;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputEditText etOldPwd, etNewPwd, etConfirmPwd;
    private SharedPreferences userSp ;
    private Button btnSavePwd,btnReturn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        userSp = getSharedPreferences("user_info", 0);
        initViews();
        bindClickListeners();
    }
    private void initViews() {
        btnReturn=findViewById(R.id.btn_return);
        etOldPwd = findViewById(R.id.et_old_pwd);
        etNewPwd = findViewById(R.id.et_new_pwd);
        etConfirmPwd = findViewById(R.id.et_confirm_pwd);
        btnSavePwd = findViewById(R.id.btn_save_pwd);
    }
    private void bindClickListeners() {
        btnReturn.setOnClickListener(v -> finish());
        btnSavePwd.setOnClickListener(v -> changePassword());
    }


    // 验证并修改密码
    private void changePassword() {
        String oldPwd = etOldPwd.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();

        //登录时代表密码的键
        String currentPwd = userSp.getString("current_password", "");
        String currentUser= userSp.getString("current_user","");

        // 校验逻辑
        if (oldPwd.isEmpty()) {
            Toast.makeText(this, "请输入原密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!oldPwd.equals(currentPwd)) {
            Toast.makeText(this, "原密码输入错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPwd.isEmpty() || newPwd.length() < 8 || newPwd.length() > 16) {
            Toast.makeText(this, "新密码需为8-16位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // 保存新密码到SP
        userSp.edit().putString("current_password", newPwd).putString(currentUser,newPwd).apply();
        Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}