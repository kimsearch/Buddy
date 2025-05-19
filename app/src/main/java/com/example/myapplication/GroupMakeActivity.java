package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GroupMakeActivity extends AppCompatActivity {

    private EditText editTextGroupName, editTextGroupDescription;
    private Button buttonStartDate, buttonAlarmSetting, buttonCreateGroup;
    private TextView selectedCategory;
    private String selectedAlarmSetting = "설정 안 됨";
    private String selectedStartDate = "";
    private String selectedSubCategory = "";  // 선택된 서브 카테고리
    private boolean isGoalFrequencySet = false; // Track if goal frequency is set

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_make);

        // UI 연결
        editTextGroupName = findViewById(R.id.edittext_group_name);
        editTextGroupDescription = findViewById(R.id.edittext_group_description);
        buttonStartDate = findViewById(R.id.button_start_date);
        buttonAlarmSetting = findViewById(R.id.button_alarm_setting);
        buttonCreateGroup = findViewById(R.id.button_create_group);
        selectedCategory = findViewById(R.id.selected_category);

        // Initially disable the create group button
        buttonCreateGroup.setEnabled(false);
        buttonCreateGroup.setAlpha(0.5f); // Show it as disabled

        // 시작 날짜 버튼 클릭 시
        buttonStartDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        selectedStartDate = sdf.format(calendar.getTime());
                        buttonStartDate.setText(selectedStartDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // 목표 설정 주기 팝업
        buttonAlarmSetting.setOnClickListener(v -> showAlarmPopup());

        // 그룹 만들기 버튼 클릭 시
        buttonCreateGroup.setOnClickListener(v -> {
            String groupName = editTextGroupName.getText().toString().trim();
            String groupDesc = editTextGroupDescription.getText().toString().trim(); // 그룹 설명 가져오기

            if (groupName.isEmpty() || groupDesc.isEmpty()) {
                Toast.makeText(this, "그룹 이름과 설명을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 카테고리 선택에 따른 이동 처리
            Intent intent;

            if ("만보기".equals(selectedSubCategory) || "다이어트".equals(selectedSubCategory)) {
                intent = new Intent(GroupMakeActivity.this, GroupMainStepActivity.class);
            } else {
                intent = new Intent(GroupMakeActivity.this, GroupMainActivity.class);
            }

            // 인텐트로 데이터를 전달
            intent.putExtra("groupName", groupName);
            intent.putExtra("startDate", selectedStartDate); // 시작 날짜
            intent.putExtra("alarmSetting", selectedAlarmSetting); // 알림 설정
            intent.putExtra("goalSubtitle", selectedSubCategory); // 목표 서브타이틀 (만보기, 다이어트 등)
            intent.putExtra("groupDesc", groupDesc); // 그룹 설명
            startActivity(intent);
            finish();
        });

        // 그룹 이름에 대한 TextWatcher 추가
        editTextGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String groupName = editTextGroupName.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    enableCreateGroupButton();
                } else {
                    disableCreateGroupButton();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // 카테고리 버튼 클릭 시
        findViewById(R.id.category_btn_1).setOnClickListener(v -> {
            showSubCategoryPopup();
        });
    }

    // 카테고리 선택 팝업
    private void showSubCategoryPopup() {
        String[] items = {"만보기", "섭취 칼로리", "운동 칼로리", "식단"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세부 카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            selectedSubCategory = items[which];
            Toast.makeText(this, selectedSubCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            // 선택된 카테고리 텍스트 뷰 업데이트
            selectedCategory.setText("선택된 카테고리: " + selectedSubCategory);

            // 선택된 카테고리가 '만보기'일 경우 그룹 목표 입력란을 '걸음 수' 목표로 설정
            if ("만보기".equals(selectedSubCategory)) {
                editTextGroupDescription.setHint("걸음 수 목표 입력");
            } else if ("다이어트".equals(selectedSubCategory)) {
                editTextGroupDescription.setHint("섭취 칼로리 목표 입력");
            }

            // Enable the create group button only when category is selected
            enableCreateGroupButton();
        });
        builder.show();
    }

    private void enableCreateGroupButton() {
        if (!selectedSubCategory.isEmpty()) {
            buttonCreateGroup.setEnabled(true);
            buttonCreateGroup.setAlpha(1f);  // Make it visible
        }
    }

    private void disableCreateGroupButton() {
        buttonCreateGroup.setEnabled(false);
        buttonCreateGroup.setAlpha(0.5f); // Show it as disabled
    }

    private void showAlarmPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림 설정");

        String[] alarmOptions = {"안 함", "매일", "매주", "매월"};
        builder.setItems(alarmOptions, (dialog, which) -> {
            selectedAlarmSetting = alarmOptions[which];
            buttonAlarmSetting.setText(selectedAlarmSetting);

            // Check if the goal frequency is set to any option other than "안 함"
            if (!"안 함".equals(selectedAlarmSetting)) {
                isGoalFrequencySet = true;
                enableCreateGroupButton();  // Enable the button once the frequency is set
            } else {
                isGoalFrequencySet = false;
                disableCreateGroupButton(); // Disable the button if frequency is not set
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
