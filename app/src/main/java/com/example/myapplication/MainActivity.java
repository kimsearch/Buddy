package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageButton alarmButton, calendarButton;
    private Button goal1Button, goal2Button;
    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmButton = findViewById(R.id.notification_button);
        calendarButton = findViewById(R.id.calendar);
        goal1Button = findViewById(R.id.btnWalkTracker);  // 하루 만보 걷기 챌린지
        goal2Button = findViewById(R.id.btnDiet);  // 같이 다이어트 해요
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        navPet = findViewById(R.id.nav_pet);
        navSearch = findViewById(R.id.nav_search);

        // 터치 효과 공통 함수
        applyTouchEffect(alarmButton);
        applyTouchEffect(calendarButton);

        // 클릭 이벤트 1: 알람 버튼 → alarm_page.xml
        alarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlarmPageActivity.class);
            startActivity(intent);
        });

        // 클릭 이벤트 2: 캘린더 버튼 → group_goal_calendar.xml
        calendarButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        // 클릭 이벤트 3: 그룹 목표 1 → group_main.xml
        goal1Button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupMainActivity.class);
            startActivity(intent);
        });

        // 클릭 이벤트 4: 그룹 목표 2 → group_main.xml
        goal2Button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupMainActivity.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
           Intent intent = new Intent(MainActivity.this, MainActivity.class);
           startActivity(intent);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupSearchPageActivity.class);
            startActivity(intent);
        });

        navPet.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PetActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });
    }

    // 터치 효과 주기 위한 함수
    private void applyTouchEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(0.6f); // 눌렀을 때 투명도 변경
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.setAlpha(1.0f); // 원래대로
                    break;
            }
            return false; // 클릭 이벤트도 함께 동작하게 하기 위해 false
        });
    }
}
