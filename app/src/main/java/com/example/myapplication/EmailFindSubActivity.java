package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EmailFindSubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_find_sub);

        // 비밀번호 찾기 버튼 연결
        Button passwordSearchButton = findViewById(R.id.password_search_button);

        // 버튼 클릭 시 password_input.xml로 이동
        passwordSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailFindSubActivity.this, PasswordInputActivity.class);
                startActivity(intent);
            }
        });
    }
}
