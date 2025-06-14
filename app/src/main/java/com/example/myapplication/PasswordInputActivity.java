package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordInputActivity extends AppCompatActivity {

    private EditText emailInput, newPasswordInput, confirmPasswordInput;
    private Button emailCheckButton, passwordChangeButton, backButton;

    private boolean isEmailVerified = false; // 이메일이 확인되었는지 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_input);

        emailInput = findViewById(R.id.edittext_email_input);
        newPasswordInput = findViewById(R.id.edittext_password_1_name);
        confirmPasswordInput = findViewById(R.id.edittext_password_2_name);

        emailCheckButton = findViewById(R.id.email_okay_button); // ✅ 새로 추가된 버튼
        passwordChangeButton = findViewById(R.id.password_okay_button);
        backButton = findViewById(R.id.re_email_setting_1);

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // ✅ 이메일 확인 버튼
        emailCheckButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            api.checkEmail(email).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        isEmailVerified = true;
                        Toast.makeText(PasswordInputActivity.this, "이메일 확인 완료", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PasswordInputActivity.this, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(PasswordInputActivity.this, "이메일 확인 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // ✅ 비밀번호 변경 버튼
        passwordChangeButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (!isEmailVerified) {
                Toast.makeText(this, "먼저 이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            api.resetPassword(email, newPassword).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PasswordInputActivity.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PasswordInputActivity.this, LoginPageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PasswordInputActivity.this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(PasswordInputActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 🔙 돌아가기 버튼
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(PasswordInputActivity.this, MyPageSettingActivity.class));
            finish();
        });
    }
}
