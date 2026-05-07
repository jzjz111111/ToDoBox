package com.example.helloworld_java.ui.activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.CheckBox;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.helloworld_java.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etAccount, etPwd;
    private CheckBox cbRememberPwd;
    private SharedPreferences sp;
    private Button btnLogin;
    private Button btnRegister;
    private SharedPreferences sharedPreferences;
    private  static final String SP_NAME="user_login_info";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        adaptSystemBars();
        initViews();
        initSharedPreferences();
        loadSavedUserInfo();
        bindButtonClick();

    }
    private void adaptSystemBars(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initViews(){
        etAccount=findViewById(R.id.et_account);
        etPwd=findViewById(R.id.et_pwd);
        cbRememberPwd=findViewById(R.id.cb_remember_pwd);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
    }
    private void initSharedPreferences(){
        sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
        sp=getSharedPreferences(SP_NAME,MODE_PRIVATE);
    }
    private void loadSavedUserInfo(){
        boolean isRemember =sp.getBoolean("remember_pwd",false);
        if(isRemember){
            String account=sp.getString("account","");
            String pwd=sp.getString("password","");
            etAccount.setText(account);
            etPwd.setText(pwd);
            cbRememberPwd.setChecked(true);


        }
    }//使在由别的页面回到登录页时写的账号密码填充

    private void bindButtonClick(){
        btnLogin.setOnClickListener(v->{
            String account = etAccount.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            boolean isRemember = ((CheckBox) findViewById(R.id.cb_remember_pwd)).isChecked();
            if(checkInputValid(account,pwd)&&checkUserInfoMatch(account,pwd)) {
                saveRememberMeStatus(account, pwd, isRemember);
                saveAutoLoginStatus(account);
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "即将跳转到注册页", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }


    //方法后括号里是设计的变量
    private boolean checkUserInfoMatch(String inputAccount, String inputPwd) {
        // 获取注册时保存的账号密码，前面键值对是账号与密码
        String savedPwd = sharedPreferences.getString(inputAccount, "");
        // 比对输入与存储的信息
        if (TextUtils.isEmpty(savedPwd)) {
            Toast.makeText(this, "账号不存在，请注册一个吧", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (savedPwd.equals(inputPwd)) {
            return true;
        } else {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private boolean checkInputValid(String account, String pwd) {
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void saveRememberMeStatus(String account, String pwd, boolean isRemember) {
        SharedPreferences.Editor editor = sp.edit();
        if (isRemember) {
            editor.putString("account", account);
            editor.putString("password", pwd);
            editor.putBoolean("remember_pwd", true);
        } else {
            editor.remove("account");
            editor.remove("password");
            editor.putBoolean("remember_pwd", false);
        }
        editor.apply();
    }
    private void saveAutoLoginStatus(String account) {
        SharedPreferences autoLoginSp = getSharedPreferences("auto_login", MODE_PRIVATE);
        SharedPreferences.Editor editor = autoLoginSp.edit();
        editor.putBoolean("is_auto_login", true);
        editor.putString("logged_in_user", account);
        editor.apply();
        String currentPwd = etPwd.getText().toString().trim(); // 获取当前输入的密码
        SharedPreferences userSp = getSharedPreferences("user_info", MODE_PRIVATE);
        userSp.edit().putString("current_user", account).putString("current_password", currentPwd).apply();
    }
}