package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmPageActivity extends AppCompatActivity {

    private ImageButton rollButton;
    private ImageButton navHome, navGroup, navProgress, navAlarm, navMypage;
    private LinearLayout alarmContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_page);

        // 뷰 초기화
        rollButton = findViewById(R.id.roll_button);
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navProgress = findViewById(R.id.nav_progress);
        navAlarm = findViewById(R.id.nav_alarm);
        navMypage = findViewById(R.id.nav_mypage);

        // 알림 추가할 컨테이너 가져오기
        alarmContainer = findViewById(R.id.alarm_scroll).findViewById(android.R.id.content);

        // 롤 버튼 클릭 시 동작
        rollButton.setOnClickListener(view -> refreshAlarms());

        // 하단 네비게이션 바 클릭 이벤트
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navProgress.setOnClickListener(v -> startActivity(new Intent(this, SampleLayoutActivity.class)));
        navAlarm.setOnClickListener(v -> {/* 현재 페이지, 아무 동작 안함 */});
        navMypage.setOnClickListener(v -> startActivity(new Intent(this, MyPageActivity.class)));

        // 초기 알림 표시
        addAlarm("새로운 그룹 초대가 도착했습니다.", "5분 전");
    }

    private void refreshAlarms() {
        // 알림 초기화 후 새로 추가
        alarmContainer.removeAllViews();
        addAlarm("새로운 투표가 시작되었습니다.", "방금 전");
        addAlarm("그룹 일정이 업데이트되었습니다.", "2시간 전");
    }

    private void addAlarm(String text, String time) {
        View alarmItem = getLayoutInflater().inflate(R.layout.alarm_item, alarmContainer, false);

        TextView alarmText = alarmItem.findViewById(R.id.alarm_text);
        TextView alarmTime = alarmItem.findViewById(R.id.alarm_time);

        alarmText.setText(text);
        alarmTime.setText(time);

        alarmContainer.addView(alarmItem);
    }
}
