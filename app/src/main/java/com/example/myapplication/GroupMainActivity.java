package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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
    private AppCompatImageButton navHome, navGroup,navSearch, navPet, navMyPage;

    private int totalGoal = 0;   // 목표 누적값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        // ✅ Intent로 전달된 그룹 이름, 목표, ID 받기
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long groupId = intent.getLongExtra("groupId", -1L);

        if (groupId == -1L) {
            Toast.makeText(this, "잘못된 그룹 정보입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 알림 버튼
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        notificationButton1.setOnClickListener(v -> {
            Intent intent1 = new Intent(GroupMainActivity.this, GroupMemberActivity.class);
            intent1.putExtra("groupId", groupId);
            startActivity(intent1);
        });

        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupCommunityActivity.class)));

        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, AlarmPageActivity.class)));

        // 뒤로가기 버튼
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // 하단 네비게이션
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, MainActivity.class)));

        navGroup.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupPageActivity.class)));

        navSearch.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupSearchPageActivity.class)));

        navPet.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, PetActivity.class)));

        navMyPage.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, MyPageMainActivity.class)));

        // UI 연결
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);

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

        // 목표 입력 버튼 클릭
        goalInputButton.setOnClickListener(v -> {
            String inputGoal = goalInputEditText.getText().toString();
            if (!inputGoal.isEmpty()) {
                try {
                    int inputGoalInt = Integer.parseInt(inputGoal);
                    totalGoal += inputGoalInt;
                    goalInputEditText.setText("");
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateGoalDisplay() {
        groupGoalView.setText("현재 목표: " + totalGoal);
    }
}
