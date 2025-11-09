package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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

        // 기본 유효성 체크
        if (myMemberId == -1L || groupId == -1L) {
            Toast.makeText(this, "잘못된 접근입니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }

        // "그룹 나가기" 버튼 클릭
        confirmExitButton.setOnClickListener(view -> showExitConfirmationDialog());

        // "취소" 버튼 클릭
        cancelExitButton.setOnClickListener(view -> navigateToGroupPage());
    }

    private void showExitConfirmationDialog() {
        // 1) 커스텀 뷰 inflate
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_exit, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(customView)
                .setPositiveButton("나가기", (d, which) -> {
                    if (groupId != -1L && myMemberId != -1L) {
                        sendExitRequestToServer(groupId, myMemberId);
                    } else {
                        Toast.makeText(this, "그룹 정보를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", (d, which) -> navigateToGroupPage())
                .create();

        dialog.show();

        Typeface bmjua = ResourcesCompat.getFont(this, R.font.bmjua);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (positiveButton != null && bmjua != null) positiveButton.setTypeface(bmjua);
        if (negativeButton != null && bmjua != null) negativeButton.setTypeface(bmjua);
    }

    // ✅ 서버로 탈퇴 요청 보내는 메서드
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
