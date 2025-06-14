package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); //
        });

        calendarView = findViewById(R.id.calendar);
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
            Toast.makeText(CalendarActivity.this, "선택한 날짜: " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navPet.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, PetActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });
    }
}
