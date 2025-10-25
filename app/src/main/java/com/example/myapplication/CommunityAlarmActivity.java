package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;

public class CommunityAlarmActivity extends AppCompatActivity {

    private ImageButton backButton;

    private SwitchCompat switchPost;
    private SwitchCompat switchComment;
    private SwitchCompat switchLike;

    private AppCompatImageButton navHome;
    private AppCompatImageButton navGroup;
    private AppCompatImageButton navSearch;
    private AppCompatImageButton navPet;
    private AppCompatImageButton navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.community_alarm);

        backButton    = findViewById(R.id.back_button);

        switchPost    = findViewById(R.id.mission_switch_5);
        switchComment = findViewById(R.id.mission_switch_6);
        switchLike    = findViewById(R.id.mission_switch_7);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        backButton.setOnClickListener(v -> finish());

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v ->
                startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v ->
                startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v ->
                startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v ->
                startActivity(new Intent(this, MyPageMainActivity.class)));

        // (선택) 스위치 리스너 — 서버와 연동할 때 여기에 호출 붙이면 됨
        // switchPost.setOnCheckedChangeListener((btn, isChecked) -> { /* TODO */ });
        // switchComment.setOnCheckedChangeListener((btn, isChecked) -> { /* TODO */ });
        // switchLike.setOnCheckedChangeListener((btn, isChecked) -> { /* TODO */ });
    }
}
