// AlarmPageActivity.java
package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmPageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private TextView emptyMessageTextView;
    private List<Alarm> alarmList;
    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_page);

        recyclerView = findViewById(R.id.alarm_recycler_view);
        emptyMessageTextView = findViewById(R.id.empty_message);
        alarmList = new ArrayList<>();
        alarmAdapter = new AlarmAdapter(alarmList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alarmAdapter);


        loadAlarms();
    }


    private void loadAlarms() {
        Long memberId = getSharedPreferences("loginPrefs", MODE_PRIVATE).getLong("memberId", -1L);

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getNotifications(memberId).enqueue(new Callback<List<Alarm>>() {
            @Override
            public void onResponse(Call<List<Alarm>> call, Response<List<Alarm>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    alarmList.clear();
                    alarmList.addAll(response.body());
                    alarmAdapter.notifyDataSetChanged();
                    updateEmptyView();
                }
            }

            @Override
            public void onFailure(Call<List<Alarm>> call, Throwable t) {
                Toast.makeText(AlarmPageActivity.this, "알림을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        navPet = findViewById(R.id.nav_pet);
        navSearch = findViewById(R.id.nav_search);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(AlarmPageActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(AlarmPageActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(AlarmPageActivity.this, GroupSearchPageActivity.class);
            startActivity(intent);
        });

        navPet.setOnClickListener(v -> {
            Intent intent = new Intent(AlarmPageActivity.this, PetActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(v -> {
            Intent intent = new Intent(AlarmPageActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });
    }


    private void updateEmptyView() {
        if (alarmList.isEmpty()) {
            emptyMessageTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessageTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
