package com.example.helloworld_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private static final String TOAST_ACCOUNT_EMPTY = "请输入6-12位账号（字母/数字）";
    private static final String TOAST_PWD_EMPTY = "请输入8-16位密码（含字母和数字）";
    private static final String TOAST_CONFIRM_PWD_EMPTY = "请确认密码";
    private static final String TOAST_PWD_NOT_MATCH = "两次输入的密码不一致";
    private static final String TOAST_REGISTER_SUCCESS = "注册成功！即将返回登录页";

    private EditText etRegisterAccount;
    private EditText etRegisterPwd;
    private EditText etRegisterConfirmPwd;
    private Button btnRegister;
    private Button btnBackLogin;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedPreferences();
        setContentView(R.layout.activity_register);
        initViews();
        setEditTextProperties();
        bindButtonClick();

    }


    private void initViews() {
        etRegisterAccount = findViewById(R.id.et_register_account);
        etRegisterPwd = findViewById(R.id.et_register_pwd);
        etRegisterConfirmPwd = findViewById(R.id.et_register_confirm_pwd);
        btnRegister = findViewById(R.id.btn_register_submit);
        btnBackLogin = findViewById(R.id.btn_back_login);
    }

    private void setEditTextProperties() {

        etRegisterAccount.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        etRegisterAccount.setHint("6-12位账号（字母/数字）");

        etRegisterPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etRegisterPwd.setHint("8-16位密码（含字母和数字）");

        etRegisterConfirmPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etRegisterConfirmPwd.setHint("再次输入密码");
    }

    private void bindButtonClick() {
            btnRegister.setOnClickListener(v -> {
            String account = etRegisterAccount.getText().toString().trim();
            String pwd = etRegisterPwd.getText().toString().trim();
            String confirmPwd = etRegisterConfirmPwd.getText().toString().trim();
            if (!checkAccountValid(account)) {
                showToast(TOAST_ACCOUNT_EMPTY);
            } else if (pwd.isEmpty()) {
                showToast(TOAST_PWD_EMPTY);
            } else if (confirmPwd.isEmpty()) {
                showToast(TOAST_CONFIRM_PWD_EMPTY);
            } else if (!checkPwdValid(pwd)) {
                showToast(TOAST_PWD_EMPTY);
            } else if (!pwd.equals(confirmPwd)) {
                showToast(TOAST_PWD_NOT_MATCH);
            } else {
                showToast(TOAST_REGISTER_SUCCESS);
                saveUserInfo(account,pwd);
                finish();
            }
        });
            btnBackLogin.setOnClickListener(v -> finish());
    }

    private boolean checkAccountValid(String account) {
        if (account.isEmpty()) return false;
        String accountRegex = "^[A-Za-z0-9]{6,12}$";
        return account.matches(accountRegex);
    }


    private boolean checkPwdValid(String pwd) {
        if (pwd.isEmpty()) return false;
        String pwdRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$";
        return pwd.matches(pwdRegex);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void initSharedPreferences(){
        sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
    }
    private void saveUserInfo(String account,String pwd){
        if (sharedPreferences.contains(account)) {
            showToast("该用户名已被注册");
            return;
        }
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(account,pwd);
        editor.apply();
    }
}