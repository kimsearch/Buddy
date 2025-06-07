package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView goalPeriodText;
    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private List<CalendarGoalItem> goalItemList = new ArrayList<>();
    private Long myMemberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        // 🔹 로그인한 사용자 ID 가져오기
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        myMemberId = prefs.getLong("memberId", -1L);

        calendarView = findViewById(R.id.calendar);
        goalPeriodText = findViewById(R.id.calendar_goal).findViewById(R.id.goal_date_text);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // ✅ 서버에서 목표 로딩
        loadCalendarGoals(myMemberId);
        LocalDate initialDate = LocalDate.ofEpochDay(calendarView.getDate() / (24 * 60 * 60 * 1000));
        updateGoalPeriodText(initialDate);

        // ✅ 이후 유저가 날짜 클릭 시에도 출력
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            updateGoalPeriodText(selectedDate);
        });
    }

    // ✅ 선택한 날짜에 해당하는 목표 표시
    private void updateGoalPeriodText(LocalDate selectedDate) {
        StringBuilder result = new StringBuilder();

        for (CalendarGoalItem item : goalItemList) {
            LocalDate[] period = item.getPeriodForDate(selectedDate);
            if (period == null) continue;

            LocalDate periodStart = period[0];
            LocalDate periodEnd = period[1];

            if (selectedDate.equals(periodStart)) {
                result.append("🟢 ")
                        .append(item.title)
                        .append(" 시작일 (")
                        .append(periodStart)
                        .append(" ~ ")
                        .append(periodEnd)
                        .append(")\n");
            } else if (selectedDate.equals(periodEnd)) {
                result.append("🔴 ")
                        .append(item.title)
                        .append(" 종료일 (")
                        .append(periodStart)
                        .append(" ~ ")
                        .append(periodEnd)
                        .append(")\n");
            } else {
                result.append("🔵 ")
                        .append(item.title)
                        .append(" 진행 중 (")
                        .append(periodStart)
                        .append(" ~ ")
                        .append(periodEnd)
                        .append(")\n");
            }
        }

        if (result.length() == 0) {
            goalPeriodText.setText("이 날의 목표가 없습니다");
        } else {
            goalPeriodText.setText(result.toString().trim());
        }
    }

    // ✅ 서버에서 목표 데이터 가져오기
    private void loadCalendarGoals(Long memberId) {
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getCalendarGoals(memberId).enqueue(new Callback<List<CalendarGoalItem>>() {
            @Override
            public void onResponse(Call<List<CalendarGoalItem>> call, Response<List<CalendarGoalItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    goalItemList.clear();
                    goalItemList.addAll(response.body());
                    Log.d("캘린더", "목표 로딩 완료: " + goalItemList.size() + "개");

                    long selectedDateMillis = calendarView.getDate();
                    LocalDate selectedDate = java.time.ZonedDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(selectedDateMillis),
                            java.time.ZoneId.systemDefault()
                    ).toLocalDate();

                    updateGoalPeriodText(selectedDate);
                } else {
                    Toast.makeText(CalendarActivity.this, "목표 로딩 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CalendarGoalItem>> call, Throwable t) {
                Toast.makeText(CalendarActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    }

