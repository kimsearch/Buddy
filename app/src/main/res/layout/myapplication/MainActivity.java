package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ImageButton alarmButton, calendarButton, moreVertButton;
    private Button goal1Button, goal2Button;
    private ImageButton navHome, navGroup, navMyPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 자동 로그인 여부 확인
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("userEmail", null);

        if (savedEmail == null) {
            // 로그인하지 않은 상태 → 로그인 화면으로 이동
            Intent intent = new Intent(MainActivity.this, LoginPageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        alarmButton = findViewById(R.id.notification_button);
        calendarButton = findViewById(R.id.calendar);
        moreVertButton = findViewById(R.id.edit_group_goals_button);
        goal1Button = findViewById(R.id.btnWalkTracker);  // 하루 만보 걷기 챌린지
        goal2Button = findViewById(R.id.btnDiet);  // 같이 다이어트 해요
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);



        // 터치 효과 공통 함수
        applyTouchEffect(alarmButton);
        applyTouchEffect(calendarButton);
        applyTouchEffect(moreVertButton);

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

        // 클릭 이벤트 3: more_vert 버튼 → 팝업창으로 그룹 삭제 옵션
        moreVertButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, moreVertButton);
            popupMenu.getMenu().add("그룹 목표 1 삭제하기");
            popupMenu.getMenu().add("그룹 목표 2 삭제하기");

            popupMenu.setOnMenuItemClickListener(item -> {
                Toast.makeText(MainActivity.this, item.getTitle() + " 선택됨", Toast.LENGTH_SHORT).show();
                // 실제 삭제 로직은 여기에 작성하면 됨
                return true;
            });

            popupMenu.show();
        });

        // 클릭 이벤트 4, 5: 그룹 목표 → group_main.xml
        goal1Button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupMainActivity.class);
            startActivity(intent);
        });

        goal2Button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupMainActivity.class);
            startActivity(intent);
        });

        navHome.setOnClickListener(v -> {
            // 현재 페이지 → 아무 동작 안 함
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GroupPageActivity.class);
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
