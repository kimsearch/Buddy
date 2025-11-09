package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashMainActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 4000; // 4초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // 4초 후에 MainActivity로 이동
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // SplashActivity를 백스택에서 제거
        }, SPLASH_TIME_OUT);
    }
}
