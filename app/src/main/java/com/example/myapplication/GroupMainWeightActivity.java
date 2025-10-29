package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.WeightRecordModel;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainWeightActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalView;
    private EditText goalInputEditText;
    private Button goalInputButton;

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailyWeightMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_weight);

        // ✅ 로그인 정보 확인
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        long memberId = prefs.getLong("memberId", -1L);
        if (memberId == -1L) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ 뷰 연결
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        // ✅ RecyclerView 세팅
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        // ✅ 초기 차트 세팅
        updatePieChart(0f);
        dailyWeightMap.put(getTodayDate(), 0f);
        updateBarChart();

        // ✅ 몸무게 입력 버튼 클릭 이벤트
        goalInputButton.setOnClickListener(v -> {
            String inputText = goalInputEditText.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(this, "몸무게를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float newWeight = Float.parseFloat(inputText);
                String today = getTodayDate(); // "yyyy-MM-dd"

                // ✅ 1. 로컬 업데이트
                dailyWeightMap.put(today, newWeight);
                updatePieChart(100f);
                updateBarChart();

                // ✅ 2. 서버 전송 (Retrofit)
                WeightRecordModel record = new WeightRecordModel(memberId, newWeight, today);
                Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

                api.saveWeight(record).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(GroupMainWeightActivity.this,
                                    "몸무게 기록 완료: " + newWeight + "kg",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GroupMainWeightActivity.this,
                                    "서버 응답 오류 (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(GroupMainWeightActivity.this,
                                "서버 연결 실패: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

                goalInputEditText.setText("");

            } catch (NumberFormatException e) {
                Toast.makeText(this, "숫자를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ 네비게이션 버튼들
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
    }

    // ✅ 파이 차트 업데이트
    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value, "달성"));
        entries.add(new PieEntry(100 - value, "남은 목표"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(Color.rgb(76, 175, 80), Color.LTGRAY);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    // ✅ 바 차트 업데이트
    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> entry : dailyWeightMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "몸무게 기록");
        dataSet.setColor(Color.parseColor("#4CAF50"));
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
        barChart.invalidate();
    }

    // ✅ 날짜 포맷 ("yyyy-MM-dd")
    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}
