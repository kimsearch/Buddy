package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.myapplication.model.SaveSpendRequest;
import com.example.myapplication.RankingItem;
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
import java.util.*;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMainSpendActivity extends AppCompatActivity {

    private TextView groupMainTitle, groupGoalView;
    private EditText goalInputEditText;
    private Button goalInputButton;
    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    private final Map<String, Float> dailySpendMap = new LinkedHashMap<>();
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private long groupId = 1L; // TODO: 실제 그룹 ID로 교체 필요
    private String cycleType = "DAILY"; // 기본값: 매일 / WEEKLY, MONTHLY 도 가능

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_spend);

        // 🔹 1️⃣ Intent 데이터 받기
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");
        cycleType = intent.getStringExtra("cycleType"); // "DAILY", "WEEKLY", "MONTHLY"

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        long memberId = prefs.getLong("memberId", -1L);
        if (memberId == -1L) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 2️⃣ 뷰 연결
        groupMainTitle = findViewById(R.id.group_main_title);
        groupGoalView = findViewById(R.id.group_goal_view);
        goalInputEditText = findViewById(R.id.goal_input_edittext);
        goalInputButton = findViewById(R.id.goal_input_button);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        // 🔹 3️⃣ TextView에 데이터 표시
        if (groupName != null) groupMainTitle.setText(groupName);
        if (groupGoal != null) groupGoalView.setText(groupGoal);

        // 🔹 4️⃣ RecyclerView 세팅
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        updatePieChart(0f);
        dailySpendMap.put(getTodayDate(), 0f);
        updateBarChart();

        // 🔹 5️⃣ 금액 입력 버튼 동작
        goalInputButton.setOnClickListener(v -> {
            String inputText = goalInputEditText.getText().toString().trim();
            if (inputText.isEmpty()) {
                Toast.makeText(this, "금액을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long amount = Long.parseLong(inputText);
                String today = getTodayDate();

                // ✅ 차트 갱신
                dailySpendMap.put(today, (float) amount);
                updatePieChart(100f);
                updateBarChart();

                // ✅ 서버 전송
                SaveSpendRequest request = new SaveSpendRequest();
                request.setGroupId(groupId);
                request.setMemberId(memberId);
                request.setAmount(amount);
                request.setRecordDate(today);

                Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
                api.saveSpend(request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(GroupMainSpendActivity.this,
                                    amount + "원 기록 완료!", Toast.LENGTH_SHORT).show();
                            // 🔹 랭킹 새로 불러오기
                            loadRanking(api, memberId);
                        } else {
                            Toast.makeText(GroupMainSpendActivity.this,
                                    "서버 응답 오류 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(GroupMainSpendActivity.this,
                                "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                goalInputEditText.setText("");

            } catch (NumberFormatException e) {
                Toast.makeText(this, "숫자를 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 네비게이션 버튼
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));

        // 🔹 첫 로딩 시 랭킹 가져오기
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        loadRanking(api, memberId);
    }

    // ✅ 랭킹 불러오기 (주기별로)
    private void loadRanking(Retrofit_interface api, long memberId) {
        // 🔸 날짜 범위 계산 (매일/매주/매월)
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
                    // 🔸 적게 쓴 순으로 정렬
                    data.sort(Comparator.comparingDouble(RankingItem::getValue));

                    rankingList.clear();
                    rankingList.addAll(data);
                    rankingAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GroupMainSpendActivity.this, "랭킹 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<RankingItem>> call, Throwable t) {
                Toast.makeText(GroupMainSpendActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
}
