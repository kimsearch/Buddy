package com.example.myapplication;

import android.content.Intent;
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

public class JoinPageActivity extends AppCompatActivity {

    private EditText nicknameInput, birthInput, emailInput,
            passwordInput, passwordConfirmInput, verificationCodeInput;
    private Button signupButton, passwordCheckButton, nicknameCheckButton,
            emailAuthButton, emailVerifyButton;

    private Retrofit_interface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_page);

        nicknameInput = findViewById(R.id.edittext_id_input);
        birthInput = findViewById(R.id.edittext_birth_input);
        emailInput = findViewById(R.id.edittext_email_input);
        passwordInput = findViewById(R.id.edittext_password_input);
        passwordConfirmInput = findViewById(R.id.edittext_password_check_input);
        nicknameCheckButton = findViewById(R.id.id_check_button);
        signupButton = findViewById(R.id.id_check_button);
        emailAuthButton = findViewById(R.id.email_auth_button);
        emailVerifyButton = findViewById(R.id.email_verify_button);
        passwordCheckButton = findViewById(R.id.password_check_button);
        verificationCodeInput = findViewById(R.id.edittext_verification_code);

        apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // 비밀번호 일치 확인
        passwordCheckButton.setOnClickListener(v -> {
            String pw = passwordInput.getText().toString();
            String confirm = passwordConfirmInput.getText().toString();
            if (pw.equals(confirm)) {
                Toast.makeText(this, "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 닉네임 중복 확인
        nicknameCheckButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.checkNickname(nickname).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Toast.makeText(JoinPageActivity.this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 409) {
                        Toast.makeText(JoinPageActivity.this, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(JoinPageActivity.this, "알 수 없는 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(JoinPageActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 이메일 인증번호 전송
        emailAuthButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.requestEmailAuth(email).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(JoinPageActivity.this, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(JoinPageActivity.this, "전송 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(JoinPageActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 인증번호 확인
        emailVerifyButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String code = verificationCodeInput.getText().toString().trim();

            if (email.isEmpty() || code.isEmpty()) {
                Toast.makeText(this, "이메일과 인증번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.verifyEmailCode(email, code).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(JoinPageActivity.this, "이메일 인증 성공!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(JoinPageActivity.this, "인증 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(JoinPageActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // 회원가입 버튼 클릭
        signupButton.setOnClickListener(v -> {
            String nickname = nicknameInput.getText().toString().trim();
            String birth = birthInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = passwordConfirmInput.getText().toString().trim();

            if (nickname.isEmpty() || birth.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Member member = new Member(nickname, email, password, birth);

            apiService.createMember(member).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            Toast.makeText(JoinPageActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(JoinPageActivity.this, LoginPageActivity.class));
                            finish();
                        } else {
                            Toast.makeText(JoinPageActivity.this, "회원가입 실패: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(JoinPageActivity.this, "응답 처리 오류", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(JoinPageActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
