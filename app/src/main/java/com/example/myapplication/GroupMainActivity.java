package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupMainActivity extends AppCompatActivity {

    private TextView groupMainTitle;  // 그룹 이름 표시
    private TextView groupGoalView;   // 그룹 목표 표시
    private EditText goalInputEditText;  // 목표 입력 받는 EditText
    private Button goalInputButton;  // 목표 입력 버튼

    private int totalGoal = 0;   // 목표 누적값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_main);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish(); //
        });

        // UI 연결
        groupMainTitle = findViewById(R.id.group_main_title);  // 그룹 이름
        groupGoalView = findViewById(R.id.group_goal_view);    // 그룹 목표
        goalInputEditText = findViewById(R.id.goal_input_edittext);  // 목표 입력란
        goalInputButton = findViewById(R.id.goal_input_button);  // 목표 입력 버튼

        // Intent로 전달된 그룹 이름 및 목표 받기
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        String groupGoal = intent.getStringExtra("groupGoal");

        // 그룹 이름과 목표 설정
        if (groupName != null) {
            groupMainTitle.setText(groupName);  // 그룹 이름을 텍스트뷰에 설정
        }
        if (groupGoal != null) {
            groupGoalView.setText(groupGoal);  // 그룹 목표를 텍스트뷰에 설정
        }

        // 목표 입력 버튼 클릭 시
        goalInputButton.setOnClickListener(v -> {
            // 입력값을 가져와서 정수로 변환
            String inputGoal = goalInputEditText.getText().toString();
            if (!inputGoal.isEmpty()) {
                try {
                    int inputGoalInt = Integer.parseInt(inputGoal);
                    totalGoal += inputGoalInt;  // 목표 누적 덧셈
                    goalInputEditText.setText("");  // 입력란 비우기
                    // 업데이트된 목표 값 표시
                    groupGoalView.setText("현재 목표: " + totalGoal);
                } catch (NumberFormatException e) {
                    Toast.makeText(GroupMainActivity.this, "숫자를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(GroupMainActivity.this, "목표를 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 이곳에 센서 관련 로직을 추가하지 않음 (만약 필요시 추가)
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 이곳에 센서 리스너 해제 등을 추가하지 않음 (만약 필요시 추가)
    }

    // 목표 업데이트 메소드 (추가 로직)
    private void updateGoalDisplay() {
        groupGoalView.setText("현재 목표: " + totalGoal);
    }
}
