package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class MyPageMainActivity extends AppCompatActivity {

    AppCompatImageButton settingsButton, delete1Button, delete2Button;
    AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;
    TextView nicknameText;
    private LineChart monthlyLineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_main);

        nicknameText = findViewById(R.id.nickname_text);

        // 닉네임 불러오기
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String nickname = prefs.getString("userNickname", "닉네임 없음");
        nicknameText.setText(nickname);

        // 1. 설정 버튼 → mypage_setting.xml
        settingsButton = findViewById(R.id.settings_title);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, MyPageSettingActivity.class);
            startActivity(intent);
        });

        // 2. 삭제 팝업
        delete1Button = findViewById(R.id.history_delete_1);
        delete2Button = findViewById(R.id.history_delete_2);

        delete1Button.setOnClickListener(v -> showDeleteDialog());
        delete2Button.setOnClickListener(v -> showDeleteDialog());

        // 3~5. 네비게이션 바 연결
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, GroupSearchPageActivity.class);
            startActivity(intent);
        });

        navPet.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, PetActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(v -> {
            // 현재 페이지 → 아무 작업 안 함
        });

        // 6. 그래프 초기화
        monthlyLineChart = findViewById(R.id.monthlyLineChart);
        drawMonthlyChart();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);

        builder.setTitle("삭제 확인");
        builder.setMessage("정말 삭제하시겠어요?");

        builder.setPositiveButton("삭제", (dialog, which) -> {
            // TODO: 삭제 로직
            dialog.dismiss();
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // 배경 둥글게 설정
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        // 팝업 내부 글꼴 적용
        Typeface customFont = ResourcesCompat.getFont(this, R.font.bmjua);
        TextView messageView = dialog.findViewById(android.R.id.message);
        TextView titleView = dialog.findViewById(getResources().getIdentifier("alertTitle", "id", "android"));
        if (messageView != null) messageView.setTypeface(customFont);
        if (titleView != null) titleView.setTypeface(customFont);
    }

    private void drawMonthlyChart() {
        LineChart chart = findViewById(R.id.monthlyLineChart);

        // TODO: DB에서 해당 월별 달성률 데이터를 불러와야 함
        List<Entry> personalEntries = new ArrayList<>();
        List<Entry> groupEntries = new ArrayList<>();

        // 예시 데이터 (삭제하고 DB 연동 구현할 것)
        // personalEntries.add(new Entry(0, 70));
        // groupEntries.add(new Entry(0, 60));

        LineDataSet personalDataSet = new LineDataSet(personalEntries, "개인 목표");
        personalDataSet.setColor(Color.parseColor("#FF9800"));
        personalDataSet.setCircleColor(Color.parseColor("#FF9800"));
        personalDataSet.setValueTextColor(Color.BLACK);
        personalDataSet.setLineWidth(2f);
        personalDataSet.setCircleRadius(4f);

        LineDataSet groupDataSet = new LineDataSet(groupEntries, "그룹 목표");
        groupDataSet.setColor(Color.parseColor("#3F51B5"));
        groupDataSet.setCircleColor(Color.parseColor("#3F51B5"));
        groupDataSet.setValueTextColor(Color.BLACK);
        groupDataSet.setLineWidth(2f);
        groupDataSet.setCircleRadius(4f);

        LineData data = new LineData(personalDataSet, groupDataSet);
        chart.setData(data);

        String[] months = {"1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(0);
        xAxis.setTextColor(Color.DKGRAY);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(100f);
        chart.getAxisLeft().setTextColor(Color.DKGRAY);
        chart.getLegend().setTextColor(Color.DKGRAY);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(false);

        chart.invalidate();
    }
}