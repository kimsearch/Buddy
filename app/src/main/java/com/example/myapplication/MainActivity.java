package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageButton alarmButton, calendarButton;
    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    //HomeGroupAdapter 사용해서 그룹 목록 보여줌
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

        Long myMemberId = prefs.getLong("memberId", -1L);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_home_group_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getMyGroups(myMemberId).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(@NonNull Call<List<Group>> call, @NonNull Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Group> groupList = response.body();
                    HomeGroupAdapter adapter = new HomeGroupAdapter(MainActivity.this, groupList, myMemberId);
                    recyclerView.setAdapter(adapter);
                    fetchAndBindProgressPerGroup(groupList, adapter, myMemberId);
                } else {
                    Toast.makeText(MainActivity.this, "그룹 데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Group>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 로그인 되어 있으면 정상적으로 화면 구성

        alarmButton = findViewById(R.id.notification_button);
        calendarButton = findViewById(R.id.calendar);
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
    private void fetchAndBindProgressPerGroup(List<Group> groups, HomeGroupAdapter adapter, long myMemberId) {
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        for (int i = 0; i < groups.size(); i++) {
            final int pos = i;
            final Group g = groups.get(i);

            api.getGoalProgress(g.getId(), myMemberId).enqueue(new Callback<GoalProgressResponse>() {
                @Override
                public void onResponse(Call<GoalProgressResponse> call, Response<GoalProgressResponse> res) {
                    if (!res.isSuccessful() || res.body() == null) return;

                    int goal   = res.body().getGoalValue();
                    int record = res.body().getRecordValue();

                    // 부수입: record/goal*100, 가계부/목표권수 등 성공/실패형: 백엔드가 goal=1, record=0/1로 내려줌
                    float percent = (goal <= 0) ? 0f : Math.min(100f, (record * 100f) / (float) goal);

                    g.setProgressPercent(percent);
                }
                @Override public void onFailure(Call<GoalProgressResponse> call, Throwable t) { /* no-op */ }
            });
        }
    }

}
