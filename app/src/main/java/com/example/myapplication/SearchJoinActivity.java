package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchJoinActivity extends AppCompatActivity {

    private Long groupId;
    private String groupName;
    private String category;
    private String goalType;
    private String startDate;
    private int memberCount;
    private String description;
    private String leaderNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_join);

        // 🔙 뒤로가기 버튼
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // ✅ Intent 값 받기
        groupId = getIntent().getLongExtra("groupId", -1);
        groupName = getIntent().getStringExtra("groupName");
        category = getIntent().getStringExtra("category");
        goalType = getIntent().getStringExtra("goalType"); // ✅ 세부 카테고리 추가
        startDate = getIntent().getStringExtra("startDate");
        memberCount = getIntent().getIntExtra("memberCount", 0);
        description = getIntent().getStringExtra("description");
        leaderNickname = getIntent().getStringExtra("leaderNickname");

        // ✅ 뷰 연결
        TextView groupNameText = findViewById(R.id.group_name_text);
        TextView categoryText = findViewById(R.id.category_text);
        TextView memberCountText = findViewById(R.id.member_count_text);
        TextView startDateText = findViewById(R.id.start_date_text);
        TextView nicknameText = findViewById(R.id.nickname_text);
        TextView descriptionText = findViewById(R.id.group_introduction);
        AppCompatButton joinButton = findViewById(R.id.button_join_group);

        // ✅ 데이터 표시
        groupNameText.setText(groupName != null ? groupName : "");
        categoryText.setText(category != null ? category : "");
        memberCountText.setText("참여자 " + memberCount);
        startDateText.setText("시작 날짜 " + (startDate != null ? startDate : ""));
        nicknameText.setText(leaderNickname != null ? leaderNickname : "");
        descriptionText.setText(description != null ? description : "");

        // ✅ 참가 요청 처리
        joinButton.setOnClickListener(v -> {
            Long memberId = getMyMemberId();

            if (groupId == -1 || memberId == -1) {
                Toast.makeText(this, "유효하지 않은 요청입니다", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
            api.sendJoinRequest(groupId, memberId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SearchJoinActivity.this, "그룹에 참여했습니다!", Toast.LENGTH_SHORT).show();
                        joinButton.setEnabled(false);
                        joinButton.setText("참여 완료");
                        goToGroupMainActivity();
                    } else if (response.code() == 409) {
                        Toast.makeText(SearchJoinActivity.this, "이미 참여 중인 그룹입니다.", Toast.LENGTH_SHORT).show();
                        goToGroupMainActivity();
                    } else {
                        Toast.makeText(SearchJoinActivity.this, "참여 실패 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(SearchJoinActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ✅ 로그인한 사용자 ID 꺼내기
    private Long getMyMemberId() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return prefs.getLong("memberId", -1L);
    }

    // ✅ 그룹별 메인 액티비티로 이동
    private void goToGroupMainActivity() {
        String key = (goalType != null && !goalType.isEmpty()) ? goalType : category;
        Intent intent;

        if ("만보기".equals(key)) {
            intent = new Intent(this, GroupMainStepActivity.class);
        } else if ("섭취 칼로리".equals(key)) {
            intent = new Intent(this, GroupMainIntakeActivity.class);
        } else if ("운동 칼로리".equals(key)) {
            intent = new Intent(this, GroupMainBurnedActivity.class);
        } else if ("몸무게".equals(key)) {
            intent = new Intent(this, GroupMainWeightActivity.class);
        } else if ("학습 시간".equals(key)) {
            intent = new Intent(this, GroupMainStudyTimeActivity.class);
        } else if ("문제 풀이 수".equals(key)) {
            intent = new Intent(this, GroupMainStudyProgressActivity.class);
        } else if ("복습 체크".equals(key)) {
            intent = new Intent(this, GroupMainReviewCheckActivity.class);
        } else if ("목표 점수".equals(key)) {
            intent = new Intent(this, GroupMainGoalScoreActivity.class);
        } else if ("목표 권수".equals(key)) {
            intent = new Intent(this, GroupMainGoalBooksActivity.class);
        } else if ("목표 시간".equals(key)) {
            intent = new Intent(this, GroupMainGoalMinutesActivity.class);
        } else if ("읽은 시간".equals(key)) {
            intent = new Intent(this, GroupMainTimeLogActivity.class);
        } else {
            intent = new Intent(this, GroupMainActivity.class);
        }

        // ✅ 공통 데이터 전달
        intent.putExtra("groupId", groupId);
        intent.putExtra("groupName", groupName);
        intent.putExtra("groupGoal", goalType);
        startActivity(intent);
        finish();
    }
}
