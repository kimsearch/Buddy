package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainStepActivity extends AppCompatActivity implements SensorEventListener {

    private TextView groupMainTitle;
    private TextView stepCountView;
    private TextView groupGoalTextView;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private PieChart pieChart;

    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    private SharedPreferences prefs;
    private int stepOffset = 0;

    private int totalSteps = 0;
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

        prefs = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        stepOffset = prefs.getInt("stepOffset", 0);

        if (!prefs.contains("stepOffset")) {
            if (stepCounterSensor != null) {
                sensorManager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        int currentSteps = (int) event.values[0];
                        prefs.edit().putInt("stepOffset", currentSteps).apply();
                        stepOffset = currentSteps;
                        sensorManager.unregisterListener(this);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
                }, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
            // stepOffset이 이미 저장되어 있는 경우 가져옴
            stepOffset = prefs.getInt("stepOffset", 0);
        }

        setMidnightResetAlarm();

        // UI 연결
        groupMainTitle = findViewById(R.id.group_main_title);
        stepCountView = findViewById(R.id.step_count_value);
        groupGoalTextView = findViewById(R.id.group_goal_view);
        pieChart = findViewById(R.id.pieChart);

        // 인텐트로부터 데이터 수신
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long groupId = intent.getLongExtra("groupId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText("그룹 목표: " + groupGoal);

        // 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
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

        // 하단 네비게이션 바
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, MainActivity.class)));

        navGroup.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, GroupPageActivity.class)));

        navMyPage.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, MyPageMainActivity.class)));
        navSearch.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v ->
                startActivity(new Intent(GroupMainStepActivity.this, PetActivity.class)));

        // PieChart 기본 설정
        setupPieChart();
        updatePieChart(0, 100); // 초기값
    }

    private void setMidnightResetAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StepResetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1); // 내일 00:00

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }

        getLastStepAndSend();
        getGoalProgressAndUpdateChart();
        loadRankingAndDrawChart();
        loadStepHistoryAndDrawChart();

    }

    private void getGoalProgressAndUpdateChart() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Long memberId = prefs.getLong("memberId", -1L);
        Long groupId = getIntent().getLongExtra("groupId", -1L);

        if (memberId == -1L || groupId == -1L) return;

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getGoalProgress(groupId, memberId).enqueue(new Callback<GoalProgressResponse>() {
            @Override
            public void onResponse(Call<GoalProgressResponse> call, Response<GoalProgressResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int record = response.body().getRecordValue();
                    int goal = response.body().getGoalValue();
                    groupGoalTextView.setText("그룹 목표: " + goal + "보");
                    updatePieChart(record, goal);
                } else {
                    Log.e("PieChart", "서버 응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GoalProgressResponse> call, Throwable t) {
                Log.e("PieChart", "네트워크 오류", t);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private void getLastStepAndSend() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    int currentSteps = (int) event.values[0];
                    int todaySteps = currentSteps - stepOffset;
                    updateStepCount(todaySteps);
                    sendStepsToServer(todaySteps);
                    sensorManager.unregisterListener(this);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            }, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            int todaySteps = totalSteps - stepOffset;
            updateStepCount(todaySteps);

            sendStepsToServer(todaySteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 생략
    }

    private void updateStepCount(int steps) {
        stepCountView.setText("걸음 수: " + steps);
    }

    private void sendStepsToServer(int todaySteps) {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Long memberId = prefs.getLong("memberId", -1L);
        Long groupId = getIntent().getLongExtra("groupId", -1L);

        if (memberId == -1L || groupId == -1L) return;

        // ✅ 오늘 날짜 기반 키 생성
        String today = java.time.LocalDate.now().toString(); // 예: "2025-07-31"
        String key = "savedStep_" + today + "_" + groupId + "_" + memberId;

        SharedPreferences stepPrefs = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        if (stepPrefs.getBoolean(key, false)) {
            Log.d("걸음 저장", "이미 오늘 저장된 기록 있음 → 서버 전송 생략");
            return;
        }

        GroupGoalLogRequest request = new GroupGoalLogRequest(groupId, memberId, todaySteps);

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.updateStepLog(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("걸음 저장", "서버에 저장 완료");
                    stepPrefs.edit().putBoolean(key, true).apply(); // ✅ 저장 완료 표시
                } else {
                    Log.e("걸음 저장", "서버 응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("걸음 저장", "네트워크 오류", t);
            }
        });
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

    private void updatePieChart(int recordValue, int goalValue) {
        float progress = Math.min(recordValue, goalValue);
        float percentage = (progress / goalValue) * 100f;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percentage, "달성"));
        entries.add(new PieEntry(100f - percentage, "남음"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#76C7C0"), Color.parseColor("#D3D3D3"));
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setCenterText(String.format("%.0f%%", percentage));
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.invalidate();
    }

    private void loadRankingAndDrawChart() {
        Long groupId = getIntent().getLongExtra("groupId", -1L);
        if (groupId == -1L) return;

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getRanking(groupId).enqueue(new Callback<List<RankingItem>>() {
            @Override
            public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    drawRankingRecyclerView(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                Log.e("랭킹", "네트워크 오류", t);
            }
        });

    }

    private void loadStepHistoryAndDrawChart() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        Long memberId = prefs.getLong("memberId", -1L);
        Long groupId = getIntent().getLongExtra("groupId", -1L);

        if (memberId == -1L || groupId == -1L) return;

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getWeeklyStepHistory(groupId, memberId).enqueue(new Callback<List<StepHistoryItem>>() {
            @Override
            public void onResponse(Call<List<StepHistoryItem>> call, Response<List<StepHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    drawHistoryBarChart(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<StepHistoryItem>> call, Throwable t) {
                Log.e("히스토리", "네트워크 오류", t);
            }
        });
    }

    private void drawHistoryBarChart(List<StepHistoryItem> historyList) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < historyList.size(); i++) {
            StepHistoryItem item = historyList.get(i);
            entries.add(new BarEntry(i, item.getStepCount()));
            labels.add(item.getDate().substring(5)); // "MM-dd" 포맷
        }

        BarDataSet dataSet = new BarDataSet(entries, "일별 걸음 수");
        dataSet.setColor(Color.parseColor("#76C7C0"));
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        BarChart chart = findViewById(R.id.barChart);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setFitBars(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);

        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);

        chart.invalidate();
    }


    private void drawRankingRecyclerView(List<RankingItem> rankingList) {
        // 랭킹 중 가장 높은 수치로 정규화된 progress bar 설정
        int maxSuccess = rankingList.stream().mapToInt(RankingItem::getSuccessCount).max().orElse(1);

        for (RankingItem item : rankingList) {
            item.setProgress((int) ((item.getSuccessCount() * 100.0) / maxSuccess));
            item.setProfileResId(R.drawable.ic_profile); // 기본 이미지 (커스터마이징 가능)
        }

        RecyclerView rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RankingAdapter adapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setAdapter(adapter);
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