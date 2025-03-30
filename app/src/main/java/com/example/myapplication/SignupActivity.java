package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText nameInput, idInput, emailInput, passwordInput, passwordCheckInput;
    private Button idCheckButton, emailCheckButton, passwordCheckButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup); // signup.xml 연결

        // View 연결
        nameInput = findViewById(R.id.name_input);
        idInput = findViewById(R.id.id_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        passwordCheckInput = findViewById(R.id.password_check_input);

        idCheckButton = findViewById(R.id.id_check_button);
        emailCheckButton = findViewById(R.id.email_check_button);
        passwordCheckButton = findViewById(R.id.password_check_button);
        signupButton = findViewById(R.id.signup_button);

        // 아이디 중복확인 버튼
        idCheckButton.setOnClickListener(v -> {
            String id = idInput.getText().toString().trim();
            if (id.isEmpty()) {
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 실제 중복확인은 서버 연동 필요
                Toast.makeText(this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 이메일 중복확인 버튼
        emailCheckButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 실제 중복확인은 서버 연동 필요
                Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 비밀번호 확인 버튼
        passwordCheckButton.setOnClickListener(v -> {
            String pw = passwordInput.getText().toString();
            String pwCheck = passwordCheckInput.getText().toString();
            if (pw.isEmpty() || pwCheck.isEmpty()) {
                Toast.makeText(this, "비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (!pw.equals(pwCheck)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 회원가입 버튼
        signupButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String id = idInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String pw = passwordInput.getText().toString();
            String pwCheck = passwordCheckInput.getText().toString();

            if (name.isEmpty() || id.isEmpty() || email.isEmpty() || pw.isEmpty() || pwCheck.isEmpty()) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pw.equals(pwCheck)) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 실제 회원가입 처리는 서버 연동 또는 Firebase 등에서 진행
            Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_SHORT).show();

            // 가입 후 로그인 화면으로 돌아가기
            finish();
        });
    }
}
