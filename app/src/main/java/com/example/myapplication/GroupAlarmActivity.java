package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;

public class GroupAlarmActivity extends AppCompatActivity {

    private ImageButton backButton;

    private SwitchCompat switchRequest;
    private SwitchCompat switchAccept;
    private SwitchCompat switchReject;

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.group_alarm);

        backButton   = findViewById(R.id.back_button);

        switchRequest = findViewById(R.id.mission_switch_2);
        switchAccept  = findViewById(R.id.mission_switch_3);
        switchReject  = findViewById(R.id.mission_switch_4);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        backButton.setOnClickListener(v -> finish());

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // (선택) 스위치 상태 변경 시 서버 호출 붙일 곳
        // switchRequest.setOnCheckedChangeListener((btn, isChecked) -> { /* TODO */ });
        // switchAccept.setOnCheckedChangeListener((btn, isChecked) -> { /* TODO */ });
        // switchReject.setOnCheckedChangeListener((btn, isChecked) -> { /* TODO */ });
    }
}
