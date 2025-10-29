package com.example.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.SaveStudyRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainStudyTimeActivity extends AppCompatActivity {

    private TextView groupMainTitle, groupGoalView, tvStartTime, tvEndTime;
    private View underlineStart, underlineEnd;
    private Button btnSaveStudy;

    private BarChart barChart;
    private RecyclerView rankingRecyclerView;
    private RankingAdapter rankingAdapter;
    private final List<RankingItem> rankingList = new ArrayList<>();
    private final Map<String, Float> dailyProgressMap = new LinkedHashMap<>();

    private long groupId = -1L, memberId = -1L;

    private int startHour = 0, startMinute = 0, endHour = 0, endMinute = 0;
    private static final String[] MINUTE_STEPS = {"00", "10", "20", "30", "40", "50"};

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_study_time);

        // --- 뷰 바인딩 ---
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView  = findViewById(R.id.group_goal_view);
        tvStartTime    = findViewById(R.id.tv_start_time);
        tvEndTime      = findViewById(R.id.tv_end_time);
        underlineStart = findViewById(R.id.underline_start);
        underlineEnd   = findViewById(R.id.underline_end);
        btnSaveStudy   = findViewById(R.id.btn_save_study);
        barChart       = findViewById(R.id.barChart);
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

        // --- Intent & SharedPreferences ---
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        groupId          = intent.getLongExtra("groupId", -1L);     // ✅ 반드시 넘겨줘야 함

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = prefs.getLong("memberId", -1L);                   // ✅ 로그인 시 저장된 값 사용

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalView.setText(groupGoal);

        // --- 뒤로가기/알림/네비게이션 ---
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

        // --- 랭킹 RecyclerView ---
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        // --- 차트 초기화 ---
        if (dailyProgressMap.isEmpty()) {
            dailyProgressMap.put(getTodayDate(), 0f);
        }
        updateBarChart();

        // --- 시간 라벨/언더라인 초기화 ---
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

        // --- 저장 버튼 ---
        btnSaveStudy.setOnClickListener(v -> saveStudyRecord());

        // --- 첫 로딩 시 랭킹 불러오기 ---
        updateRanking();
    }

    private String getTodayDate() {
        // 서버/백엔드와 호환되는 yyyy-MM-dd 포맷
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // ✅ 공부 기록 저장
    private void saveStudyRecord() {
        if (memberId == -1L || groupId == -1L) {
            Toast.makeText(this, "로그인 또는 그룹 정보를 확인하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = (endHour * 60 + endMinute) - (startHour * 60 + startMinute);
        if (duration <= 0) {
            Toast.makeText(this, "종료 시간이 시작 시간보다 늦어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        SaveStudyRequest req = new SaveStudyRequest();
        req.setMemberId(memberId);
        req.setGroupId(groupId);
        req.setRecordDate(getTodayDate());
        req.setStartTime(String.format(Locale.KOREAN, "%02d:%02d", startHour, startMinute));
        req.setEndTime(String.format(Locale.KOREAN, "%02d:%02d", endHour, endMinute));
        req.setDurationMinutes(duration);

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.saveStudy(req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> res) {
                if (res.isSuccessful()) {
                    Toast.makeText(GroupMainStudyTimeActivity.this, "공부 기록 저장 완료!", Toast.LENGTH_SHORT).show();
                    updateRanking();
                    updateGraph(duration); // ✅ 빨간줄 원인: 누락되어 있던 메서드 추가함
                } else {
                    Toast.makeText(GroupMainStudyTimeActivity.this, "저장 실패 (" + res.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(GroupMainStudyTimeActivity.this, "연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ 랭킹 갱신
    private void updateRanking() {
        if (groupId == -1L || memberId == -1L) return;

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        // 서버 시그니처: /api/study/ranking/{groupId}?memberId=...
        api.getStudyRanking(groupId, memberId).enqueue(new Callback<List<RankingItem>>() {
            @Override
            public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    rankingList.clear();
                    rankingList.addAll(res.body());
                    // ✅ 많이 공부한 사람이 1등(내림차순)
                    rankingList.sort((a, b) -> Double.compare(
                            b.getValue(), a.getValue()
                    ));
                    rankingAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GroupMainStudyTimeActivity.this, "랭킹 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                Toast.makeText(GroupMainStudyTimeActivity.this, "랭킹 불러오기 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ 저장 후 그래프 업데이트
    private void updateGraph(int durationMinutes) {
        dailyProgressMap.put(getTodayDate(), (float) durationMinutes);
        updateBarChart();
    }

    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> e : dailyProgressMap.entrySet()) {
            entries.add(new BarEntry(index++, e.getValue()));
            labels.add(e.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "누적 기록(분)");
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
        barChart.getAxisLeft().setAxisMaximum(600f); // 대략 10시간(600분)까지
        barChart.invalidate();
    }

    // --- 시간 선택 다이얼로그 (10분 단위) ---
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
            } catch (Exception ignore) {}
        });

        dlg.show();
    }

    private int clampToStep(int minuteValueFromPicker) {
        // 0~59를 10분 단위로 고정
        int rounded = Math.round(minuteValueFromPicker / 10f) * 10;
        if (rounded == 60) rounded = 50;
        return rounded;
    }

    private void updateTimeLabels() {
        tvStartTime.setText(String.format(Locale.KOREAN, "%02d:%02d", startHour, startMinute));
        tvEndTime.setText(String.format(Locale.KOREAN, "%02d:%02d", endHour, endMinute));
    }

    private void setUnderlineActive(boolean startActive) {
        underlineStart.setBackgroundColor(startActive ? 0xFF2962FF : 0xFFE0E0E0);
        underlineEnd.setBackgroundColor(startActive ? 0xFFE0E0E0 : 0xFF2962FF);
    }
}
