package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;

public class MissionAlarmActivity extends AppCompatActivity {

    private ImageButton backButton;

    private SwitchCompat missionSwitch1;

    private AppCompatImageButton navHome;
    private AppCompatImageButton navGroup;
    private AppCompatImageButton navSearch;
    private AppCompatImageButton navPet;
    private AppCompatImageButton navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_alarm);

        backButton     = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
        missionSwitch1 = findViewById(R.id.mission_switch_1);

        navHome  = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch= findViewById(R.id.nav_search);
        navPet   = findViewById(R.id.nav_pet);
        navMyPage= findViewById(R.id.nav_mypage);

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MyPageSettingsActivity.class));
            finish();
        });

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

        // missionSwitch1.setOnCheckedChangeListener((buttonView, isChecked) -> { ... });
    }

}
