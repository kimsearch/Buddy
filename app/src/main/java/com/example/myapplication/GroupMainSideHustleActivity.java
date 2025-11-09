package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
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

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainSideHustleActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalTextView;
    private EditText goalInputEditText;
    private Button goalInputButton;
    private TextView sideHustleView;

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailyProgressMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_side_hustle);

        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalTextView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        sideHustleView = findViewById(R.id.side_hustle_value);
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
        Long memberId = getSharedPreferences("loginPrefs", MODE_PRIVATE).getLong("memberId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText("그룹 목표: " + groupGoal);

        // 차트 기본 세팅
        setupPieChart();

        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        // 알림 버튼
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        notificationButton1.setOnClickListener(v -> {
            Intent intent1 = new Intent(GroupMainSideHustleActivity.this, GroupMemberActivity.class);
            intent1.putExtra("groupId", groupId);
            startActivity(intent1);
        });

        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(GroupMainSideHustleActivity.this, GroupCommunityActivity.class)));

        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(GroupMainSideHustleActivity.this, AlarmPageActivity.class)));

        // 로컬 바차트 초기화 (선택)
        if (dailyProgressMap.isEmpty()) {
            dailyProgressMap.put(getTodayDate(), 0f);
        }
        updateBarChart();

        // 목표 입력 버튼
        goalInputButton.setOnClickListener(v -> {
            String inputText = goalInputEditText.getText().toString().trim();
            if (!inputText.isEmpty()) {
                try {
                    float value = Float.parseFloat(inputText);

                    // 바차트용 로컬 히스토리만 갱신 (원형차트는 서버값으로만 갱신)
                    float percentageForHistory = Math.min(100f, (value / 10000f) * 100f);
                    dailyProgressMap.put(getTodayDate(), percentageForHistory);
                    updateBarChart();

                    Toast.makeText(this, "기록 완료: " + (int)value + "원", Toast.LENGTH_SHORT).show();
                    goalInputEditText.setText("");

                    IncomeLogRequest request = new IncomeLogRequest(groupId, memberId, (int) value);
                    Retrofit_interface apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);
                    apiService.updateIncome(request).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "서버 저장 완료", Toast.LENGTH_SHORT).show();
                                // ✅ 서버 반영 후, 서버값으로 PieChart/랭킹 다시 로드
                                getGoalProgressAndUpdateChart();
                                loadRankingAndDrawChart();
                            } else {
                                Toast.makeText(getApplicationContext(), "서버 저장 실패", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
                        }
                    });

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
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면 복귀 시 서버 기준으로 항상 동기화
        getGoalProgressAndUpdateChart();
        loadRankingAndDrawChart();
        loadRecordHistoryAndDrawChart();
    }

    private void updateIncomeTotal(int amount) {
        sideHustleView.setText("내 부수입: " + amount + "원");
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
                    updateIncomeTotal(record);
                    groupGoalTextView.setText("그룹 목표: " + goal + "원");
                    updatePieChart(record, goal); // ✅ 서버값으로만 갱신
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
        float percentage = (goalValue > 0) ? (progress * 100f / goalValue) : 0f;

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

    private void loadRecordHistoryAndDrawChart() {
                 SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                 Long memberId = prefs.getLong("memberId", -1L);
                 Long groupId = getIntent().getLongExtra("groupId", -1L);

                 if (memberId == -1L || groupId == -1L) return;

                 Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

                 // 1번 단계에서 새로 추가한 API 메서드를 호출합니다.
                 api.getWeeklyRecordHistory(groupId, memberId).enqueue(new Callback<List<StepHistoryItem>>() {
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
                         // Y축 값: recordValue, X축 위치: i
                         entries.add(new BarEntry(i, item.getStepCount())); // StepHistoryItem의 getStepCount()가 recordValue를 담고 있음
                         // X축 라벨: cycleStart 날짜
                         labels.add(item.getDate().toString().substring(5)); // StepHistoryItem의 getDate()가 cycleStart를 담고 있음
                     }

                 BarDataSet dataSet = new BarDataSet(entries, "주기별 기록");
                 dataSet.setColor(Color.parseColor("#76C7C0"));
                 dataSet.setValueTextSize(12f);

                 BarData data = new BarData(dataSet);
                 data.setBarWidth(0.9f);

                 // 2번 단계에서 XML에 추가한 barChart ID
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

                 chart.invalidate(); // 차트 새로고침
             }

    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Float> entry : dailyProgressMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }
        BarDataSet dataSet = new BarDataSet(entries, "누적 기록(로컬)");
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
        return new SimpleDateFormat("MM/dd", Locale.getDefault()).format(new Date());
    }

    private void drawRankingRecyclerView(List<RankingItem> rankingList) {
        int maxSuccess = rankingList.stream().mapToInt(RankingItem::getSuccessCount).max().orElse(1);
        for (RankingItem item : rankingList) {
            item.setProgress((int) ((item.getSuccessCount() * 100.0) / maxSuccess));
            item.setProfileResId(R.drawable.ic_profile);
        }
        RecyclerView rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RankingAdapter adapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setAdapter(adapter);
    }
}
