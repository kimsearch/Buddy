package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class GroupMainStepActivity extends AppCompatActivity implements SensorEventListener {

    private TextView groupMainTitle;
    private TextView stepCountView;
    private TextView groupGoalTextView;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private PieChart pieChart;

    private int totalSteps = 0;
    private final int GOAL_STEPS = 10000; // 목표 걸음 수 (예시)
    private final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001;

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
        groupGoalTextView = findViewById(R.id.group_goal_view);
        pieChart = findViewById(R.id.pieChart);

        // 인텐트로부터 그룹 이름/목표 수신
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText("그룹 목표: " + groupGoal);

        // 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
        }

        // PieChart 기본 설정
        setupPieChart();
        updatePieChart(0); // 초기값
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
            updatePieChart(totalSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 생략
    }

    private void updateStepCount() {
        stepCountView.setText("걸음 수: " + totalSteps);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(58f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
    }

    private void updatePieChart(int steps) {
        float progress = Math.min(steps, GOAL_STEPS);
        float percentage = (progress / GOAL_STEPS) * 100f;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percentage, "달성"));
        entries.add(new PieEntry(100f - percentage, "남음"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#76C7C0"), Color.parseColor("#D3D3D3"));
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
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