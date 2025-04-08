package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPageActivity extends AppCompatActivity {

    private Button loginButton;
    private Button findNicknameButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page); // ← XML 파일 이름

        // 버튼 연결
        loginButton = findViewById(R.id.login_login_button);
        findNicknameButton = findViewById(R.id.find_nickname_button);
        signupButton = findViewById(R.id.signup_button);

        // 1. 로그인 버튼 → MailActivity
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 2. 닉네임 찾기 버튼 → EmailFindMainActivity
        findNicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageActivity.this, EmailFindMainActivity.class);
                startActivity(intent);
            }
        });

        // 3. 회원가입 버튼 → JoinMembershipPageActivity
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageActivity.this, JoinMembershipPageActivity.class);
                startActivity(intent);
            }
        });
    }
}
