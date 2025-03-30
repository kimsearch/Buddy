package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddGoalActivity extends AppCompatActivity {

    private RadioGroup goalRadioGroup;
    private RadioButton radioGroupGoal, radioPersonalGoal;
    private ImageButton navHome, navGroup, navAlarm, navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_goal); // add_goal.xml과 연결

        // 라디오 버튼들 초기화
        goalRadioGroup = findViewById(R.id.goal_radio_group);
        radioGroupGoal = findViewById(R.id.radio_group_goal);
        radioPersonalGoal = findViewById(R.id.radio_personal_goal);

        // 하단 네비게이션 버튼들 초기화
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navAlarm = findViewById(R.id.nav_alarm);
        navMyPage = findViewById(R.id.nav_mypage);

        // 라디오 그룹 선택 이벤트
        goalRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_group_goal) {
                Toast.makeText(this, "그룹 목표 설정 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                // TODO: 그룹 목표 설정 화면으로 전환
                // startActivity(new Intent(this, GroupGoalActivity.class));
            } else if (checkedId == R.id.radio_personal_goal) {
                Toast.makeText(this, "개인 목표 설정 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
                // TODO: 개인 목표 설정 화면으로 전환
                // startActivity(new Intent(this, PersonalGoalActivity.class));
            }
        });

        // 네비게이션 바 클릭 처리
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        navGroup.setOnClickListener(v -> {
            Toast.makeText(this, "그룹 페이지 (예정)", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, GroupActivity.class));
        });

        navAlarm.setOnClickListener(v -> {
            Toast.makeText(this, "알림 페이지 (예정)", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, AlarmActivity.class));
        });

        navMyPage.setOnClickListener(v -> {
            Toast.makeText(this, "마이페이지 (예정)", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, MyPageActivity.class));
        });
    }
}
