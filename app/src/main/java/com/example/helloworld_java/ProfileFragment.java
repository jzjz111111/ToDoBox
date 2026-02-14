package com.example.helloworld_java;
import static android.app.Activity.RESULT_OK;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private TextView tvUsername, tvEmail;
    private Button btnLogout;

    private CardView cardEditProfile, cardChangePassword,cardSettings;
    private ActivityResultLauncher<Intent> editProfileLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadUserData();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        loadUserData();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        cardEditProfile = view.findViewById(R.id.card_edit_profile);
        cardChangePassword = view.findViewById(R.id.card_change_password);
        cardSettings=view.findViewById(R.id.card_settings);
    }

    private void loadUserData() {
        // 从SharedPreferences加载用户信息
        //current_user是登录界面尾部设置的键，在同一个文件“user_info”
        SharedPreferences userSp = requireContext().getSharedPreferences("user_info", 0);
        String username = userSp.getString("current_user", "用户名");
        String email = userSp.getString("current_email", username + "@example.com");
        tvUsername.setText(username);
        tvEmail.setText(email);
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> logout());
        cardEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });
        cardChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });
        cardSettings.setOnClickListener(v -> {
            Intent intent =new Intent(requireContext(),SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void logout() {
        SharedPreferences autoLoginSp = requireContext().getSharedPreferences("auto_login", 0);
        autoLoginSp.edit().clear().apply();
        Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
