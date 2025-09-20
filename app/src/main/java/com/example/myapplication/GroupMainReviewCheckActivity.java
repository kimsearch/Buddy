package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatCheckBox;
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

public class GroupMainReviewCheckActivity extends AppCompatActivity {

    private TextView groupMainTitle;
    private ImageButton backButton;
    private AppCompatImageButton notificationButton1, notificationButton2, notificationButton3;

    private RecyclerView rankingRecyclerView;
    private BarChart barChart;

    private TextView subjectText;                 // @id/subject_select_card
    private AppCompatImageButton subjectExpand;   // @id/btn_subject_expand
    private AppCompatCheckBox cbReviewConcept;    // @id/cb_review_concept
    private AppCompatCheckBox cbReviewProblems;   // @id/cb_review_problems
    private AppCompatCheckBox cbReviewWrongs;     // @id/cb_review_wrongs
    private EditText etReviewMemo;                // @id/et_review_memo
    private android.widget.Button btnSaveReview;  // @id/btn_save_review

    private AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    private final List<RankingItem> rankingList = new ArrayList<>();
    private RankingAdapter rankingAdapter;

    private static final String[] SUBJECTS = {"수학", "영어", "국어", "과학", "사회"};

    // ===== 서버 호출에 필요한 것들 =====
    private Retrofit_interface api;
    private long groupId = -1L, memberId = -1L;
    private int cycleDays = 1;            // 기본 1일 주기 (인텐트 없을 때 대비)
    private String startDateStr = null;   // "yyyy-MM-dd"
    private String cycleStartStr, cycleEndStr; // "yyyy-MM-dd"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main_review_check);

        // ====== findViewById ======
        groupMainTitle      = findViewById(R.id.group_main_title);

        backButton          = findViewById(R.id.back_button);
        notificationButton1 = findViewById(R.id.notification_button_1);
        notificationButton2 = findViewById(R.id.notification_button_2);
        notificationButton3 = findViewById(R.id.notification_button_3);

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        barChart            = findViewById(R.id.barChart);

        subjectText         = findViewById(R.id.subject_select_card);
        subjectExpand       = findViewById(R.id.btn_subject_expand);
        cbReviewConcept     = findViewById(R.id.cb_review_concept);
        cbReviewProblems    = findViewById(R.id.cb_review_problems);
        cbReviewWrongs      = findViewById(R.id.cb_review_wrongs);
        etReviewMemo        = findViewById(R.id.et_review_memo);
        btnSaveReview       = findViewById(R.id.btn_save_review);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        // ====== Retrofit ======
        api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // ====== Intent / Prefs ======
        Intent intent   = getIntent();
        String groupName = intent.getStringExtra("groupName");
        if (groupName != null) groupMainTitle.setText(groupName);

        groupId      = intent.getLongExtra("groupId", -1L);
        cycleDays    = intent.getIntExtra("cycleDays", 7);            // 없으면 7로 가정
        startDateStr = intent.getStringExtra("startDate");            // "yyyy-MM-dd" (없을 수도 있음)

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = prefs.getLong("memberId", -1L);

        // ====== 주기 계산 (startDate 없으면 오늘 하루 주기로 기록) ======
        computeCurrentCycle(startDateStr, cycleDays);

        // ====== 헤더 버튼 ======
        backButton.setOnClickListener(v -> finish());

        notificationButton1.setOnClickListener(v -> {
            Intent i = new Intent(this, GroupMemberActivity.class);
            i.putExtra("groupId", groupId);
            startActivity(i);
        });
        notificationButton2.setOnClickListener(v ->
                startActivity(new Intent(this, GroupCommunityActivity.class))
        );
        notificationButton3.setOnClickListener(v ->
                startActivity(new Intent(this, AlarmPageActivity.class))
        );

        // ====== 하단 네비 ======
        navHome  .setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup .setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navPet   .setOnClickListener(v -> startActivity(new Intent(this, PetActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));

        rankingAdapter = new RankingAdapter(this, rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(rankingAdapter);
        // ====== 과목 선택 ======
        subjectExpand.setOnClickListener(v -> showSubjectPicker());
        subjectText  .setOnClickListener(v -> showSubjectPicker());

        // ====== 저장(=성공) 버튼 ======
        btnSaveReview.setOnClickListener(v -> submitReviewSuccess());
    }

    // ★★★ “저장 = 성공 처리” 서버 업서트 호출 ★★★
    private void submitReviewSuccess() {
        if (groupId == -1L || memberId == -1L) {
            Toast.makeText(this, "로그인이 필요하거나 그룹 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // (선택) 체크박스/메모 수집은 UI 표시용으로만 사용
        String subject  = subjectText.getText() != null ? subjectText.getText().toString() : "과목";
        boolean concept = cbReviewConcept.isChecked();
        boolean problems= cbReviewProblems.isChecked();
        boolean wrongs  = cbReviewWrongs.isChecked();
        String memo     = etReviewMemo.getText() != null ? etReviewMemo.getText().toString() : "";

        // 성공만 1로 승급시키는 서버 API
        SaveGoalResultRequest body =
                new SaveGoalResultRequest(memberId, cycleStartStr, cycleEndStr, true);

        // 중복 클릭 방지
        btnSaveReview.setEnabled(false);

        api.upsertLog(groupId, body).enqueue(new Callback<GoalLogDto>() {
            @Override public void onResponse(Call<GoalLogDto> call, Response<GoalLogDto> res) {
                if (res.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "✅ 복습 체크 성공으로 기록됐어요!", Toast.LENGTH_SHORT).show();

                    // (선택) 입력값 초기화/잠금
                    // cbReviewConcept.setChecked(false);
                    // cbReviewProblems.setChecked(false);
                    // cbReviewWrongs.setChecked(false);
                    // etReviewMemo.setText("");
                    // btnSaveReview.setEnabled(false); // 이미 성공했으니 계속 잠가두고 싶다면 유지

                } else {
                    Toast.makeText(getApplicationContext(),
                            "저장 실패: " + res.code(), Toast.LENGTH_SHORT).show();
                    btnSaveReview.setEnabled(true);
                }
            }
            @Override public void onFailure(Call<GoalLogDto> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSaveReview.setEnabled(true);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 화면 복귀 시 최신 랭킹/히스토리 동기화
        loadRankingAndBind();
        loadRecordHistoryAndDrawChart(); // ✅ 변환 없이 record-history 재사용
    }

    // 주기 계산: startDate가 있으면 cycleDays 단위로, 없으면 오늘 하루로
    private void computeCurrentCycle(String startDate, int cycleDays) {
        try {
            if (startDate == null || startDate.isEmpty()) {
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(new Date());
                cycleStartStr = today;
                cycleEndStr   = today;
                return;
            }

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

    private void showSubjectPicker() {
        int preSelected = 0;
        new AlertDialog.Builder(this)
                .setTitle("과목 선택")
                .setSingleChoiceItems(SUBJECTS, preSelected, (dialog, which) -> {
                    subjectText.setText(SUBJECTS[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("취소", null)
                .show();
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
}
