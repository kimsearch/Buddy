package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class GroupMainActivity extends AppCompatActivity {

    private TextView groupMainTitle;  // 그룹 이름 표시
    private TextView groupGoalView;   // 그룹 목표 표시
    private EditText goalInputEditText;  // 목표 입력 받는 EditText
    private Button goalInputButton;  // 목표 입력 버튼

    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private AppCompatImageButton navHome, navGroup, navMyPage;


    private int totalGoal = 0;   // 목표 누적값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        notificationButton1.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupMemberActivity.class)));

        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupCommunityActivity.class)));

        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, AlarmPageActivity.class)));

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); //
        });

        // 하단 네비게이션 버튼 연결
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        // navProgress = findViewById(R.id.nav_progress); // 주석 처리: 레이아웃에 없음

        navHome.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, MainActivity.class)));

        navGroup.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupPageActivity.class)));

        navMyPage.setOnClickListener(v -> {
            Intent intent = new Intent(GroupMainActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });

        // UI 연결
        groupMainTitle = findViewById(R.id.group_main_title);  // 그룹 이름
        groupGoalView = findViewById(R.id.group_goal_view);    // 그룹 목표
        goalInputEditText = findViewById(R.id.goal_input_edittext);  // 목표 입력란
        goalInputButton = findViewById(R.id.goal_input_button);  // 목표 입력 버튼

        // Intent로 전달된 그룹 이름 및 목표 받기
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long groupId = intent.getLongExtra("groupId", -1L);

// 그룹 이름과 목표 설정
        if (groupName != null && !groupName.isEmpty()) {
            groupMainTitle.setText(groupName);
        } else {
            groupMainTitle.setText("이름 없는 그룹");
        }

        if (groupGoal != null && !groupGoal.isEmpty()) {
            groupGoalView.setText("그룹 목표: " + groupGoal);
        } else {
            groupGoalView.setText("그룹 목표가 없습니다");
        }

        // 목표 입력 버튼 클릭 시
        goalInputButton.setOnClickListener(v -> {
            // 입력값을 가져와서 정수로 변환
            String inputGoal = goalInputEditText.getText().toString();
            if (!inputGoal.isEmpty()) {
                try {
                    int inputGoalInt = Integer.parseInt(inputGoal);
                    totalGoal += inputGoalInt;  // 목표 누적 덧셈
                    goalInputEditText.setText("");  // 입력란 비우기
                    // 업데이트된 목표 값 표시
                    groupGoalView.setText("현재 목표: " + totalGoal);
                } catch (NumberFormatException e) {
                    Toast.makeText(GroupMainActivity.this, "숫자를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(GroupMainActivity.this, "목표를 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 이곳에 센서 관련 로직을 추가하지 않음 (만약 필요시 추가)
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 이곳에 센서 리스너 해제 등을 추가하지 않음 (만약 필요시 추가)
    }

    // 목표 업데이트 메소드 (추가 로직)
    private void updateGoalDisplay() {
        groupGoalView.setText("현재 목표: " + totalGoal);
    }
}
