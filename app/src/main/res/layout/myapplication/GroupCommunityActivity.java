package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class GroupCommunityActivity extends AppCompatActivity {

    ImageButton btnMypage, btnWritePost;
    ImageButton navHome, navGroup, navSearch, navAlarm, navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);

        // 상단 버튼
        btnMypage = findViewById(R.id.btn_mypage);
        btnMypage.setOnClickListener(view -> {
            Intent intent = new Intent(GroupCommunityActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });

        // 글쓰기 버튼
        btnWritePost = findViewById(R.id.btn_write_post);
        btnWritePost.setOnClickListener(view -> {
            Intent intent = new Intent(GroupCommunityActivity.this, GroupCommunityWriteActivity.class);
            startActivity(intent);
        });

        // 하단 네비게이션 버튼
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navAlarm = findViewById(R.id.nav_alarm);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(view -> {
            Intent intent = new Intent(GroupCommunityActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(view -> {
            Intent intent = new Intent(GroupCommunityActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(view -> {
            Intent intent = new Intent(GroupCommunityActivity.this, SampleLayoutActivity.class);
            startActivity(intent);
        });

        navAlarm.setOnClickListener(view -> {
            Intent intent = new Intent(GroupCommunityActivity.this, AlarmPageActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(view -> {
            Intent intent = new Intent(GroupCommunityActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });
    }
}
