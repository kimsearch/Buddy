package com.example.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.*;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.SaveStudyRequest;
import com.example.myapplication.StepHistoryItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainStudyTimeActivity extends AppCompatActivity {

    private TextView groupMainTitle, groupGoalView, tvStartTime, tvEndTime, tvStudyDuration;
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

    private static final String PREFS_NAME = "studyPrefs";
    private static final String DATE_KEY = "lastSavedDate";
    private static final String DURATION_KEY = "todayDuration";

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;

    private final Handler midnightHandler = new Handler();
    private final Runnable midnightResetRunnable = this::resetAtMidnight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_study_time);

        // ✅ 뷰 바인딩
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        underlineStart = findViewById(R.id.underline_start);
        underlineEnd = findViewById(R.id.underline_end);
        tvStudyDuration = findViewById(R.id.tv_study_duration);
        btnSaveStudy = findViewById(R.id.btn_save_study);
        barChart = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);
        backButton = findViewById(R.id.back_button);

        // ✅ Intent & SharedPreferences
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        groupId = intent.getLongExtra("groupId", -1L);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = prefs.getLong("memberId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalView.setText(groupGoal);

        // ✅ 뒤로가기 → MainActivity로 이동
        backButton.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        });

        // ✅ 상단 버튼
        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v -> startActivity(new Intent(this, GroupCommunityActivity.class)));
        notificationButton3.setOnClickListener(v -> startActivity(new Intent(this, AlarmPageActivity.class)));

        // ✅ 하단 네비게이션
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // ✅ RecyclerView 세팅
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        // ✅ 차트 초기값
        dailyProgressMap.put(getTodayDate(), 0f);
        updateBarChart();

        // ✅ 시간 선택
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

        btnSaveStudy.setOnClickListener(v -> saveStudyRecord());

        // ✅ 오늘 기록 복원
        restoreDailyRecord();

        // ✅ 초기 데이터 로딩
        updateRanking();
        fetchStudyHistory();

        // ✅ 자정 리셋 예약
        scheduleMidnightReset();
    }

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // ✅ 공부 시간 계산 후 텍스트 표시
    private void updateStudyDuration() {
        int duration = (endHour * 60 + endMinute) - (startHour * 60 + startMinute);
        if (duration <= 0) {
            tvStudyDuration.setText("오늘 공부한 시간 : 0분");
            return;
        }
        int hours = duration / 60;
        int minutes = duration % 60;
        if (hours > 0)
            tvStudyDuration.setText(String.format(Locale.KOREAN, "오늘 공부한 시간 : %d시간 %d분", hours, minutes));
        else
            tvStudyDuration.setText(String.format(Locale.KOREAN, "오늘 공부한 시간 : %d분", minutes));
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

        updateStudyDuration();
        saveDailyRecord(duration);

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
                    fetchStudyHistory();
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

    // ✅ 하루 기록 저장
    private void saveDailyRecord(int duration) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(DATE_KEY, getTodayDate())
                .putInt(DURATION_KEY, duration)
                .apply();
    }

    // ✅ 하루 기록 복원
    private void restoreDailyRecord() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedDate = prefs.getString(DATE_KEY, "");
        int duration = prefs.getInt(DURATION_KEY, 0);

        if (getTodayDate().equals(savedDate)) {
            if (duration > 0) {
                int hours = duration / 60;
                int minutes = duration % 60;
                if (hours > 0)
                    tvStudyDuration.setText(String.format(Locale.KOREAN, "오늘 공부한 시간 : %d시간 %d분", hours, minutes));
                else
                    tvStudyDuration.setText(String.format(Locale.KOREAN, "오늘 공부한 시간 : %d분", minutes));
            }
        } else {
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
            tvStudyDuration.setText("오늘 공부한 시간 : 0분");
        }
    }

    // ✅ 자정 리셋 예약
    private void scheduleMidnightReset() {
        Calendar now = Calendar.getInstance();
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.add(Calendar.DAY_OF_MONTH, 1);

        long delay = midnight.getTimeInMillis() - now.getTimeInMillis();
        midnightHandler.postDelayed(midnightResetRunnable, delay);
    }

    private void resetAtMidnight() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().clear().apply();
        tvStudyDuration.setText("오늘 공부한 시간 : 0분");
        Toast.makeText(this, "자정이 되어 기록이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
        scheduleMidnightReset();
    }

    // ✅ 랭킹 갱신
    private void updateRanking() {
        if (groupId == -1L || memberId == -1L) return;

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getStudyRanking(groupId, memberId).enqueue(new Callback<List<RankingItem>>() {
            @Override
            public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    rankingList.clear();
                    rankingList.addAll(res.body());
                    rankingList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
                    rankingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                Log.e("StudyTimeActivity", "랭킹 불러오기 실패", t);
            }
        });
    }

    // ✅ 차트 데이터 불러오기
    private void fetchStudyHistory() {
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.getWeeklyRecordHistory(groupId, memberId).enqueue(new Callback<List<StepHistoryItem>>() {
            @Override
            public void onResponse(Call<List<StepHistoryItem>> call, Response<List<StepHistoryItem>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    dailyProgressMap.clear();
                    for (StepHistoryItem item : res.body()) {
                        dailyProgressMap.put(item.getDate(), (float) item.getValue());
                    }
                    updateBarChart();
                }
            }

            @Override
            public void onFailure(Call<List<StepHistoryItem>> call, Throwable t) {
                Log.e("StudyTimeActivity", "히스토리 불러오기 실패", t);
            }
        });
    }

    // ✅ 차트 업데이트
    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> e : dailyProgressMap.entrySet()) {
            entries.add(new BarEntry(index++, e.getValue()));
            labels.add(e.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "누적 기록(분)");
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

    // --- TimePicker ---
    private void showIntervalTimePicker(boolean isStart) {
        final int hour = isStart ? startHour : endHour;
        final int minute = isStart ? startMinute : endMinute;

        TimePickerDialog dlg = new TimePickerDialog(this, (TimePicker view, int h, int mFromPicker) -> {
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
            updateStudyDuration();
        }, hour, minute, true);

        dlg.setOnShowListener(d -> {
            try {
                TimePicker tp = dlg.findViewById(Resources.getSystem().getIdentifier("timePicker", "id", "android"));
                NumberPicker minutePicker = tp.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(MINUTE_STEPS.length - 1);
                minutePicker.setDisplayedValues(null);
                minutePicker.setDisplayedValues(MINUTE_STEPS);
                minutePicker.setWrapSelectorWheel(true);
                minutePicker.setValue((isStart ? startMinute : endMinute) / 10);
            } catch (Exception ignore) {}
        });
        dlg.show();
    }

    private int clampToStep(int minuteValueFromPicker) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        midnightHandler.removeCallbacks(midnightResetRunnable);
    }
}
