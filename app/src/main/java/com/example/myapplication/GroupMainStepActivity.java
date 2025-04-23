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
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GroupMainStepActivity extends AppCompatActivity implements SensorEventListener {

    private TextView groupMainTitle;  // 그룹 이름 표시
    private TextView stepCountView;  // 걸음 수 표시
    private int totalSteps = 0;  // 걸음 수 저장 변수
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001;  // 권한 요청 코드

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_step);

        // 센서 매니저 초기화
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 걸음 수 센서 초기화
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        // 만약 센서가 없으면 알림
        if (stepCounterSensor == null) {
            Toast.makeText(this, "만보기 센서가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        // UI 연결
        groupMainTitle = findViewById(R.id.group_main_title);  // 그룹 이름
        stepCountView = findViewById(R.id.step_count_value);
        TextView groupGoalTextView = findViewById(R.id.group_goal_view);// 걸음 수

        // Intent로 전달된 그룹 이름 및 그룹 목표 받기
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");

        if (groupName != null) {
            groupMainTitle.setText(groupName);  // 그룹 이름을 텍스트뷰에 설정
        }
        if (groupGoal != null) {
            groupGoalTextView.setText(groupGoal);  // 그룹 목표 텍스트뷰에 설정
        }

        // 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 센서 리스너 등록
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 센서 리스너 해제
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // 센서 값 변경 시 호출되는 메소드
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];  // 센서로부터 받은 걸음 수
            updateStepCount();  // 걸음 수 업데이트
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도 변경 처리 (필요 시 추가)
    }

    // 걸음 수 업데이트 메소드
    private void updateStepCount() {
        stepCountView.setText("걸음 수: " + totalSteps);
    }

    // 00:00시에 자동 리셋하는 메소드
    private void resetStepsAtMidnight() {
        int currentHour = android.text.format.DateFormat.format("HH", new java.util.Date()).toString().charAt(0) - '0';  // 시간 추출
        int currentMinute = android.text.format.DateFormat.format("mm", new java.util.Date()).toString().charAt(0) - '0';  // 분 추출

        if (currentHour == 0 && currentMinute == 0) {
            totalSteps = 0;  // 걸음 수 리셋
            updateStepCount();  // UI 업데이트
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 승인됨, 센서 리스너 등록
                if (stepCounterSensor != null) {
                    sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
                }
            } else {
                Toast.makeText(this, "걸음 수 기능을 위해 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
