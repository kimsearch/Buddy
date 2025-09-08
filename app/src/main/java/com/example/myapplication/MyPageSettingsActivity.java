package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class MyPageSettingsActivity extends AppCompatActivity {

    private AppCompatImageButton backButton;
    private AppCompatImageButton btnMissionAlarm;
    private AppCompatImageButton btnGroupAlarm;
    private AppCompatImageButton btnCommunityAlarm;
    private AppCompatImageButton btnAccountInformation;

    private AppCompatImageButton navHome;
    private AppCompatImageButton navGroup;
    private AppCompatImageButton navSearch;
    private AppCompatImageButton navPet;
    private AppCompatImageButton navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_settings);

        backButton           = findViewById(R.id.back_button);
        btnMissionAlarm      = findViewById(R.id.btn_mission_alarm);
        btnGroupAlarm        = findViewById(R.id.btn_group_alarm);
        btnCommunityAlarm    = findViewById(R.id.btn_community_alarm);
        btnAccountInformation= findViewById(R.id.btn_account_information);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MyPageMainActivity.class));
            finish();
        });

        btnMissionAlarm.setOnClickListener(v ->
                startActivity(new Intent(this, MissionAlarmActivity.class))
        );

        btnGroupAlarm.setOnClickListener(v ->
                startActivity(new Intent(this, GroupAlarmActivity.class))
        );

        btnCommunityAlarm.setOnClickListener(v ->
                startActivity(new Intent(this, CommunityAlarmActivity.class))
        );

        btnAccountInformation.setOnClickListener(v ->
                startActivity(new Intent(this, AccountManagementActivity.class))
        );

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class))
        );

        navGroup.setOnClickListener(v ->
                startActivity(new Intent(this, GroupPageActivity.class))
        );

        navSearch.setOnClickListener(v ->
                startActivity(new Intent(this, GroupSearchPageActivity.class))
        );

        navPet.setOnClickListener(v ->
                startActivity(new Intent(this, PetActivity.class))
        );

        navMyPage.setOnClickListener(v ->
                startActivity(new Intent(this, MyPageMainActivity.class))
        );
    }
}
