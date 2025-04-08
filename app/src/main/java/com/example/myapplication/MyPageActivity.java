package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MyPageActivity extends AppCompatActivity {

    ImageButton navHome, navGroup, navAlarm, navMyPage;
    ImageButton settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_main);

        // 하단 네비게이션 바 버튼 초기화
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navAlarm = findViewById(R.id.nav_alarm);
        navMyPage = findViewById(R.id.nav_mypage);

        // 설정 버튼
        settingsButton = findViewById(R.id.settings_button);

        // 설정 버튼 클릭 이벤트
        settingsButton.setOnClickListener(v -> {
            // 설정 페이지로 이동하는 인텐트 예시 (SettingsActivity가 있다고 가정)
            Intent intent = new Intent(MyPageActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // 네비게이션 버튼 클릭 이벤트 처리
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, GroupPageActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageActivity.this, AlarmPageActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navMyPage.setOnClickListener(v -> {
            Toast.makeText(this, "이미 마이페이지입니다.", Toast.LENGTH_SHORT).show();
        });
    }
}
