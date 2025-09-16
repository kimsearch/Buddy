package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainGoalBooksActivity extends AppCompatActivity {

    private Retrofit_interface api;
    private TextView groupMainTitle;
    private Button successButton, failureButton;
    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private ImageButton backButton;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    // 그룹/사용자/주기 정보
    private long groupId = -1L, memberId = -1L;
    private int cycleDays = 7;
    private String startDateStr = "";           // "yyyy-MM-dd"
    private String cycleStartStr, cycleEndStr;  // "yyyy-MM-dd"

    private static final String PREF_LOCK = "goalLogLocks"; // 로컬 잠금 SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_goal_books);

        // ====== View binding ======
        groupMainTitle      = findViewById(R.id.group_main_title);
        successButton       = findViewById(R.id.success_button);
        failureButton       = findViewById(R.id.failure_button);
        barChart            = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);

        navHome  = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navMyPage= findViewById(R.id.nav_mypage);
        navSearch= findViewById(R.id.nav_search);
        navPet   = findViewById(R.id.nav_pet);

        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);
        backButton          = findViewById(R.id.back_button);

        api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // ====== Intent/Prefs ======
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        if (groupName != null) groupMainTitle.setText(groupName);

        groupId      = intent.getLongExtra("groupId", -1L);
        cycleDays    = intent.getIntExtra("cycleDays", 7);
        startDateStr = intent.getStringExtra("startDate"); // "yyyy-MM-dd"

        SharedPreferences loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = loginPrefs.getLong("memberId", -1L);

        // ====== 주기 계산 ======
        computeCurrentCycle(startDateStr, cycleDays);

        // ====== 헤더 버튼 ======
        backButton.setOnClickListener(v -> finish());
        notificationButton1.setOnClickListener(v -> {
            Intent i1 = new Intent(this, GroupMemberActivity.class);
            i1.putExtra("groupId", groupId);
            startActivity(i1);
        });
        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(this, GroupCommunityActivity.class)));
        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(this, AlarmPageActivity.class)));

        // ====== 랭킹 리사이클러 ======
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        // ====== 버튼 리스너 ======
        successButton.setOnClickListener(v -> submitResult(true));   // 성공만 1로 승급
        failureButton.setOnClickListener(v -> submitResult(false));  // 실패는 0 유지, 그래도 잠금

        // ====== 네비게이션 ======
        navHome .setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet  .setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));

        // ====== 잠금 상태 ======
        if (isLockedLocally()) {
            lockButtons(true);
        } else {
            fetchCurrentLogFromServer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면 복귀 시 최신 랭킹/히스토리 동기화
        loadRankingAndBind();
        loadRecordHistoryAndDrawChart(); // ✅ 변환 없이 record-history 재사용
    }

    // -------------------- 주기 계산 --------------------
    private void computeCurrentCycle(String startDate, int cycleDays) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date start = sdf.parse(startDate);
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(start);

            Calendar today = Calendar.getInstance();
            long daysDiff = (today.getTimeInMillis() - startCal.getTimeInMillis()) / (1000L * 60 * 60 * 24);
            long k = Math.max(0, daysDiff / cycleDays);

            Calendar s = (Calendar) startCal.clone();
            s.add(Calendar.DAY_OF_YEAR, (int) (k * cycleDays));
            Calendar e = (Calendar) s.clone();
            e.add(Calendar.DAY_OF_YEAR, cycleDays - 1);

            SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            cycleStartStr = out.format(s.getTime());
            cycleEndStr   = out.format(e.getTime());
        } catch (Exception e) {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date());
            cycleStartStr = today;
            cycleEndStr   = today;
        }
    }

    private String cycleLabel() {
        return toMd(cycleStartStr) + "~" + toMd(cycleEndStr);
    }
    private String toMd(String ymd) {
        try {
            SimpleDateFormat in  = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            SimpleDateFormat out = new SimpleDateFormat("MM/dd",     Locale.KOREA);
            return out.format(in.parse(ymd));
        } catch (Exception e) {
            return ymd;
        }
    }

    // -------------------- 서버 조회 / 업서트 --------------------
    private void fetchCurrentLogFromServer() {
        if (groupId == -1L || memberId == -1L) {
            lockButtons(false);
            return;
        }
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date());
        api.getCurrentLog(groupId, memberId, today).enqueue(new Callback<GoalLogDto>() {
            @Override public void onResponse(Call<GoalLogDto> call, Response<GoalLogDto> res) {
                if (res.isSuccessful() && res.body() != null) {
                    Boolean ok = res.body().isSuccess;
                    if (Boolean.TRUE.equals(ok)) {
                        setLocalLock();
                        lockButtons(true);
                    } else {
                        lockButtons(false);
                    }
                } else {
                    lockButtons(false);
                }
            }
            @Override public void onFailure(Call<GoalLogDto> call, Throwable t) {
                lockButtons(false);
            }
        });
    }

    private void submitResult(boolean ok) {
        if (groupId == -1L || memberId == -1L) {
            Toast.makeText(this, "로그인이 필요하거나 그룹 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        lockButtons(true);

        SaveGoalResultRequest body =
                new SaveGoalResultRequest(memberId, cycleStartStr, cycleEndStr, ok);

        api.upsertLog(groupId, body).enqueue(new Callback<GoalLogDto>() {
            @Override public void onResponse(Call<GoalLogDto> call, Response<GoalLogDto> res) {
                if (res.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            ok ? "🎉 이번 주기 성공으로 기록!" : "😢 이번 주기 실패로 기록.",
                            Toast.LENGTH_SHORT).show();
                    setLocalLock();
                    // 저장 후 서버 기준으로 다시 로드
                    loadRankingAndBind();
                    loadRecordHistoryAndDrawChart();
                } else {
                    Toast.makeText(getApplicationContext(), "저장 실패: " + res.code(), Toast.LENGTH_SHORT).show();
                    lockButtons(false);
                }
            }
            @Override public void onFailure(Call<GoalLogDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                lockButtons(false);
            }
        });
    }

    // -------------------- 랭킹 --------------------
    private void loadRankingAndBind() {
        if (groupId == -1L) return;
        api.getRanking(groupId).enqueue(new Callback<List<RankingItem>>() {
            @Override public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> res) {
                if (!res.isSuccessful() || res.body() == null) return;
                List<RankingItem> items = res.body();

                int maxSuccess = items.stream().mapToInt(RankingItem::getSuccessCount).max().orElse(1);
                for (RankingItem it : items) {
                    it.setProgress((int)((it.getSuccessCount() * 100.0) / maxSuccess));
                    it.setProfileResId(R.drawable.ic_profile);
                }
                rankingList.clear();
                rankingList.addAll(items);
                rankingAdapter.notifyDataSetChanged();
            }
            @Override public void onFailure(Call<List<RankingItem>> call, Throwable t) { /* no-op */ }
        });
    }

    // -------------------- (변환 없이) 기록 히스토리 --------------------
    private void loadRecordHistoryAndDrawChart() {
        if (groupId == -1L || memberId == -1L) return;

        api.getWeeklyRecordHistory(groupId, memberId).enqueue(new Callback<List<StepHistoryItem>>() {
            @Override public void onResponse(Call<List<StepHistoryItem>> call, Response<List<StepHistoryItem>> res) {
                if (!res.isSuccessful() || res.body() == null) return;
                drawHistoryBarChart(res.body()); // ✅ 값 그대로 사용 (0이면 0으로)
            }
            @Override public void onFailure(Call<List<StepHistoryItem>> call, Throwable t) { /* no-op */ }
        });
    }

    private void drawHistoryBarChart(List<StepHistoryItem> historyList) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < historyList.size(); i++) {
            StepHistoryItem item = historyList.get(i);
            float y = (item.getStepCount() > 0) ? 100f : 0f; // 1이면 100, 0이면 0
            entries.add(new BarEntry(i, y));
            labels.add(item.getDate().toString().substring(5));
        }

        BarDataSet ds = new BarDataSet(entries, "주기별 성공(100)/실패(0)");
        ds.setColor(Color.parseColor("#FF9800"));
        ds.setValueTextSize(12f);
        BarData data = new BarData(ds);

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(100f);
        barChart.invalidate();
    }

    // -------------------- 잠금 로직 --------------------
    private void lockButtons(boolean locked) {
        successButton.setEnabled(!locked);
        failureButton.setEnabled(!locked);
        successButton.setAlpha(locked ? 0.5f : 1f);
        failureButton.setAlpha(locked ? 0.5f : 1f);
    }
    private String lockKey() { return "lock:" + groupId + ":" + cycleStartStr; }
    private boolean isLockedLocally() {
        return getSharedPreferences(PREF_LOCK, MODE_PRIVATE).getBoolean(lockKey(), false);
    }
    private void setLocalLock() {
        getSharedPreferences(PREF_LOCK, MODE_PRIVATE).edit().putBoolean(lockKey(), true).apply();
    }
}