package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GroupMainStepActivity extends AppCompatActivity implements SensorEventListener {

    private TextView groupMainTitle;  // 그룹 이름 표시
    private TextView stepCountView;   // 걸음 수 표시
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int totalSteps = 0;

    private final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001;

    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_step);

        // 센서 초기화
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        if (stepCounterSensor == null) {
            Toast.makeText(this, "만보기 센서가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // UI 연결
        groupMainTitle = findViewById(R.id.group_main_title);
        stepCountView = findViewById(R.id.step_count_value);
        TextView groupGoalTextView = findViewById(R.id.group_goal_view);

        // 인텐트로 전달받은 데이터
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long groupId = intent.getLongExtra("groupId", -1L);


        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText("그룹 목표: " + groupGoal);

        // 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
        }

        // 알림 버튼
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        notificationButton1.setOnClickListener(v -> {
            Intent intent1 = new Intent(GroupMainStepActivity.this, GroupMemberActivity.class);
            intent1.putExtra("groupId", groupId);
            startActivity(intent1);
        });

        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, GroupCommunityActivity.class)));

        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, AlarmPageActivity.class)));

        // 하단 네비게이션
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, MainActivity.class)));

        navGroup.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, GroupPageActivity.class)));

        navSearch.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, GroupSearchPageActivity.class)));

        navPet.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, PetActivity.class)));
        navMyPage.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, MyPageMainActivity.class)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            updateStepCount();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 생략
    }

    private void updateStepCount() {
        stepCountView.setText("걸음 수: " + totalSteps);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (stepCounterSensor != null) {
                    sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
                }
            } else {
                Toast.makeText(this, "걸음 수 기능을 위해 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
