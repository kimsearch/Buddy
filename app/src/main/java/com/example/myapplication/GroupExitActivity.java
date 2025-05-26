package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupExitActivity extends AppCompatActivity {

    private Button confirmExitButton;
    private Button cancelExitButton;

    private Long groupId;
    private Long myMemberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_exit);

        confirmExitButton = findViewById(R.id.confirm_exit_button);
        cancelExitButton = findViewById(R.id.cancel_exit_button);

        // ✅ SharedPreferences에서 내 memberId 가져오기
        myMemberId = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                .getLong("memberId", -1L);

        // ✅ 전달받은 groupId 가져오기
        Intent intent = getIntent();
        groupId = intent.getLongExtra("groupId", -1L);

        // "그룹 나가기" 버튼 클릭
        confirmExitButton.setOnClickListener(view -> showExitConfirmationDialog());

        // "취소" 버튼 클릭
        cancelExitButton.setOnClickListener(view -> navigateToGroupPage());
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("그룹 나가기")
                .setMessage("정말 그룹을 나가시겠습니까?\n이 작업은 되돌릴 수 없습니다.")
                .setPositiveButton("나가기", (dialog, which) -> sendExitRequestToServer(groupId, myMemberId))
                .setNegativeButton("취소", (dialog, which) -> navigateToGroupPage())
                .show();
    }

    // ✅ 서버로 탈퇴 요청 보내는 메서드 (신규)
    private void sendExitRequestToServer(Long groupId, Long memberId) {
        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
        api.exitGroup(groupId, memberId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GroupExitActivity.this, "그룹에서 나갔습니다.", Toast.LENGTH_SHORT).show();
                    navigateToGroupPage();
                } else {
                    Toast.makeText(GroupExitActivity.this, "서버 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(GroupExitActivity.this, "연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToGroupPage() {
        Intent intent = new Intent(GroupExitActivity.this, GroupPageActivity.class);
        startActivity(intent);
        finish();
    }
}
