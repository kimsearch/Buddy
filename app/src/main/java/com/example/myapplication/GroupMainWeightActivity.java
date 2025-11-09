package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.model.WeightRecordModel;
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

public class GroupMainWeightActivity extends AppCompatActivity {

    private TextView groupGoalView, groupMainTitle, weightResultText;
    private EditText goalInputEditText;
    private Button goalInputButton;
    private ImageButton backButton; // ✅ 추가

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailyWeightMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private long memberId, groupId;
    private static final String PREF_NAME = "weightPrefs";
    private static final String DATE_KEY = "lastSavedDate";
    private static final String WEIGHT_KEY = "todayWeight";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_weight);

        // ✅ 뷰 연결
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        weightResultText = findViewById(R.id.weight_result_text);
        backButton = findViewById(R.id.back_button); // ✅ XML에 있는 back_button 연결

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        // ✅ 로그인 정보
        SharedPreferences loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = loginPrefs.getLong("memberId", -1L);
        String myNickname = loginPrefs.getString("nickname", "나");
        if (memberId == -1L) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent intent = getIntent();
        groupId = intent.getLongExtra("groupId", -1L);
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");

        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalView.setText(groupGoal);

        // ✅ RecyclerView 세팅
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        // ✅ 네비게이션 버튼
        setupNavigation();

        // ✅ 뒤로가기 버튼 → MainActivity 이동
        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(GroupMainWeightActivity.this, MainActivity.class);
            backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(backIntent);
            finish();
        });

        // ✅ 초기 차트 세팅
        updatePieChart(0f);
        dailyWeightMap.put(getTodayDate(), 0f);
        updateBarChart();

        // ✅ 날짜 확인 → 새날이면 초기화
        checkAndResetIfNewDay();

        // ✅ 입력 버튼
        goalInputButton.setOnClickListener(v -> saveWeight());

        // ✅ 초기 랭킹 불러오기
        loadWeightRanking(myNickname);
    }

    // ✅ 랭킹 불러오기 (상위 3명, 혼자일 경우 내 닉네임 표시)
    private void loadWeightRanking(String myNickname) {
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        api.getWeightRanking(groupId).enqueue(new Callback<List<RankingItem>>() {
            @Override
            public void onResponse(Call<List<RankingItem>> call, Response<List<RankingItem>> response) {
                rankingList.clear();

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    for (RankingItem item : response.body()) {
                        float loss = (float) (item.getYesterdayValue() - item.getValue());
                        item.setLossValue(loss);
                    }

                    response.body().sort((a, b) -> Float.compare(b.getLossValue(), a.getLossValue()));
                    rankingList.addAll(response.body().subList(0, Math.min(3, response.body().size())));
                } else {
                    SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    float myWeight = prefs.getFloat(WEIGHT_KEY, 0f);

                    RankingItem me = new RankingItem();
                    me.setNickname(myNickname);
                    me.setValue(myWeight);
                    me.setLossValue(0f);
                    rankingList.add(me);
                }

                rankingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                rankingList.clear();

                SharedPreferences loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                String myNickname = loginPrefs.getString("nickname", "나");

                SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                float myWeight = prefs.getFloat(WEIGHT_KEY, 0f);

                RankingItem me = new RankingItem();
                me.setNickname(myNickname);
                me.setValue(myWeight);
                me.setLossValue(0f);

                rankingList.add(me);
                rankingAdapter.notifyDataSetChanged();

                Toast.makeText(GroupMainWeightActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ 오늘 날짜 확인 후 리셋 / 복원
    private void checkAndResetIfNewDay() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedDate = prefs.getString(DATE_KEY, null);
        String today = getTodayDate();

        if (savedDate == null || !savedDate.equals(today)) {
            resetDailyData();
            prefs.edit().putString(DATE_KEY, today).apply();
        } else {
            restoreDailyData();
        }
    }

    private void resetDailyData() {
        dailyWeightMap.clear();
        dailyWeightMap.put(getTodayDate(), 0f);
        updatePieChart(0f);
        updateBarChart();

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().remove(WEIGHT_KEY).apply();

        weightResultText.setText("");
        Toast.makeText(this, "새로운 하루가 시작됐습니다!", Toast.LENGTH_SHORT).show();
    }

    private void restoreDailyData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        float savedWeight = prefs.getFloat(WEIGHT_KEY, 0f);

        if (savedWeight > 0) {
            dailyWeightMap.put(getTodayDate(), savedWeight);
            updatePieChart(100f);
            updateBarChart();
            weightResultText.setText("오늘 기록된 몸무게: " + savedWeight + "kg");
        }
    }

    private void saveWeight() {
        String inputText = goalInputEditText.getText().toString().trim();
        if (inputText.isEmpty()) {
            Toast.makeText(this, "몸무게를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float newWeight = Float.parseFloat(inputText);
            String today = getTodayDate();

            dailyWeightMap.put(today, newWeight);
            updatePieChart(100f);
            updateBarChart();
            weightResultText.setText("오늘 기록된 몸무게: " + newWeight + "kg");

            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            prefs.edit()
                    .putString(DATE_KEY, today)
                    .putFloat(WEIGHT_KEY, newWeight)
                    .apply();

            WeightRecordModel record = new WeightRecordModel(memberId, newWeight, today);
            Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
            api.saveWeight(record).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(GroupMainWeightActivity.this, "몸무게 저장 완료", Toast.LENGTH_SHORT).show();
                        SharedPreferences loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                        String myNickname = loginPrefs.getString("nickname", "나");
                        loadWeightRanking(myNickname);
                    } else {
                        Toast.makeText(GroupMainWeightActivity.this, "서버 오류 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(GroupMainWeightActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            goalInputEditText.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "숫자를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value, "달성"));
        entries.add(new PieEntry(100 - value, "남은 목표"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(Color.rgb(76, 175, 80), Color.LTGRAY);

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

        for (Map.Entry<String, Float> entry : dailyWeightMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "몸무게 기록");
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

    private String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v -> startActivity(new Intent(this, GroupCommunityActivity.class)));
        notificationButton3.setOnClickListener(v -> startActivity(new Intent(this, AlarmPageActivity.class)));
    }
}
