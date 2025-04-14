package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPageActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, findEmailButton, signupButton;

    private Retrofit_interface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // 자동 로그인 확인
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("userEmail", null);
        if (savedEmail != null) {
            // 이미 로그인된 상태면 바로 MainActivity로 이동
            startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
            finish();
            return;
        }

        emailInput = findViewById(R.id.edittext_email_login);
        passwordInput = findViewById(R.id.edittext_password_input);
        loginButton = findViewById(R.id.login_login_button);
        findEmailButton = findViewById(R.id.find_email_button);
        signupButton = findViewById(R.id.signup_button);

        apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            apiService.login(request).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // 로그인 성공 시 이메일 저장
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("userEmail", email);
                        editor.apply();

                        Toast.makeText(LoginPageActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginPageActivity.this, "로그인 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(LoginPageActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        findEmailButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPageActivity.this, EmailFindMainActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPageActivity.this, JoinPageActivity.class);
            startActivity(intent);
        });
    }
}
