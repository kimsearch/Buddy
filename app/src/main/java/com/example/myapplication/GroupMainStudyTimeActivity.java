package com.example.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupMainStudyTimeActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalView;

    private TextView tvStartTime, tvEndTime;
    private View underlineStart, underlineEnd;

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;

    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailyProgressMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private int startHour = 0, startMinute = 0;
    private int endHour   = 0, endMinute   = 0;
    private static final String[] MINUTE_STEPS = {"00", "10", "20", "30", "40", "50"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_study_time);

        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView  = findViewById(R.id.group_goal_view);

        tvStartTime    = findViewById(R.id.tv_start_time);
        tvEndTime      = findViewById(R.id.tv_end_time);
        underlineStart = findViewById(R.id.underline_start);
        underlineEnd   = findViewById(R.id.underline_end);

        barChart            = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);

        navHome  = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch= findViewById(R.id.nav_search);
        navPet   = findViewById(R.id.nav_pet);
        navMyPage= findViewById(R.id.nav_mypage);

        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);
        backButton          = findViewById(R.id.back_button);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        Long groupId     = intent.getLongExtra("groupId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalView.setText(groupGoal);

        backButton.setOnClickListener(v -> finish());

        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(this, GroupCommunityActivity.class)));
        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(this, AlarmPageActivity.class)));

        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        if (dailyProgressMap.isEmpty()) {
            dailyProgressMap.put(getTodayDate(), 0f);
        }
        updateBarChart();

        updateTimeLabels();
        setUnderlineActive(true);

        tvStartTime.setOnClickListener(v -> {
            setUnderlineActive(true);
            showIntervalTimePicker(true);
        });

        tvEndTime.setOnClickListener(v -> {
            setUnderlineActive(false);
            showIntervalTimePicker(false);
        });
    }

    private void showIntervalTimePicker(boolean isStart) {
        final int hour   = isStart ? startHour   : endHour;
        final int minute = isStart ? startMinute : endMinute;

        TimePickerDialog dlg = new TimePickerDialog(
                this,
                (TimePicker view, int h, int mFromPicker) -> {
                    int realMinute = clampToStep(mFromPicker);
                    if (isStart) {
                        startHour = h;
                        startMinute = realMinute;
                    } else {
                        endHour = h;
                        endMinute = realMinute;
                    }
                    updateTimeLabels();
                    setUnderlineActive(isStart);
                },
                hour,
                minute,
                true
        );

        dlg.setOnShowListener(d -> {
            try {
                TimePicker tp = dlg.findViewById(
                        Resources.getSystem().getIdentifier("timePicker", "id", "android")
                );
                if (tp == null) return;

                NumberPicker minutePicker = tp.findViewById(
                        Resources.getSystem().getIdentifier("minute", "id", "android")
                );
                if (minutePicker == null) return;

                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(MINUTE_STEPS.length - 1);
                minutePicker.setDisplayedValues(null);
                minutePicker.setDisplayedValues(MINUTE_STEPS);
                minutePicker.setWrapSelectorWheel(true);

                int current = (isStart ? startMinute : endMinute) / 10;
                minutePicker.setValue(current);
            } catch (Exception ignore) {

            }
        });

        dlg.show();
    }

    private int clampToStep(int minuteValueFromPicker) {
        if (minuteValueFromPicker >= 0 && minuteValueFromPicker <= 5) {
            return minuteValueFromPicker * 10;
        } else {
            int rounded = Math.round(minuteValueFromPicker / 10f) * 10;
            if (rounded == 60) rounded = 50;
            return rounded;
        }
    }

    private void updateTimeLabels() {
        tvStartTime.setText(String.format(Locale.KOREAN, "%02d:%02d", startHour, startMinute));
        tvEndTime.setText(String.format(Locale.KOREAN, "%02d:%02d", endHour, endMinute));
    }

    private void setUnderlineActive(boolean startActive) {
        // 활성: 파랑(#2962FF), 비활성: 회색(#E0E0E0)
        underlineStart.setBackgroundColor(startActive ? 0xFF2962FF : 0xFFE0E0E0);
        underlineEnd.setBackgroundColor(startActive ? 0xFFE0E0E0 : 0xFF2962FF);
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
        dataSet.setColor(0xFFFF9800);

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
