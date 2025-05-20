package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class JoinMembershipPageActivity extends AppCompatActivity {

    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_membership_page);

        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 페이지로 이동
                Intent intent = new Intent(JoinMembershipPageActivity.this, LoginPageActivity.class);
                startActivity(intent);
                finish();  // 현재 액티비티 종료 (원하면 생략 가능)
            }
        });
    }
}
