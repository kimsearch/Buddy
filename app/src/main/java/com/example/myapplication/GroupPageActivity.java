package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class GroupPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_page);

        // 1. 알림 버튼 → alarm_page.xml
        AppCompatImageButton notificationBtn = findViewById(R.id.notification_button);
        notificationBtn.setOnClickListener(v -> startActivity(new Intent(this, AlarmPageActivity.class)));

        // 2. 그룹 목표 1 - '하루 만보 걷기 챌린지' 버튼 → group_main.xml
        Button goal1Btn = findViewById(R.id.group_goal_button_1); // 버튼에 id 설정 필요
        goal1Btn.setOnClickListener(v -> startActivity(new Intent(this, GroupMainActivity.class)));

        // 3. 그룹 목표 1 - ic_delete 버튼 → group_delete.xml
        AppCompatImageButton deleteGoal1Btn = findViewById(R.id.delete_group_goals_button);
        deleteGoal1Btn.setOnClickListener(v -> startActivity(new Intent(this, GroupDeleteActivity.class)));

        // 4. 그룹 목표 2 - '같이 다이어트 해요' 버튼 → group_main.xml
        Button goal2Btn = findViewById(R.id.group_goal_button_2); // 버튼에 id 설정 필요
        goal2Btn.setOnClickListener(v -> startActivity(new Intent(this, GroupMainActivity.class)));

        // 5. 그룹 목표 2 - ic_edit 버튼 → group_exit.xml
        AppCompatImageButton editGoal2Btn = findViewById(R.id.edit_group_goals_button_1);
        editGoal2Btn.setOnClickListener(v -> startActivity(new Intent(this, GroupExitActivity.class)));

        // 6. 그룹 목표 3 - '관리 할 사람이' 버튼 → group_main.xml
        Button goal3Btn = findViewById(R.id.group_goal_3); // 버튼에 id 설정 필요
        goal3Btn.setOnClickListener(v -> startActivity(new Intent(this, GroupMainActivity.class)));

        // 7. 그룹 목표 3 - ic_edit 버튼 → group_exit.xml
        AppCompatImageButton editGoal3Btn = findViewById(R.id.edit_group_goals_button_2);
        editGoal3Btn.setOnClickListener(v -> startActivity(new Intent(this, GroupExitActivity.class)));

        // 8. '그룹 만들기' 버튼 → group_make.xml
        Button createRoomBtn = findViewById(R.id.create_room_button);
        createRoomBtn.setOnClickListener(v -> startActivity(new Intent(this, GroupMakeActivity.class)));

        // 9. 하단 네비게이션 - 홈 → activity_main.xml
        AppCompatImageButton navHome = findViewById(R.id.nav_home);
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        // 10. 하단 네비게이션 - 그룹 → group_page.xml (자기 자신)
        AppCompatImageButton navGroup = findViewById(R.id.nav_group);
        navGroup.setOnClickListener(v -> {
            // 현재 페이지라서 굳이 이동 안 해도 되지만, 원하면 refresh 가능
            recreate();
        });

        // 11. 하단 네비게이션 - 진행 상황 → sample_layout.xml
        AppCompatImageButton navProgress = findViewById(R.id.nav_search);
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, SampleLayoutActivity.class)));
    }
}
