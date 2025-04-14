package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class MyPageSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_setting);

        // 1. 이메일 찾기 버튼
        Button emailButton = findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, EmailFindMainActivity.class);
                startActivity(intent);
            }
        });

        // 2. 홈 버튼
        AppCompatImageButton homeBtn = findViewById(R.id.nav_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // 3. 그룹 버튼
        AppCompatImageButton groupBtn = findViewById(R.id.nav_group);
        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, GroupPageActivity.class);
                startActivity(intent);
            }
        });

        // 4. 마이페이지 버튼
        AppCompatImageButton mypageBtn = findViewById(R.id.nav_mypage);
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, MyPageMainActivity.class);
                startActivity(intent);
            }
        });

        // ✅ 5. 로그아웃 버튼
        Button logoutButton = findViewById(R.id.logout_button); // 로그아웃 버튼 ID 확인!
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 정보 삭제
                SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                prefs.edit().remove("userEmail").apply();

                // 로그인 페이지로 이동
                Intent intent = new Intent(MyPageSettingActivity.this, LoginPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 백스택 제거
                startActivity(intent);
                finish();
            }
        });

        // 6. 회원 탈퇴 버튼
        Button deleteIdButton = findViewById(R.id.delete_id_button);
        deleteIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageSettingActivity.this, AppDeleteActivity.class);
                startActivity(intent);
            }
        });

    }
}
