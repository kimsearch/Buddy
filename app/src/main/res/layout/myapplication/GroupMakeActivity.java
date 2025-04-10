package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.util.ArrayList;
import java.util.Calendar;

public class GroupMakeActivity extends AppCompatActivity {

    private EditText editTextGroupName, editTextDescription;
    private TextView groupNameHint;
    private AppCompatButton btnGroupNameCheck, btnCategory1, btnStartDate, btnEndDate, btnCreateGroup;
    private ScrollView scrollView;

    // 예시: 기존 그룹 이름 목록 (실제로는 DB 또는 서버에서 가져와야 함)
    private final ArrayList<String> existingGroupNames = new ArrayList<>();

    private String selectedSubCategory = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_make);

        editTextGroupName = findViewById(R.id.edittext_group_name);
        groupNameHint = findViewById(R.id.group_name_hint);
        btnGroupNameCheck = findViewById(R.id.button_group_name_check);
        btnCategory1 = findViewById(R.id.category_btn_1);
        btnStartDate = findViewById(R.id.button_start_date);
        btnEndDate = findViewById(R.id.button_end_date);
        editTextDescription = findViewById(R.id.edittext_group_description);
        btnCreateGroup = findViewById(R.id.button_create_group);
        scrollView = findViewById(R.id.group_make_scroll);

        // 가상 데이터 - 이미 존재하는 그룹 이름들
        existingGroupNames.add("헬스왕");
        existingGroupNames.add("하루만보기");

        // 그룹 이름 확인 버튼 클릭 시
        btnGroupNameCheck.setOnClickListener(v -> {
            String inputName = editTextGroupName.getText().toString().trim();
            if (inputName.isEmpty()) {
                Toast.makeText(this, "그룹 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingGroupNames.contains(inputName)) {
                groupNameHint.setText("⚠ 이미 존재하는 그룹 이름입니다.");
                groupNameHint.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                groupNameHint.setText("✔ 사용 가능한 이름입니다.");
                groupNameHint.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

            hideKeyboard();
        });

        // 카테고리 버튼 클릭
        btnCategory1.setOnClickListener(v -> {
            btnCategory1.setBackgroundColor(0xFFF3BFB9); // 색상 변경
            showSubCategoryPopup();
        });

        // 시작 날짜 선택
        btnStartDate.setOnClickListener(v -> showDateDialog(btnStartDate));

        // 종료 날짜 선택
        btnEndDate.setOnClickListener(v -> showDateDialog(btnEndDate));

        // 키보드 입력 시 하단 가림 방지 스크롤 조정
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });

        // 그룹 만들기 버튼 클릭 시 다음 화면으로 이동
        btnCreateGroup.setOnClickListener(v -> {
            Intent intent = new Intent(GroupMakeActivity.this, GroupMainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showSubCategoryPopup() {
        String[] items = {"만보기", "섭취 칼로리", "운동 칼로리", "식단"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세부 카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            selectedSubCategory = items[which];
            Toast.makeText(this, selectedSubCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            // 예: 텍스트뷰 업데이트 가능
            groupNameHint.setText("선택된 항목: " + selectedSubCategory);
        });
        builder.show();
    }

    private void showDateDialog(AppCompatButton button) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String dateStr = year1 + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                    button.setText(dateStr);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
