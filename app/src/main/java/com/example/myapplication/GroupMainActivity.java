package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.*;

public class GroupMainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView groupMainTitle;
    private TextView groupGoalView;
    private EditText goalInputEditText;
    private Button goalInputButton;

    private AppCompatImageButton navHome, navGroup,navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailyProgressMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int totalSteps = 0;
    private final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long groupId = intent.getLongExtra("groupId", -1L);
        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalView.setText(groupGoal);

        // 알림 버튼
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        notificationButton1.setOnClickListener(v -> {
            Intent intent1 = new Intent(GroupMainActivity.this, GroupMemberActivity.class);
            intent1.putExtra("groupId", groupId);
            startActivity(intent1);
        });

        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, GroupCommunityActivity.class)));

        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainActivity.this, AlarmPageActivity.class)));

        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        updatePieChart(0f);
        if (dailyProgressMap.isEmpty()) {
            dailyProgressMap.put(getTodayDate(), 0f);
        }
        updateBarChart();

        goalInputButton.setOnClickListener(v -> {
            String inputText = goalInputEditText.getText().toString().trim();
            if (!inputText.isEmpty()) {
                try {
                    float value = Float.parseFloat(inputText);
                    float percentage = Math.min(100f, (value / 10000f) * 100f);
                    updatePieChart(percentage);
                    String today = getTodayDate();
                    dailyProgressMap.put(today, percentage);

                    updateBarChart();
                    Toast.makeText(this, "기록 완료: " + value, Toast.LENGTH_SHORT).show();
                    goalInputEditText.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "숫자를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "목표를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(GroupMainActivity.this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(GroupMainActivity.this, PetActivity.class)));
    }

    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value, "달성"));
        entries.add(new PieEntry(100 - value, "남은 목표"));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(value >= 80 ? Color.rgb(76, 175, 80) : value >= 50 ? Color.rgb(255, 193, 7) : Color.rgb(244, 67, 54), Color.LTGRAY);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Float> entry : dailyProgressMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "누적 기록");
        dataSet.setColors(Color.parseColor("#FF9800"));
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);
        data.setValueTextSize(12f);

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setDrawGridLines(false);

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(100f);
        barChart.invalidate();
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
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
            float percentage = Math.min(100f, (totalSteps / 10000f) * 100f);
            updatePieChart(percentage);
            dailyProgressMap.put(getTodayDate(), percentage);
            updateBarChart();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
