package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.model.GoalTimeRecordDTO;
import com.example.myapplication.model.TimeLogRecordDTO;
import com.example.myapplication.StepHistoryItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Locale;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainGoalMinutesActivity extends AppCompatActivity {

    private TextView groupMainTitle, groupGoalTextView, tvReadProgress;
    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;
    private RecyclerView rankingRecyclerView;
    private ProgressBar pbReadProgress;
    private BarChart barChart;

    private EditText etReadMinutes;
    private Button btnPlus, btnMinus, btnPreset10, btnPreset20, btnPreset30, btnStartTimer, btnDone;

    private Retrofit_interface retrofitApi;
    private RankingAdapter rankingAdapter;
    private List<RankingItem> rankingList = new ArrayList<>();

    private Long groupId;
    private Long memberId;
    private int goalMinutes = 60;
    private int readMinutesToday = 0;

    private boolean isRunning = false;
    private long startTime = 0L;
    private long timeAccumulated = 0L;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private final Map<String, Float> dailyProgressMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_goal_minutes);

        setupViews();
        retrofitApi = Retrofit_client.getInstance().create(Retrofit_interface.class);

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = prefs.getLong("memberId", -1L);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        groupId = intent.getLongExtra("groupId", -1L);

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalTextView.setText(groupGoal);

        setupButtonListeners();
        setupTimerRunnable();

        if (memberId != -1L && groupId != -1L) {
            loadGoalTime();
            loadDailyRanking();
            loadWeeklyHistoryChart();
        } else {
            Toast.makeText(this, "사용자 또는 그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViews() {
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalTextView = findViewById(R.id.group_goal_view);
        etReadMinutes = findViewById(R.id.et_read_minutes);
        tvReadProgress = findViewById(R.id.tv_read_progress);
        pbReadProgress = findViewById(R.id.pb_read_progress);
        btnPlus = findViewById(R.id.btn_read_plus);
        btnMinus = findViewById(R.id.btn_read_minus);
        btnPreset10 = findViewById(R.id.preset_10);
        btnPreset20 = findViewById(R.id.preset_20);
        btnPreset30 = findViewById(R.id.preset_30);
        btnStartTimer = findViewById(R.id.btn_read_start);
        btnDone = findViewById(R.id.btn_read_done);
        backButton = findViewById(R.id.back_button);
        barChart = findViewById(R.id.barChart);

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setAdapter(rankingAdapter);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> finish());
        btnPlus.setOnClickListener(v -> changeMinutes(5));
        btnMinus.setOnClickListener(v -> changeMinutes(-5));
        btnPreset10.setOnClickListener(v -> setGoal(10));
        btnPreset20.setOnClickListener(v -> setGoal(20));
        btnPreset30.setOnClickListener(v -> setGoal(30));
        btnStartTimer.setOnClickListener(v -> toggleTimer());
        btnDone.setOnClickListener(v -> completeAndSave());

        // ✅ 목표 시간 직접 수정 가능
        etReadMinutes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        goalMinutes = Integer.parseInt(s.toString());
                        updateProgressBar();
                    }
                } catch (NumberFormatException e) {
                    goalMinutes = 0;
                }
            }
        });

        // ✅ 상단 버튼 연결
        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v -> startActivity(new Intent(this, GroupCommunityActivity.class)));
        notificationButton3.setOnClickListener(v -> startActivity(new Intent(this, AlarmPageActivity.class)));

        // ✅ 하단 네비게이션 연결
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
    }

    private void loadGoalTime() {
        retrofitApi.getGoalTime(memberId, groupId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null && response.body() > 0) {
                    goalMinutes = response.body();
                }
                etReadMinutes.setText(String.valueOf(goalMinutes));
                updateProgressBar();
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                etReadMinutes.setText(String.valueOf(goalMinutes));
                updateProgressBar();
            }
        });
    }

    // ✅ 하루 기준 랭킹 (상위 3명)
    private void loadDailyRanking() {
        retrofitApi.getUserTimeLogs(memberId).enqueue(new Callback<List<TimeLogRecordDTO>>() {
            @Override
            public void onResponse(Call<List<TimeLogRecordDTO>> call, Response<List<TimeLogRecordDTO>> response) {
                rankingList.clear();
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Map<Long, Float> todayMap = new HashMap<>();
                Map<Long, String> nameMap = new HashMap<>();

                if (response.isSuccessful() && response.body() != null) {
                    for (TimeLogRecordDTO log : response.body()) {
                        if (log.getRecordDate() == null) continue;
                        String recordDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(log.getRecordDate());
                        if (recordDate.equals(today)) {
                            long uid = log.getUserId();
                            float total = todayMap.getOrDefault(uid, 0f);
                            todayMap.put(uid, total + log.getTimeSpent());
                            nameMap.put(uid, log.getNickname() != null ? log.getNickname() : "익명");
                        }
                    }

                    for (Map.Entry<Long, Float> entry : todayMap.entrySet()) {
                        RankingItem item = new RankingItem();
                        item.setMemberId(entry.getKey());
                        item.setNickname(nameMap.get(entry.getKey()));
                        item.setValue(entry.getValue());
                        rankingList.add(item);
                    }
                }

                // ✅ 혼자라도 표시
                if (rankingList.isEmpty()) {
                    SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    String myNick = prefs.getString("nickname", "나");
                    RankingItem me = new RankingItem();
                    me.setMemberId(memberId);
                    me.setNickname(myNick);
                    me.setValue(readMinutesToday);
                    rankingList.add(me);
                }

                rankingList.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
                if (rankingList.size() > 3) rankingList = rankingList.subList(0, 3);

                rankingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<TimeLogRecordDTO>> call, Throwable t) {
                Toast.makeText(GroupMainGoalMinutesActivity.this, "랭킹 불러오기 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWeeklyHistoryChart() {
        retrofitApi.getWeeklyRecordHistory(groupId, memberId).enqueue(new Callback<List<StepHistoryItem>>() {
            @Override
            public void onResponse(Call<List<StepHistoryItem>> call, Response<List<StepHistoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();
                    int i = 0;
                    for (StepHistoryItem item : response.body()) {
                        entries.add(new BarEntry(i, item.getValue()));
                        labels.add(item.getDate().substring(5));
                        i++;
                    }
                    updateBarChart(entries, labels);
                }
            }
            @Override
            public void onFailure(Call<List<StepHistoryItem>> call, Throwable t) {
                Log.e("Chart", "차트 데이터 로딩 실패", t);
            }
        });
    }

    private void changeMinutes(int diff) {
        String input = etReadMinutes.getText().toString();
        int current = input.isEmpty() ? 0 : Integer.parseInt(input);
        int newGoal = Math.max(0, current + diff);
        etReadMinutes.setText(String.valueOf(newGoal));
    }

    private void setGoal(int minutes) {
        etReadMinutes.setText(String.valueOf(minutes));
    }

    private void toggleTimer() {
        if (isRunning) {
            isRunning = false;
            timerHandler.removeCallbacks(timerRunnable);
            timeAccumulated += System.currentTimeMillis() - startTime;
            btnStartTimer.setText("다시 시작");
        } else {
            isRunning = true;
            startTime = System.currentTimeMillis();
            btnStartTimer.setText("일시정지");
            timerHandler.post(timerRunnable);
        }
    }

    private void setupTimerRunnable() {
        timerRunnable = () -> {
            if (isRunning) {
                long elapsedMillis = timeAccumulated + (System.currentTimeMillis() - startTime);
                int elapsedSeconds = (int) (elapsedMillis / 1000);
                int minutes = elapsedSeconds / 60;
                int seconds = elapsedSeconds % 60;
                int currentTotalMinutes = readMinutesToday + minutes;
                String timeString = String.format(Locale.getDefault(), "진행중: %02d분 %02d초", minutes, seconds);
                int progress = goalMinutes > 0 ? (int) ((currentTotalMinutes * 100.0) / goalMinutes) : 0;
                pbReadProgress.setProgress(Math.min(100, progress));
                tvReadProgress.setText(String.format(Locale.getDefault(), "%s (오늘 %d / %d분, %d%%)",
                        timeString, currentTotalMinutes, goalMinutes, progress));
                timerHandler.postDelayed(this.timerRunnable, 1000);
            }
        };
    }

    private void completeAndSave() {
        if (isRunning) {
            isRunning = false;
            timerHandler.removeCallbacks(timerRunnable);
            timeAccumulated += System.currentTimeMillis() - startTime;
        }

        int newMinutes = (int) (timeAccumulated / 60000);
        if (newMinutes == 0 && timeAccumulated > 1000) newMinutes = 1;
        if (newMinutes <= 0) {
            Toast.makeText(this, "기록할 시간이 없습니다.", Toast.LENGTH_SHORT).show();
            resetTimer();
            return;
        }

        TimeLogRecordDTO timeLog = new TimeLogRecordDTO();
        timeLog.setUserId(memberId);
        timeLog.setGroupId(groupId);
        timeLog.setTimeSpent((long) newMinutes);

        int finalNewMinutes = newMinutes;
        retrofitApi.saveTimeLog(timeLog).enqueue(new Callback<TimeLogRecordDTO>() {
            @Override
            public void onResponse(Call<TimeLogRecordDTO> call, Response<TimeLogRecordDTO> response) {
                if (response.isSuccessful()) {
                    readMinutesToday += finalNewMinutes;
                    updateProgressBar();
                    Toast.makeText(GroupMainGoalMinutesActivity.this, finalNewMinutes + "분 저장 완료!", Toast.LENGTH_SHORT).show();

                    // ✅ 오늘 기록 차트에 즉시 반영
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    dailyProgressMap.put(today, (float) readMinutesToday);
                    updateBarChartFromMap();

                    new Handler().postDelayed(() -> loadDailyRanking(), 800);
                }
                resetTimer();
            }

            @Override
            public void onFailure(Call<TimeLogRecordDTO> call, Throwable t) {
                Toast.makeText(GroupMainGoalMinutesActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                resetTimer();
            }
        });
    }

    private void resetTimer() {
        timeAccumulated = 0L;
        btnStartTimer.setText("타이머 시작");
        updateProgressBar();
    }

    private void updateProgressBar() {
        int progress = goalMinutes > 0 ? (int) ((readMinutesToday * 100.0) / goalMinutes) : 0;
        pbReadProgress.setProgress(Math.min(100, progress));
        tvReadProgress.setText("오늘 " + readMinutesToday + " / " + goalMinutes + "분 (" + progress + "%)");
    }

    private void updateBarChartFromMap() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Float> e : dailyProgressMap.entrySet()) {
            entries.add(new BarEntry(i++, e.getValue()));
            labels.add(e.getKey().substring(5));
        }
        updateBarChart(entries, labels);
    }

    private void updateBarChart(List<BarEntry> entries, List<String> labels) {
        if (entries.isEmpty()) {
            barChart.clear();
            barChart.invalidate();
            return;
        }
        BarDataSet dataSet = new BarDataSet(entries, "최근 7일 기록(분)");
        dataSet.setColor(0xFF76C7C0);
        dataSet.setValueTextSize(10f);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setFitBars(true);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.invalidate();
    }
}
