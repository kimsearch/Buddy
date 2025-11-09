package com.example.myapplication;

import android.content.Intent;
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


    private TextView groupMainTitle;
    private TextView groupGoalView;
    private EditText goalInputEditText;
    private Button goalInputButton;

    private AppCompatImageButton navHome, navGroup,navSearch, navPet, navMyPage;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_burned);

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
        if (groupName != null) groupMainTitle.setText(groupName);

        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        updatePieChart(0f);
        updateBarChart();

        goalInputButton.setOnClickListener(v -> {
            String inputText = goalInputEditText.getText().toString().trim();
            if (!inputText.isEmpty()) {
                try {

                    updateBarChart();
                    goalInputEditText.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "숫자를 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
            }
        });

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
    }

    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

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
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

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
