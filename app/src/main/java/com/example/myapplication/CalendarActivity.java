package com.example.myapplication;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_goal_calender); //

        calendarView = findViewById(R.id.calendar_view);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // 월(month)은 0부터 시작하므로 +1 해줘야 함
            String selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
            Toast.makeText(CalendarActivity.this, "선택한 날짜: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        // 하단 내비게이션 바나 버튼 연결도 여기서 가능
    }
}
