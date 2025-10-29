package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
    private String selectedMainCategory = "";
    private String selectedSubCategory = "";
    private boolean isGoalFrequencySet = false;

    private EditText editTextGoalValue;
    private TextView goalValueLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_make);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        editTextGroupName = findViewById(R.id.edittext_group_name);
        editTextGroupDescription = findViewById(R.id.edittext_group_description);
        buttonStartDate = findViewById(R.id.button_start_date);
        buttonAlarmSetting = findViewById(R.id.button_alarm_setting);
        buttonCreateGroup = findViewById(R.id.button_create_group);
        selectedCategory = findViewById(R.id.selected_category);
        buttonGroupNameCheck = findViewById(R.id.button_group_name_check);
        editTextGoalValue = findViewById(R.id.edittext_goal_value);
        goalValueLabel = findViewById(R.id.goal_value_label);

        buttonCreateGroup.setEnabled(false);
        buttonCreateGroup.setAlpha(0.5f);

        // 날짜 선택
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

        // 그룹 이름 중복 확인
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
                            Toast.makeText(GroupMakeActivity.this, "이미 존재하는 그룹 이름입니다.", Toast.LENGTH_SHORT).show();
                        } else if (response.isSuccessful()) {
                            Toast.makeText(GroupMakeActivity.this, "사용 가능한 이름입니다.", Toast.LENGTH_SHORT).show();
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

        buttonAlarmSetting.setOnClickListener(v -> showAlarmPopup());

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

            // 로그인한 memberId
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            long memberId = prefs.getLong("memberId", -1L);
            if (memberId == -1L) {
                Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
                return;
            }

            // 알림 설정 → 주기 일수 변환
            int cycleDays;
            switch (selectedAlarmSetting) {
                case "매일": cycleDays = 1; break;
                case "매주": cycleDays = 7; break;
                case "매월": cycleDays = 30; break;
                default: cycleDays = 1;
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
            int finalGoalValue = goalValue;

            api.createGroup(dto, memberId).enqueue(new Callback<BuddyGroup>() {
                @Override
                public void onResponse(Call<BuddyGroup> call, Response<BuddyGroup> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BuddyGroup created = response.body();
                        Toast.makeText(GroupMakeActivity.this, "그룹 생성 완료!", Toast.LENGTH_SHORT).show();

                        Intent intent;

                        // 카테고리 분기 — 기존 로직 유지 (세부 카테고리 그대로)
                        if ("다이어트".equals(selectedMainCategory)) {
                            if ("만보기".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainStepActivity.class);
                            } else if ("섭취 칼로리".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainIntakeActivity.class);
                            } else if ("운동 칼로리".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainBurnedActivity.class);
                            } else if ("몸무게".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainWeightActivity.class);
                            } else {
                                intent = new Intent(GroupMakeActivity.this, GroupMainActivity.class);
                            }

                        } else if ("재테크".equals(selectedMainCategory)) {
                            if ("부수입".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainSideHustleActivity.class);
                            } else if ("가계부".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainBudgetBookActivity.class);
                            } else if ("소비".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainSpendActivity.class);
                            } else if ("저축".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainSavingsActivity.class);
                            } else {
                                intent = new Intent(GroupMakeActivity.this, GroupMainActivity.class);
                            }

                        } else if ("공부".equals(selectedMainCategory)) {
                            if ("학습 시간".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainStudyTimeActivity.class);
                            } else if ("문제 풀이 수".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainStudyProgressActivity.class);
                            } else if ("복습 체크".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainReviewCheckActivity.class);
                            } else if ("목표 점수".equals(selectedSubCategory)) {
                                intent = new Intent(GroupMakeActivity.this, GroupMainGoalScoreActivity.class);
                            } else {
                                intent = new Intent(GroupMakeActivity.this, GroupMainActivity.class);
                            }

                        } else if ("독서".equals(selectedMainCategory) && "목표 권수".equals(selectedSubCategory)) {
                            intent = new Intent(GroupMakeActivity.this, GroupMainGoalBooksActivity.class);

                        } else {
                            intent = new Intent(GroupMakeActivity.this, GroupMainActivity.class);
                        }

                        // 목표 문구 — 카테고리/세부카테고리별 단위
                        String goalText;
                        if ("공부".equals(selectedMainCategory) && "학습 시간".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "분 목표";
                        } else if ("재테크".equals(selectedMainCategory) && "소비".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "원 절약 목표";
                        } else if ("재테크".equals(selectedMainCategory) && "저축".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "원 저축 목표";
                        } else if ("다이어트".equals(selectedMainCategory) && "만보기".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "보 목표";
                        } else if ("다이어트".equals(selectedMainCategory) && "섭취 칼로리".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "kcal 목표";
                        } else if ("다이어트".equals(selectedMainCategory) && "운동 칼로리".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "kcal 소모 목표";
                        } else if ("다이어트".equals(selectedMainCategory) && "몸무게".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "kg 목표";
                        } else if ("독서".equals(selectedMainCategory) && "목표 권수".equals(selectedSubCategory)) {
                            goalText = finalGoalValue + "권 목표";
                        } else {
                            goalText = String.valueOf(finalGoalValue);
                        }

                        // 다음 액티비티로 전달할 공통 데이터들
                        intent.putExtra("groupId", created.getId());           // ✅ 서버에서 받은 그룹 ID
                        intent.putExtra("memberId", memberId);                 // ✅ 로그인한 멤버 ID
                        intent.putExtra("groupName", groupName);
                        intent.putExtra("groupGoal", goalText);
                        intent.putExtra("startDate", selectedStartDate);
                        intent.putExtra("alarmSetting", selectedAlarmSetting);
                        intent.putExtra("goalSubtitle", selectedSubCategory);
                        intent.putExtra("groupDesc", groupDesc);

                        // 주기 타입도 전달 (필요한 화면에서 사용)
                        String cycleType = selectedAlarmSetting.equals("매주") ? "WEEKLY"
                                : selectedAlarmSetting.equals("매월") ? "MONTHLY" : "DAILY";
                        intent.putExtra("cycleType", cycleType);

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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String groupName = editTextGroupName.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    enableCreateGroupButton();
                } else {
                    disableCreateGroupButton();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ✅ 카테고리 버튼 — 세부 카테고리 팝업(네가 준 그대로 유지)
        findViewById(R.id.category_btn_1).setOnClickListener(v -> {
            selectedMainCategory = "다이어트";
            selectedCategory.setText("선택된 카테고리: " + selectedMainCategory);
            showSubCategoryPopup();
        });

        findViewById(R.id.category_btn_2).setOnClickListener(v -> {
            selectedMainCategory = "재테크";
            selectedCategory.setText("선택된 카테고리: " + selectedMainCategory);
            showFinanceSubCategoryPopup();
        });

        findViewById(R.id.category_btn_3).setOnClickListener(v -> {
            selectedMainCategory = "공부";
            selectedCategory.setText("선택된 카테고리: " + selectedMainCategory);
            showStudySubCategoryPopup();
        });

        findViewById(R.id.category_btn_4).setOnClickListener(v -> {
            selectedMainCategory = "독서";
            selectedCategory.setText("선택된 카테고리: " + selectedMainCategory);
            showReadingSubCategoryPopup();
        });
    }

    // ✅ 다이어트 세부 카테고리 (네 코드 유지)
    private void showSubCategoryPopup() {
        String[] items = {"만보기", "섭취 칼로리", "운동 칼로리", "몸무게"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세부 카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            selectedSubCategory = items[which];
            Toast.makeText(this, selectedSubCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            selectedCategory.setText("카테고리: " + selectedMainCategory + " / 목표: " + selectedSubCategory);

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
            } else if ("몸무게".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 50kg");
            } else {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.GONE);
            }

            enableCreateGroupButton();
        });
        builder.show();
    }

    // ✅ 재테크 세부 카테고리 (네 코드 유지)
    private void showFinanceSubCategoryPopup() {
        String[] items = {"저축", "소비", "가계부", "부수입"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세부 카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            selectedSubCategory = items[which];
            Toast.makeText(this, selectedSubCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            selectedCategory.setText("카테고리: " + selectedMainCategory + " / 목표: " + selectedSubCategory);

            if ("저축".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 10,000");
            } else if ("소비".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 30,000");
            } else if ("가계부".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.GONE);
            } else if ("부수입".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 700,000");
            } else {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.GONE);
            }

            enableCreateGroupButton();
        });
        builder.show();
    }

    // ✅ 공부 세부 카테고리 (네 코드 유지)
    private void showStudySubCategoryPopup() {
        String[] items = {"학습 시간", "문제 풀이 수", "복습 체크", "목표 점수"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세부 카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            selectedSubCategory = items[which];
            Toast.makeText(this, selectedSubCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            selectedCategory.setText("카테고리: " + selectedMainCategory + " / 목표: " + selectedSubCategory);

            if ("학습 시간".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 90 (분)");
            } else if ("문제 풀이 수".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 50 (문제)");
            } else if ("복습 체크".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.GONE);
            } else if ("목표 점수".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 90 (점)");
            } else {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.GONE);
            }

            enableCreateGroupButton();
        });
        builder.show();
    }

    // ✅ 독서 세부 카테고리 (네 코드 유지)
    private void showReadingSubCategoryPopup() {
        String[] items = {"목표 권수", "목표 시간", "읽은 시간"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("세부 카테고리 선택");
        builder.setItems(items, (dialog, which) -> {
            selectedSubCategory = items[which];
            Toast.makeText(this, selectedSubCategory + " 선택됨", Toast.LENGTH_SHORT).show();
            selectedCategory.setText("카테고리: " + selectedMainCategory + " / 목표: " + selectedSubCategory);

            if ("목표 권수".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.VISIBLE);
                editTextGoalValue.setHint("예: 3권");
            } else if ("목표 시간".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
            } else if ("읽은 시간".equals(selectedSubCategory)) {
                goalValueLabel.setVisibility(View.VISIBLE);
                editTextGoalValue.setVisibility(View.VISIBLE);
            } else {
                goalValueLabel.setVisibility(View.GONE);
                editTextGoalValue.setVisibility(View.GONE);
            }

            enableCreateGroupButton();
        });
        builder.show();
    }

    private void enableCreateGroupButton() {
        if (!selectedSubCategory.isEmpty() && !selectedStartDate.isEmpty() && isGoalFrequencySet) {
            buttonCreateGroup.setEnabled(true);
            buttonCreateGroup.setAlpha(1f);
        }
    }

    private void disableCreateGroupButton() {
        buttonCreateGroup.setEnabled(false);
        buttonCreateGroup.setAlpha(0.5f);
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
