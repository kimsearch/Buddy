package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchJoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_join);

        // 🔙 뒤로가기 버튼
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // ✅ Intent로 전달받은 값 꺼내기
        String groupName = getIntent().getStringExtra("groupName");
        String category = getIntent().getStringExtra("category");
        String startDate = getIntent().getStringExtra("startDate");
        int memberCount = getIntent().getIntExtra("memberCount", 0);
        String description = getIntent().getStringExtra("description");
        String leaderNickname = getIntent().getStringExtra("leaderNickname");

        // ✅ 뷰 바인딩
        TextView groupNameText = findViewById(R.id.group_name_text);
        TextView categoryText = findViewById(R.id.category_text);
        TextView memberCountText = findViewById(R.id.member_count_text);
        TextView startDateText = findViewById(R.id.start_date_text);
        TextView nicknameText = findViewById(R.id.nickname_text);
        TextView descriptionText = findViewById(R.id.group_introduction);
        AppCompatButton joinButton = findViewById(R.id.button_join_group);

        // ✅ 데이터 세팅
        groupNameText.setText(groupName != null ? groupName : "");
        categoryText.setText(category != null ? category : "");
        memberCountText.setText("참여자 " + memberCount);
        startDateText.setText("시작 날짜 " + (startDate != null ? startDate : ""));
        nicknameText.setText(leaderNickname != null ? leaderNickname : "");
        descriptionText.setText(description != null ? description : "");

        // ✅ 참가 요청 처리
        joinButton.setOnClickListener(v -> {
            Long groupId = getIntent().getLongExtra("groupId", -1);
            Long memberId = getMyMemberId(); // SharedPreferences에서 가져오기

            if (groupId == -1 || memberId == -1) {
                Toast.makeText(this, "유효하지 않은 요청입니다", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
            api.sendJoinRequest(groupId, memberId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SearchJoinActivity.this, "참가 요청을 보냈습니다!", Toast.LENGTH_SHORT).show();
                        joinButton.setEnabled(false);
                        joinButton.setText("요청 완료");
                    } else {
                        Toast.makeText(SearchJoinActivity.this, "요청 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(SearchJoinActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ✅ 로그인한 사용자 ID 꺼내기 (SharedPreferences 기반)
    private Long getMyMemberId() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return prefs.getLong("memberId", -1L);
    }
}
