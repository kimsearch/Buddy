package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SampleLayoutActivity extends AppCompatActivity {

    private ImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton navHome, navGroup, navProgress, navAlarm, navMypage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_layout);

        // 상단 알림 버튼
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        notificationButton1.setOnClickListener(view -> {
            // 예: 그룹 멤버 화면으로 이동
            Intent intent = new Intent(SampleLayoutActivity.this, GroupMemberActivity.class);
            startActivity(intent);
        });

        notificationButton2.setOnClickListener(view -> {
            // 예: 커뮤니티 화면으로 이동
            Intent intent = new Intent(SampleLayoutActivity.this, GroupCommunityActivity.class);
            startActivity(intent);
        });

        notificationButton3.setOnClickListener(view -> {
            // 예: 알림 화면으로 이동
            Intent intent = new Intent(SampleLayoutActivity.this, AlarmPageActivity.class);
            startActivity(intent);
        });

        // 하단 네비게이션 버튼
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navProgress = findViewById(R.id.nav_progress);
        navAlarm = findViewById(R.id.nav_alarm);
        navMypage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(view -> {
            Intent intent = new Intent(SampleLayoutActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(view -> {
            Intent intent = new Intent(SampleLayoutActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navProgress.setOnClickListener(view -> {
            Intent intent = new Intent(SampleLayoutActivity.this, SampleLayoutActivity.class);
            startActivity(intent);
        });

        navAlarm.setOnClickListener(view -> {
            Intent intent = new Intent(SampleLayoutActivity.this, AlarmPageActivity.class);
            startActivity(intent);
        });

        navMypage.setOnClickListener(view -> {
            Intent intent = new Intent(SampleLayoutActivity.this, MyPageActivity.class);
            startActivity(intent);
        });
    }
}
