package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class MyPageSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_setting);

        // 1. 이메일 찾기 버튼
        Button emailButton = findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, EmailFindMainActivity.class); // 이메일 찾기 액티비티
                startActivity(intent);
            }
        });

        // 2. 홈 버튼 → activity_main.xml
        AppCompatImageButton homeBtn = findViewById(R.id.nav_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, MainActivity.class); // 홈 액티비티
                startActivity(intent);
            }
        });

        // 3. 그룹 버튼 → group_page.xml
        AppCompatImageButton groupBtn = findViewById(R.id.nav_group);
        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, GroupPageActivity.class); // 그룹 액티비티
                startActivity(intent);
            }
        });

        // 4. 마이페이지 버튼 → mypage_main.xml
        AppCompatImageButton mypageBtn = findViewById(R.id.nav_mypage);
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, MyPageMainActivity.class); // 마이페이지 액티비티
                startActivity(intent);
            }
        });
    }
}
