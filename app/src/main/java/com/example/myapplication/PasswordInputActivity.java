package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PasswordInputActivity extends AppCompatActivity {

    private Button reEmailSettingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_input);

        // 버튼 초기화
        reEmailSettingButton = findViewById(R.id.re_email_setting);

        // 클릭 리스너 설정
        reEmailSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MypageSettingActivity로 이동
                Intent intent = new Intent(PasswordInputActivity.this, MyPageSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
