package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton alarmButton, calendarButton;
    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private RecyclerView groupGoalRecyclerView;
    private GroupGoalAdapter groupGoalAdapter;
    private List<GoalItem> goalItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmButton = findViewById(R.id.notification_button);
        calendarButton = findViewById(R.id.calendar);
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        navPet = findViewById(R.id.nav_pet);
        navSearch = findViewById(R.id.nav_search);

        // 터치 효과
        applyTouchEffect(alarmButton);
        applyTouchEffect(calendarButton);

        alarmButton.setOnClickListener(v -> startActivity(new Intent(this, AlarmPageActivity.class)));
        calendarButton.setOnClickListener(v -> startActivity(new Intent(this, CalendarActivity.class)));
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // 그룹 목표 리사이클러뷰 설정
        groupGoalRecyclerView = findViewById(R.id.group_goal_recycler);
        goalItemList = new ArrayList<>();
        goalItemList.add(new GoalItem("하루 만보 걷기 챌린지", 100));
        goalItemList.add(new GoalItem("같이 다이어트 해요", 30));

// goalItemList에서 제목과 달성률 분리
        List<String> goalTitles = new ArrayList<>();
        List<Integer> completionRates = new ArrayList<>();
        for (GoalItem item : goalItemList) {
            goalTitles.add(item.getTitle());
            completionRates.add(item.getCompletionRate());
        }

        groupGoalAdapter = new GroupGoalAdapter(this, goalTitles, completionRates);
        groupGoalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupGoalRecyclerView.setAdapter(groupGoalAdapter);
    }

    private void applyTouchEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.6f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f);
                    break;
            }
            return false;
        });
    }
}
