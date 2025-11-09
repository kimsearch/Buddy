package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupMainBurnedActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalView;
    private EditText goalInputEditText;
    private Button goalInputButton;

    private TextView tvBurnGoal, tvBurnProgressPercent, tvBurnedAmount, tvBurnRemain;
    private ProgressBar burnProgress;

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailyPercentMap = new LinkedHashMap<>();
    private final Map<String, Float> dailyKcalMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private float targetKcal = 500f; // 하루 목표 소모 칼로리 (기본값)
    private final NumberFormat comma = NumberFormat.getInstance(Locale.getDefault());
    private final DecimalFormat oneDecimal = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_burned);

        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);

        tvBurnGoal = findViewById(R.id.tv_burn_goal);
        tvBurnProgressPercent = findViewById(R.id.tv_burn_progress_percent);
        burnProgress = findViewById(R.id.burn_progress);
        tvBurnedAmount = findViewById(R.id.tv_burned_amount);
        tvBurnRemain = findViewById(R.id.tv_burn_remain);

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
        String burnGoal = intent.getStringExtra("burnGoal");
        if (groupName != null) groupMainTitle.setText(groupName);
        if (burnGoal != null) {
            try {
                targetKcal = Float.parseFloat(burnGoal);
            } catch (NumberFormatException ignored) {}
        }

        groupGoalView.setText("일일 운동 목표: " + comma.format((long) targetKcal) + " kcal");

        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        String today = getTodayDate();
        dailyPercentMap.put(today, 0f);
        dailyKcalMap.put(today, 0f);

        updateSummary();
        updatePieChart(0f);
        updateBarChart();

        goalInputButton.setOnClickListener(v -> {
            String inputText = goalInputEditText.getText().toString().trim();
            if (!inputText.isEmpty()) {
                try {
                    float burned = Float.parseFloat(inputText);
                    String todayDate = getTodayDate();

                    float newTotal = dailyKcalMap.containsKey(todayDate)
                            ? dailyKcalMap.get(todayDate) + burned
                            : burned;

                    dailyKcalMap.put(todayDate, newTotal);
                    float percent = Math.min(100f, (newTotal / targetKcal) * 100f);
                    dailyPercentMap.put(todayDate, percent);

                    updateSummary();
                    updatePieChart(percent);
                    updateBarChart();

                    Toast.makeText(this, "기록 완료: " + comma.format((long) burned) + " kcal 소모", Toast.LENGTH_SHORT).show();
                    goalInputEditText.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "숫자를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "운동으로 소모한 칼로리를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
    }

    private void updateSummary() {
        String today = getTodayDate();
        float burned = dailyKcalMap.containsKey(today) ? dailyKcalMap.get(today) : 0f;
        float percent = Math.min(100f, (burned / targetKcal) * 100f);
        float remain = Math.max(0f, targetKcal - burned);

        tvBurnGoal.setText("목표 " + comma.format((long) targetKcal) + " kcal");
        tvBurnedAmount.setText("누적 " + comma.format((long) burned) + " kcal");
        tvBurnRemain.setText("남은 " + comma.format((long) remain) + " kcal");
        tvBurnProgressPercent.setText(oneDecimal.format(percent) + "%");
        burnProgress.setProgress((int) percent);

        groupGoalView.setText("일일 운동 목표: " + comma.format((long) targetKcal)
                + " kcal · 누적 " + comma.format((long) burned)
                + " kcal · 달성 " + oneDecimal.format(percent) + "%");
    }

    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value, "소모"));
        entries.add(new PieEntry(100 - value, "남은"));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(
                value >= 80 ? Color.rgb(33, 150, 243)
                        : value >= 50 ? Color.rgb(100, 181, 246)
                        : Color.rgb(187, 222, 251),
                Color.LTGRAY
        );

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
        for (Map.Entry<String, Float> entry : dailyPercentMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "소모율");
        dataSet.setColors(Color.parseColor("#64B5F6"));
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
}
