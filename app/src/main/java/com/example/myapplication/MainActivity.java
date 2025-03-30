package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView title, progressTitle, groupGoalTitle, personalGoalTitle;
    private Switch weekSwitch;
    private ProgressBar progressBar;
    private ListView groupListView, personalListView;
    private Button addGoalButton;
    private ImageButton navHome, navGroup, navAlarm, navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View 연결
        title = findViewById(R.id.title);
        weekSwitch = findViewById(R.id.week_switch);
        progressBar = findViewById(R.id.progress_bar);
        groupGoalTitle = findViewById(R.id.group_goal_title);
        personalGoalTitle = findViewById(R.id.personal_goal_title);
        groupListView = findViewById(R.id.group_list); // 두 개 ID가 같아서 수정 필요!
        personalListView = findViewById(R.id.personal_list); // 둘 중 하나를 다른 ID로 바꿔야 함!
        addGoalButton = findViewById(R.id.add_goal_button);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navAlarm = findViewById(R.id.nav_alarm);
        navMyPage = findViewById(R.id.nav_mypage);

        // ⚠️ ListView ID 중복 문제 해결 필요!
        // 예시 코드에선 아래처럼 별도 ID로 나누는 게 좋아:
        // group_list, personal_list 로 xml에서 ID 변경 추천

        // 임시 데이터 (목표 리스트)
        String[] groupGoals = {"그룹 목표 1", "그룹 목표 2"};
        String[] personalGoals = {"개인 목표 A", "개인 목표 B"};

        // 어댑터 설정
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupGoals);
        ArrayAdapter<String> personalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personalGoals);

        groupListView.setAdapter(groupAdapter);
        personalListView.setAdapter(personalAdapter);

        // 주 단위 보기 Switch 이벤트
        weekSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "주간 보기 활성화됨" : "주간 보기 비활성화됨";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        // 목표 추가 버튼
        addGoalButton.setOnClickListener(v -> {
            Toast.makeText(this, "목표 추가 화면으로 이동 (구현 필요)", Toast.LENGTH_SHORT).show();
            // 예: startActivity(new Intent(this, AddGoalActivity.class));
        });

        // 네비게이션 버튼 클릭 이벤트
        navHome.setOnClickListener(v -> Toast.makeText(this, "홈으로 이동", Toast.LENGTH_SHORT).show());
        navGroup.setOnClickListener(v -> Toast.makeText(this, "그룹 화면 이동 (구현 필요)", Toast.LENGTH_SHORT).show());
        navAlarm.setOnClickListener(v -> Toast.makeText(this, "알림 화면 이동 (구현 필요)", Toast.LENGTH_SHORT).show());
        navMyPage.setOnClickListener(v -> Toast.makeText(this, "마이페이지 이동 (구현 필요)", Toast.LENGTH_SHORT).show());
    }
}
