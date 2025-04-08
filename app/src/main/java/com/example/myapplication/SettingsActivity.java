package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout notificationSetting, logoutSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        notificationSetting = findViewById(R.id.setting_notifications);
        logoutSetting = findViewById(R.id.setting_logout);

        notificationSetting.setOnClickListener(v ->
                Toast.makeText(this, "알림 설정 클릭됨", Toast.LENGTH_SHORT).show()
        );

        logoutSetting.setOnClickListener(v ->
                Toast.makeText(this, "로그아웃 기능 준비 중", Toast.LENGTH_SHORT).show()
        );
    }
}
