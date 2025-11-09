package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.model.SaveSpendRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
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

public class GroupMainSpendActivity extends AppCompatActivity {

    private TextView groupMainTitle, groupGoalView;
    private EditText goalInputEditText;
    private Button goalInputButton;
    private ImageButton backButton;
    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailySpendMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private long groupId = 1L;
    private String cycleType = "DAILY";
    private float todaySpend = 0f;
    private String todayDate;
    private long memberId;

    private final Handler midnightHandler = new Handler();
    private final Runnable midnightResetRunnable = this::resetAtMidnight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_spend);

        // 🧩 Intent 데이터 받기
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        cycleType = intent.getStringExtra("cycleType");

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = prefs.getLong("memberId", -1L);
        if (memberId == -1L) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🧩 View 연결
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        backButton = findViewById(R.id.back_button);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        // 🧩 제목 표시
        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalView.setText(groupGoal);

        // 🧩 RecyclerView 세팅
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        todayDate = getTodayDate();

        // 🧩 저장된 기록 불러오기
        loadSavedSpend();

        updatePieChart(todaySpend);
        dailySpendMap.put(todayDate, todaySpend);
        updateBarChart();

        // 🧩 입력 버튼 동작
        goalInputButton.setOnClickListener(v -> {
            String inputText = goalInputEditText.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(this, "금액을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long amount = Long.parseLong(inputText);
                todaySpend += amount; // ✅ 누적
                dailySpendMap.put(todayDate, todaySpend);
                saveSpendToPrefs(todaySpend); // ✅ 로컬 저장
                updatePieChart(100f);
                updateBarChart();

                SaveSpendRequest request = new SaveSpendRequest();
                request.setGroupId(groupId);
                request.setMemberId(memberId);
                request.setAmount(amount);
                request.setRecordDate(todayDate);

                Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
                api.saveSpend(request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(GroupMainSpendActivity.this, amount + "원 기록 완료!", Toast.LENGTH_SHORT).show();
                            loadRanking(api, memberId);
                        } else {
                            Toast.makeText(GroupMainSpendActivity.this, "서버 오류 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(GroupMainSpendActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                goalInputEditText.setText("");

            } catch (NumberFormatException e) {
                Toast.makeText(this, "숫자를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 🧩 상단 버튼 연결
        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v -> startActivity(new Intent(this, GroupCommunityActivity.class)));
        notificationButton3.setOnClickListener(v -> startActivity(new Intent(this, AlarmPageActivity.class)));

        // 🧩 하단 네비게이션 연결
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // 🧩 뒤로가기 버튼 → 메인 이동
        backButton.setOnClickListener(v -> {
            Intent intentBack = new Intent(GroupMainSpendActivity.this, MainActivity.class);
            intentBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentBack);
            finish();
        });

        // 🧩 첫 랭킹 불러오기
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        loadRanking(api, memberId);

        // 🕛 00시에 자동 리셋 예약
        scheduleMidnightReset();
    }

    // ✅ SharedPreferences에 소비 기록 저장
    private void saveSpendToPrefs(float value) {
        SharedPreferences prefs = getSharedPreferences("spendPrefs", MODE_PRIVATE);
        prefs.edit()
                .putFloat("todaySpend", value)
                .putString("savedDate", todayDate)
                .apply();
    }

    // ✅ 저장된 기록 불러오기
    private void loadSavedSpend() {
        SharedPreferences prefs = getSharedPreferences("spendPrefs", MODE_PRIVATE);
        String savedDate = prefs.getString("savedDate", "");
        if (savedDate.equals(todayDate)) {
            todaySpend = prefs.getFloat("todaySpend", 0f);
        } else {
            todaySpend = 0f; // 날짜가 바뀌었으면 리셋
            prefs.edit().clear().apply();
        }
    }

    // ✅ 00시 자동 초기화 스케줄
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
        todaySpend = 0f;
        dailySpendMap.clear();
        dailySpendMap.put(getTodayDate(), 0f);
        updatePieChart(0f);
        updateBarChart();
        saveSpendToPrefs(0f);
        Toast.makeText(this, "자정이 되어 기록이 초기화되었습니다!", Toast.LENGTH_SHORT).show();
        scheduleMidnightReset();
    }

    // ✅ 랭킹 불러오기
    private void loadRanking(Retrofit_interface api, long memberId) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String end = sdf.format(cal.getTime());

        if (cycleType == null) cycleType = "DAILY";
        switch (cycleType) {
            case "WEEKLY":
                cal.add(Calendar.DAY_OF_MONTH, -7);
                break;
            case "MONTHLY":
                cal.add(Calendar.DAY_OF_MONTH, -30);
                break;
            default:
                cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        String start = sdf.format(cal.getTime());

        api.getSpendRanking(groupId, start, end, memberId).enqueue(new Callback<List<RankingItem>>() {
            @Override
            public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RankingItem> data = response.body();
                    data.sort(Comparator.comparingDouble(RankingItem::getValue)); // 적게 쓴 순
                    rankingList.clear();
                    rankingList.addAll(data);
                    rankingAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                Log.e("SpendRanking", "불러오기 실패", t);
            }
        });
    }

    // ✅ 차트 관련 메소드들
    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value, "소비 완료"));
        entries.add(new PieEntry(100 - value, "남은 목표"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(Color.rgb(255, 152, 0), Color.LTGRAY);

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

    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Float> entry : dailySpendMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "소비 기록");
        dataSet.setColor(Color.parseColor("#FF9800"));
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

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        midnightHandler.removeCallbacks(midnightResetRunnable);
    }
}
