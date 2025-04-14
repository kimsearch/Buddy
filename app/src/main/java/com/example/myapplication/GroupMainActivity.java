package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class GroupMainActivity extends AppCompatActivity {

    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private AppCompatImageButton navHome, navGroup, navMyPage;
    private EditText goalInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        // ✅ Intent로 전달받은 값 처리
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String startDate = intent.getStringExtra("startDate");
        String alarmSetting = intent.getStringExtra("alarmSetting");
        String goalSubtitle = intent.getStringExtra("goalSubtitle");

        TextView titleView = findViewById(R.id.group_main_title);
        TextView startDateView = findViewById(R.id.textViewStartDate);
        TextView alarmRepeatView = findViewById(R.id.textViewAlarmRepeat);
        TextView goalSettingView = findViewById(R.id.textGoalSetting);

        if (titleView != null) titleView.setText(groupName);
        if (startDateView != null) startDateView.setText(startDate);
        if (alarmRepeatView != null) alarmRepeatView.setText(alarmSetting);
        if (goalSettingView != null) goalSettingView.setText(goalSubtitle);

        // ✅ 헤더 버튼 연결
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        notificationButton1.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupMemberActivity.class)));

        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupCommunityActivity.class)));

        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, AlarmPageActivity.class)));

        // ✅ 하단 네비게이션 버튼 연결
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, MainActivity.class)));

        navGroup.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupPageActivity.class)));

        navMyPage.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, MyPageMainActivity.class)));

        // ✅ 목표 입력란
        goalInputEditText = findViewById(R.id.goal_input_edittext);
    }
}
