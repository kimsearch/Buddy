package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class GroupMainSavingsActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private TextView groupGoalView;                // 기존: 상단 설명 텍스트
    private EditText goalInputEditText;            // 입력창(저축 금액 입력)
    private Button goalInputButton;                // 입력 버튼

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rankingRecyclerView;

    // ====== [추가] 상단 요약/진행률 표시에 대응하는 뷰(있으면 바인딩, 없으면 null) ======
    private TextView tvTargetAmount;   // @id/savings_target_amount  (예: "목표 1,000,000원")
    private TextView tvSavedAmount;    // @id/savings_saved_amount   (예: "누적 320,000원")
    private TextView tvProgressText;   // @id/savings_progress_text  (예: "진행률 32%")

    // ====== 데이터 ======
    private final Map<String, Float> dailyProgressMap = new LinkedHashMap<>(); // 퍼센트 기록(바차트용)
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    // [추가] 금액 단위 데이터 (원 단위)
    private long targetAmount = 0L; // 목표 금액
    private long savedAmount = 0L;  // 누적 저축 금액

    // 그룹 별 영속화 키
    private String groupName = "GROUP";
    private SharedPreferences prefs;

    private static final String PREF_PREFIX = "savings_";
    private static final String K_SAVED = "savedAmount";
    private static final String K_TARGET = "targetAmount";
    private static final String K_HISTORY = "history"; // JSONArray [{date:"MM/dd", percent:32.5}, ...]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_savings);

        // ====== 기존 바인딩 ======
        groupMainTitle      = findViewById(R.id.group_main_title);
        groupGoalView       = findViewById(R.id.group_goal_view);
        goalInputEditText   = findViewById(R.id.goal_input_edittext);
        goalInputButton     = findViewById(R.id.goal_input_button);
        pieChart            = findViewById(R.id.pieChart);
        barChart            = findViewById(R.id.barChart);
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navMyPage = findViewById(R.id.nav_mypage);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);


        // ====== 인텐트 수신 ======
        Intent intent = getIntent();
        String groupGoalStr = intent.getStringExtra("groupGoal"); // “목표금액” (숫자문자열 추천)
        String extraName    = intent.getStringExtra("groupName");

        if (!TextUtils.isEmpty(extraName)) groupName = extraName;
        if (!TextUtils.isEmpty(extraName)) groupMainTitle.setText(extraName);

        // groupGoalView(기존 표시 텍스트)는 그대로 유지
        if (!TextUtils.isEmpty(groupGoalStr)) groupGoalView.setText(groupGoalStr);

        // 목표금액 파싱 (숫자만 추출)
        targetAmount = parseLongSafely(groupGoalStr);

        // ====== SharedPreferences 로드 (그룹별) ======
        prefs = getSharedPreferences(PREF_PREFIX + groupName, MODE_PRIVATE);
        if (targetAmount == 0L) {
            // 저장된 목표가 있으면 사용 (그룹 생성 시점에 못 받았을 수도 있으니)
            targetAmount = prefs.getLong(K_TARGET, 0L);
        }
        savedAmount = prefs.getLong(K_SAVED, 0L);

        // ====== 랭킹 RecyclerView (기존) ======
        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);

        // ====== 파이/바 차트 초기화 ======
        if (dailyProgressMap.isEmpty()) {
            // 저장된 history 복구 → 없으면 오늘 0%
            restoreHistoryOrInitToday();
        }
        updateAllChartsAndHeaders(); // 상단요약 + 파이 + 바차트 동시 갱신

        // ====== 입력 버튼: "저축 금액" 추가 ======
        goalInputButton.setOnClickListener(v -> {
            String input = goalInputEditText.getText().toString().trim();
            if (TextUtils.isEmpty(input)) {
                Toast.makeText(this, "저축 금액을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                long add = parseLongSafely(input);
                if (add <= 0) {
                    Toast.makeText(this, "0보다 큰 금액을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                savedAmount += add;

                // 오늘 진행률(%) 계산해서 history 갱신
                float pct = calculatePercent(savedAmount, targetAmount);
                dailyProgressMap.put(getTodayDate(), pct);

                // 저장
                persistAll();

                // UI 갱신
                updateAllChartsAndHeaders();
                goalInputEditText.setText("");
                Toast.makeText(this,
                        "저축 완료: " + withComma(add) + "원 (누적 " + withComma(savedAmount) + "원)",
                        Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "숫자를 정확히 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // ====== 하단 네비 (기존) ======
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet.setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
    }

    // ====== 전체 UI 갱신 (상단요약 + 파이 + 바) ======
    private void updateAllChartsAndHeaders() {
        // 상단 요약 텍스트 (XML에 없으면 자동 무시)
        if (tvTargetAmount != null) tvTargetAmount.setText("목표 " + withComma(targetAmount) + "원");
        if (tvSavedAmount  != null) tvSavedAmount.setText("누적 " + withComma(savedAmount) + "원");

        float percent = calculatePercent(savedAmount, targetAmount);
        if (tvProgressText != null) tvProgressText.setText("진행률 " + Math.round(percent) + "%");

        // 파이차트/바차트 갱신
        updatePieChart(percent);
        updateBarChart();
    }

    // ====== 퍼센트 계산 (0~100) ======
    private float calculatePercent(long saved, long target) {
        if (target <= 0) return 0f;
        float p = (saved * 100f) / (float) target;
        return Math.max(0f, Math.min(100f, p));
    }

    // ====== 파이차트 (기존 구조 유지) ======
    private void updatePieChart(float value) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(value, "달성"));
        entries.add(new PieEntry(100 - value, "남은 목표"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(
                value >= 80 ? Color.rgb(76, 175, 80)
                        : value >= 50 ? Color.rgb(255, 193, 7)
                        : Color.rgb(244, 67, 54),
                Color.LTGRAY
        );

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

    // ====== 바차트 (기존 구조 유지) ======
    private void updateBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Float> entry : dailyProgressMap.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
            labels.add(entry.getKey());
        }

        BarDataSet dataSet = new BarDataSet(entries, "누적 기록");
        dataSet.setColors(Color.parseColor("#FF9800"));
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

    // ====== 히스토리 복원(없으면 오늘 0%) ======
    private void restoreHistoryOrInitToday() {
        String json = prefs.getString(K_HISTORY, null);
        if (json == null) {
            dailyProgressMap.put(getTodayDate(), calculatePercent(savedAmount, targetAmount));
            return;
        }
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String date = o.optString("date", getTodayDate());
                float pct = (float) o.optDouble("percent", 0.0);
                dailyProgressMap.put(date, pct);
            }
        } catch (JSONException e) {
            dailyProgressMap.clear();
            dailyProgressMap.put(getTodayDate(), calculatePercent(savedAmount, targetAmount));
        }
    }

    // ====== 전체 데이터 저장 ======
    private void persistAll() {
        SharedPreferences.Editor ed = prefs.edit();
        ed.putLong(K_SAVED, savedAmount);
        ed.putLong(K_TARGET, targetAmount);

        // history 저장
        JSONArray arr = new JSONArray();
        for (Map.Entry<String, Float> e : dailyProgressMap.entrySet()) {
            JSONObject o = new JSONObject();
            try {
                o.put("date", e.getKey());
                o.put("percent", e.getValue());
                arr.put(o);
            } catch (JSONException ignored) {}
        }
        ed.putString(K_HISTORY, arr.toString());
        ed.apply();
    }

    // ====== 유틸 ======
    private String getTodayDate() {
        return new SimpleDateFormat("MM/dd", Locale.getDefault()).format(new Date());
    }

    private long parseLongSafely(String s) {
        if (TextUtils.isEmpty(s)) return 0L;
        // 숫자만 추출 (콤마/원/공백 등 제거)
        String only = s.replaceAll("[^0-9]", "");
        if (TextUtils.isEmpty(only)) return 0L;
        try { return Long.parseLong(only); } catch (Exception e) { return 0L; }
    }

    private String withComma(long v) {
        return NumberFormat.getInstance(Locale.KOREA).format(v);
    }

    private <T> T findViewByIdSafe(int id) {
        try {
            //noinspection unchecked
            return (T) findViewById(id);
        } catch (Exception e) { return null; }
    }
}
