package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class GroupMainActivity extends AppCompatActivity {

    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private AppCompatImageButton navHome, navGroup;
    private EditText goalInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        // 헤더 버튼 연결
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        notificationButton1.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupMemberActivity.class)));

        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupCommunityActivity.class)));

        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, AlarmPageActivity.class)));

        // 하단 네비게이션 버튼 연결
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        // navProgress = findViewById(R.id.nav_progress); // 주석 처리: 레이아웃에 없음

        navHome.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, MainActivity.class)));

        navGroup.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupPageActivity.class)));

        // navProgress 버튼 누락되어 주석 처리
        // navProgress.setOnClickListener(v ->
        //         startActivity(new Intent(GroupMainActivity.this, SampleLayoutActivity.class)));

        // 목표 입력란
        goalInputEditText = findViewById(R.id.goal_input_edittext);
    }
}
