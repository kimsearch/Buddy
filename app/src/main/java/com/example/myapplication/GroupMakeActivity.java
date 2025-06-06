package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupMakeActivity extends AppCompatActivity {

    private EditText editTextGroupName, editTextGroupDescription;
    private Button buttonStartDate, buttonAlarmSetting, buttonCreateGroup;
    private TextView selectedCategory;

    private Button buttonGroupNameCheck;

    private String selectedAlarmSetting = "설정 안 됨";
    private String selectedStartDate = "";
    private String selectedMainCategory = ""; //선택된 카테고리
    private String selectedSubCategory = "";  // 선택된 서브 카테고리
    private boolean isGoalFrequencySet = false; // Track if goal frequency is set

    private EditText editTextGoalValue;
    private TextView goalValueLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_make);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); //
        });

        // UI 연결
        editTextGroupName = findViewById(R.id.edittext_group_name);
        editTextGroupDescription = findViewById(R.id.edittext_group_description);
        buttonStartDate = findViewById(R.id.button_start_date);
        buttonAlarmSetting = findViewById(R.id.button_alarm_setting);
        buttonCreateGroup = findViewById(R.id.button_create_group);
        selectedCategory = findViewById(R.id.selected_category);
        buttonGroupNameCheck = findViewById(R.id.button_group_name_check);
        editTextGoalValue = findViewById(R.id.edittext_goal_value);
        goalValueLabel = findViewById(R.id.goal_value_label);



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

        buttonGroupNameCheck = findViewById(R.id.button_group_name_check);
        editTextGroupName = findViewById(R.id.edittext_group_name);


        buttonGroupNameCheck.setOnClickListener(v -> {
            String nameToCheck = editTextGroupName.getText().toString().trim();

            if (nameToCheck.isEmpty()) {
                Toast.makeText(this, "그룹 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
            api.checkGroupName(nameToCheck).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 409) {
                            // 이미 존재하는 이름
                            Toast.makeText(GroupMakeActivity.this, "이미 존재하는 그룹 이름입니다.", Toast.LENGTH_SHORT).show();
                        } else if (response.isSuccessful() && response.body() != null) {
                            // 사용 가능한 이름
                            String msg = response.body().string();
                            Toast.makeText(GroupMakeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GroupMakeActivity.this, "오류 발생: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(GroupMakeActivity.this, "응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(GroupMakeActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });




        // 목표 설정 주기 팝업
        buttonAlarmSetting.setOnClickListener(v -> showAlarmPopup());

        // 그룹 만들기 버튼 클릭 시
        buttonCreateGroup.setOnClickListener(v -> {
            String groupName = editTextGroupName.getText().toString().trim();
            String groupDesc = editTextGroupDescription.getText().toString().trim();
            Integer goalValue = 0;
            if (editTextGoalValue.getVisibility() == View.VISIBLE) {
                String input = editTextGoalValue.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(this, "목표 수치를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    goalValue = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "숫자만 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (groupName.isEmpty() || groupDesc.isEmpty()) {
                Toast.makeText(this, "그룹 이름과 설명을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedStartDate.isEmpty()) {
                Toast.makeText(this, "시작 날짜를 선택하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // SharedPreferences에서 memberId 가져오기
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            Long memberId = prefs.getLong("memberId", -1L);
            if (memberId == -1L) {
                Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
                return;
            }

            //  알림 설정 → 숫자 주기로 변환
            int cycleDays;
            switch (selectedAlarmSetting) {
                case "매일": cycleDays = 1; break;
                case "매주": cycleDays = 7; break;
                case "매월": cycleDays = 30; break;
                default: cycleDays = 1; // 기본값 보호
            }

            Log.d("DTO_DEBUG", "name: " + groupName
                    + ", category: " + selectedMainCategory
                    + ", description: " + groupDesc
                    + ", goalType: " + selectedSubCategory
                    + ", cycleDays: " + cycleDays);

            BuddyGroupDto dto = new BuddyGroupDto(
                    groupName,
                    selectedMainCategory,
                    cycleDays,
                    groupDesc,
                    selectedSubCategory,
                    selectedStartDate,
                    goalValue
            );

            Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
            api.createGroup(dto, memberId).enqueue(new Callback<BuddyGroup>() {
                @Override
                public void onResponse(Call<BuddyGroup> call, Response<BuddyGroup> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(GroupMakeActivity.this, "그룹 생성 완료!", Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if ("만보기".equals(selectedSubCategory) || "다이어트".equals(selectedSubCategory)) {
                            intent = new Intent(GroupMakeActivity.this, GroupMainStepActivity.class);
                        } else {
                            intent = new Intent(GroupMakeActivity.this, GroupMainActivity.class);
                        }

                        intent.putExtra("groupName", groupName);
                        intent.putExtra("startDate", selectedStartDate);
                        intent.putExtra("alarmSetting", selectedAlarmSetting);
                        intent.putExtra("goalSubtitle", selectedSubCategory);
                        intent.putExtra("groupDesc", groupDesc);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(GroupMakeActivity.this, "그룹 생성 실패 (서버 오류)", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BuddyGroup> call, Throwable t) {
                    Toast.makeText(GroupMakeActivity.this, "연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

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

        findViewById(R.id.category_btn_1).setOnClickListener(v -> {
            selectedMainCategory = "다이어트"; // 전역 변수에 대입
            selectedCategory.setText("선택된 카테고리: " + selectedMainCategory);
            showSubCategoryPopup();
        });
    }

    private void showSubCategoryPopup() {
        String[] items = {"만보기", "섭취 칼로리", "운동 칼로리", "식단"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세부 카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            selectedSubCategory = items[which];
            Toast.makeText(this, selectedSubCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            selectedCategory.setText("카테고리: " + selectedMainCategory + " / 목표: " + selectedSubCategory);

            // 힌트는 그대로 유지 (기존 코드)
            if ("만보기".equals(selectedSubCategory)) {
                editTextGroupDescription.setHint("걸음 수 목표 입력");
            } else if ("다이어트".equals(selectedSubCategory)) {
                editTextGroupDescription.setHint("섭취 칼로리 목표 입력");
            }

            // [✅ 새로 추가] 목표 수치 입력란 보여주기
            if ("만보기".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 10000보");
            } else if ("섭취 칼로리".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 1800kcal");
            } else if ("운동 칼로리".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 500kcal");
            } else {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.GONE);
            }

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

        String[] alarmOptions = {"매일", "매주", "매월"};
        builder.setItems(alarmOptions, (dialog, which) -> {
            selectedAlarmSetting = alarmOptions[which];
            buttonAlarmSetting.setText(selectedAlarmSetting);

            isGoalFrequencySet = true;
            enableCreateGroupButton();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
