package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.TimeLogRecordDTO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainTimeLogActivity extends AppCompatActivity {

    private TextView groupMainTitle, groupGoalTextView, tvStartTime, tvEndTime, resultText;
    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;
    private Button btnSaveStudy;

    private RecyclerView rankingRecyclerView;
    private BarChart barChart;
    private RankingAdapter rankingAdapter;
    private List<RankingItem> rankingList = new ArrayList<>();
    private Map<String, Float> dailyProgressMap = new LinkedHashMap<>();

    private LocalTime startTime = null;
    private LocalTime endTime = null;

    private Retrofit_interface timeLogApi;
    private long memberId;
    private long groupId;
    private float todayTotalMinutes = 0f;
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_time_log);

        // ✅ View 바인딩
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalTextView = findViewById(R.id.group_goal_view);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        resultText = findViewById(R.id.study_result_text);
        btnSaveStudy = findViewById(R.id.btn_save_study);
        backButton = findViewById(R.id.back_button);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        barChart = findViewById(R.id.barChart);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setAdapter(rankingAdapter);

        // ✅ 인텐트 데이터
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        groupId = intent.getLongExtra("groupId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText(groupGoal);

        // ✅ 로그인 정보
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = prefs.getLong("memberId", -1L);
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // ✅ Retrofit 초기화
        timeLogApi = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // ✅ 이전 차트/기록 불러오기
        loadChartData();

        // ✅ 시작 시간
        tvStartTime.setOnClickListener(v -> {
            startTime = LocalTime.now();
            tvStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", startTime.getHour(), startTime.getMinute()));
            Toast.makeText(this, "독서 시작!", Toast.LENGTH_SHORT).show();
        });

        // ✅ 종료 시간
        tvEndTime.setOnClickListener(v -> {
            if (startTime == null) {
                Toast.makeText(this, "시작 시간을 먼저 설정하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            endTime = LocalTime.now();
            tvEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", endTime.getHour(), endTime.getMinute()));
            Toast.makeText(this, "독서 종료!", Toast.LENGTH_SHORT).show();
        });

        // ✅ 저장 버튼
        btnSaveStudy.setOnClickListener(v -> saveTimeLog());

        // ✅ 뒤로가기
        backButton.setOnClickListener(v -> finish());

        // ✅ 상단 버튼
        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v -> startActivity(new Intent(this, GroupCommunityActivity.class)));
        notificationButton3.setOnClickListener(v -> startActivity(new Intent(this, AlarmPageActivity.class)));

        // ✅ 하단 네비
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // ✅ 첫 로드
        loadRanking();
        updateBarChart();
    }

    // ✅ 시간 저장
    private void saveTimeLog() {
        if (startTime == null || endTime == null) {
            Toast.makeText(this, "시작/종료 시간을 모두 설정하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        long minutes = Duration.between(startTime, endTime).toMinutes();
        if (minutes <= 0) {
            Toast.makeText(this, "종료 시간이 시작 시간보다 이전입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        todayTotalMinutes += minutes;
        TimeLogRecordDTO dto = new TimeLogRecordDTO();

        timeLogApi.saveTimeLog(dto).enqueue(new Callback<TimeLogRecordDTO>() {
            @Override
            public void onResponse(Call<TimeLogRecordDTO> call, Response<TimeLogRecordDTO> response) {
                if (response.isSuccessful()) {
                    resultText.setText(String.format(Locale.getDefault(), "오늘 읽은 시간: %.1f분", todayTotalMinutes));

                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    dailyProgressMap.put(today, todayTotalMinutes);

                    saveChartData();
                    updateBarChart();
                    loadRanking();

                    Toast.makeText(GroupMainTimeLogActivity.this,
                            "누적 독서 시간 " + todayTotalMinutes + "분 저장 완료!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GroupMainTimeLogActivity.this,
                            "저장 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TimeLogRecordDTO> call, Throwable t) {
                Toast.makeText(GroupMainTimeLogActivity.this,
                        "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ 그룹 전체 랭킹
    private void loadRanking() {
        timeLogApi.getGroupTimeLogs(groupId).enqueue(new Callback<List<TimeLogRecordDTO>>() {
            @Override
            public void onResponse(Call<List<TimeLogRecordDTO>> call, Response<List<TimeLogRecordDTO>> response) {
                rankingList.clear();

                if (response.isSuccessful() && response.body() != null) {
                    Map<Long, Float> totalMap = new HashMap<>();
                    Map<Long, String> nameMap = new HashMap<>();

                    for (TimeLogRecordDTO dto : response.body()) {
                        Long uid = dto.getUserId();
                        float current = totalMap.getOrDefault(uid, 0f);
                        totalMap.put(uid, current + dto.getTimeSpent());
                        nameMap.put(uid, dto.getNickname() != null ? dto.getNickname() : "익명");
                    }

                    for (Map.Entry<Long, Float> e : totalMap.entrySet()) {
                        RankingItem item = new RankingItem();
                        item.setMemberId(e.getKey());
                        item.setNickname(nameMap.get(e.getKey()));
                        item.setValue(e.getValue());
                        rankingList.add(item);
                    }

                    rankingList.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
                    if (rankingList.size() > 3)
                        rankingList = rankingList.subList(0, 3);

                    rankingAdapter.notifyDataSetChanged();
                } else {
                    SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    String myNick = prefs.getString("nickname", "나");
                    RankingItem me = new RankingItem(memberId, myNick, todayTotalMinutes);
                    rankingList.add(me);
                    rankingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<TimeLogRecordDTO>> call, Throwable t) {
                Toast.makeText(GroupMainTimeLogActivity.this,
                        "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ 차트 업데이트
    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int startIndex = Math.max(0, dailyProgressMap.size() - 7);
        int index = 0;
        List<Map.Entry<String, Float>> recent = new ArrayList<>(dailyProgressMap.entrySet())
                .subList(startIndex, dailyProgressMap.size());

        for (Map.Entry<String, Float> entry : recent) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey().substring(5)); // MM-dd
        }

        BarDataSet dataSet = new BarDataSet(entries, "최근 7일 독서 시간(분)");
        dataSet.setColor(0xFF4CAF50);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

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

    // ✅ SharedPreferences에 차트 데이터 저장
    private void saveChartData() {
        SharedPreferences prefs = getSharedPreferences("timeLogPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Float> e : dailyProgressMap.entrySet()) {
            sb.append(e.getKey()).append(":").append(e.getValue()).append(",");
        }
        editor.putString("chartData", sb.toString());
        editor.putString("lastDate", todayDate);
        editor.apply();
    }

    // ✅ SharedPreferences에서 차트 데이터 불러오기
    private void loadChartData() {
        SharedPreferences prefs = getSharedPreferences("timeLogPrefs", MODE_PRIVATE);
        String data = prefs.getString("chartData", "");
        String lastDate = prefs.getString("lastDate", todayDate);

        if (!lastDate.equals(todayDate)) {
            todayTotalMinutes = 0f;
            return; // 날짜 바뀌면 리셋
        }

        if (!data.isEmpty()) {
            dailyProgressMap.clear();
            for (String s : data.split(",")) {
                String[] parts = s.split(":");
                if (parts.length == 2) {
                    dailyProgressMap.put(parts[0], Float.parseFloat(parts[1]));
                }
            }
        }
    }
}
