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
import java.util.*;

public class GroupMainIntakeActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalView;
    private EditText goalInputEditText;
    private Button goalInputButton;

    private TextView tvIntakeGoal, tvIntakeProgressPercent, tvIntakeConsumed, tvIntakeRemain;
    private ProgressBar intakeProgress;

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailyPercentMap = new LinkedHashMap<>();
    private final Map<String, Float> dailyKcalMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private float targetKcal = 2000f;
    private final NumberFormat comma = NumberFormat.getInstance(Locale.getDefault());
    private final DecimalFormat oneDecimal = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_intake);

        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);

        tvIntakeGoal = findViewById(R.id.tv_intake_goal);
        tvIntakeProgressPercent = findViewById(R.id.tv_intake_progress_percent);
        intakeProgress = findViewById(R.id.intake_progress);
        tvIntakeConsumed = findViewById(R.id.tv_intake_consumed);
        tvIntakeRemain = findViewById(R.id.tv_intake_remain);

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
        String intakeGoal = intent.getStringExtra("intakeGoal");
        if (groupName != null) groupMainTitle.setText(groupName);
        if (intakeGoal != null) {
            try {
                targetKcal = Float.parseFloat(intakeGoal);
            } catch (NumberFormatException ignored) {}
        }

        groupGoalView.setText("일일 섭취 목표: " + comma.format((long) targetKcal) + " kcal");

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
                    float kcal = Float.parseFloat(inputText);
                    String todayDate = getTodayDate();

                    float newTotal = dailyKcalMap.containsKey(todayDate)
                            ? dailyKcalMap.get(todayDate) + kcal
                            : kcal;

                    dailyKcalMap.put(todayDate, newTotal);
                    float percent = Math.min(100f, (newTotal / targetKcal) * 100f);
                    dailyPercentMap.put(todayDate, percent);

                    updateSummary();
                    updatePieChart(percent);
                    updateBarChart();

                    Toast.makeText(this, "기록 완료: " + comma.format((long) kcal) + " kcal", Toast.LENGTH_SHORT).show();
                    goalInputEditText.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "숫자를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "섭취 칼로리를 입력하세요.", Toast.LENGTH_SHORT).show();
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
        float consumed = dailyKcalMap.containsKey(today) ? dailyKcalMap.get(today) : 0f;
        float percent = Math.min(100f, (consumed / targetKcal) * 100f);
        float remain = Math.max(0f, targetKcal - consumed);

        tvIntakeGoal.setText("목표 " + comma.format((long) targetKcal) + " kcal");
        tvIntakeConsumed.setText("누적 " + comma.format((long) consumed) + " kcal");
        tvIntakeRemain.setText("남은 " + comma.format((long) remain) + " kcal");
        tvIntakeProgressPercent.setText(oneDecimal.format(percent) + "%");
        intakeProgress.setProgress((int) percent);

        groupGoalView.setText("일일 섭취 목표: " + comma.format((long) targetKcal)
                + " kcal · 누적 " + comma.format((long) consumed)
                + " kcal · 달성 " + oneDecimal.format(percent) + "%");
    }

    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value, "섭취"));
        entries.add(new PieEntry(100 - value, "남은"));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(
                value >= 80 ? Color.rgb(76, 175, 80)
                        : value >= 50 ? Color.rgb(255, 193, 7)
                        : Color.rgb(244, 67, 54),
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

        BarDataSet dataSet = new BarDataSet(entries, "섭취율");
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
}
