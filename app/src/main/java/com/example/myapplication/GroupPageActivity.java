package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class GroupPageActivity extends AppCompatActivity {

    // UI 요소들 선언
    private AppCompatImageButton notificationBtn, deleteGoal1Btn, deleteGoal2Btn, deleteGoal3Btn;
    private AppCompatImageButton navHome, navGroup, navSearch, navMyPage;
    private Button goal1Btn, goal2Btn, goal3Btn, createRoomBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_page);

        initViews();       // 뷰 초기화
        setupListeners();  // 리스너 설정
    }

    // 1. View 요소 초기화
    private void initViews() {
        notificationBtn = findViewById(R.id.notification_button);
        deleteGoal1Btn = findViewById(R.id.delete_group_goals_button);
        deleteGoal2Btn = findViewById(R.id.delete_group_goals_button_1);
        deleteGoal3Btn = findViewById(R.id.delete_group_goals_button_2);

        goal1Btn = findViewById(R.id.group_goal_button_1);
        goal2Btn = findViewById(R.id.group_goal_button_2);
        goal3Btn = findViewById(R.id.group_goal_3);
        createRoomBtn = findViewById(R.id.create_room_button);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navMyPage = findViewById(R.id.nav_mypage);
    }

    // 2. 리스너 연결
    private void setupListeners() {

        // 상단 버튼들
        notificationBtn.setOnClickListener(v -> navigateTo(AlarmPageActivity.class));

        // 삭제 버튼 클릭 시 GroupExitActivity로 이동
        deleteGoal1Btn.setOnClickListener(v -> navigateTo(GroupExitActivity.class));
        deleteGoal2Btn.setOnClickListener(v -> navigateTo(GroupExitActivity.class));
        deleteGoal3Btn.setOnClickListener(v -> navigateTo(GroupExitActivity.class));

        // 그룹 목표 버튼들
        goal1Btn.setOnClickListener(v -> navigateTo(GroupMainStepActivity.class));
        goal2Btn.setOnClickListener(v -> navigateTo(GroupMainActivity.class));
        goal3Btn.setOnClickListener(v -> navigateTo(GroupMainActivity.class));

        // 그룹 만들기
        createRoomBtn.setOnClickListener(v -> navigateTo(GroupMakeActivity.class));

        // 하단 내비게이션
        navHome.setOnClickListener(v -> navigateTo(MainActivity.class));
        navGroup.setOnClickListener(v -> recreate()); // 현재 페이지 → 새로고침
        navSearch.setOnClickListener(v -> navigateTo(GroupSearchPageActivity.class));
        navMyPage.setOnClickListener(v -> navigateTo(MyPageMainActivity.class));
    }

    // 공통 인텐트 이동 함수
    private void navigateTo(Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
    }
}
