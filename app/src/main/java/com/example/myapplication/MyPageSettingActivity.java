package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageSettingActivity extends AppCompatActivity {

    private Retrofit_interface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_setting);

        apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // 이메일 찾기 버튼
        Button emailButton = findViewById(R.id.email_button);
        emailButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageSettingActivity.this, EmailFindMainActivity.class);
            startActivity(intent);
        });

        // 홈 버튼
        AppCompatImageButton homeBtn = findViewById(R.id.nav_home);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageSettingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // 그룹 버튼
        AppCompatImageButton groupBtn = findViewById(R.id.nav_group);
        groupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageSettingActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        // 마이페이지 버튼
        AppCompatImageButton mypageBtn = findViewById(R.id.nav_mypage);
        mypageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageSettingActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });

        // 로그아웃 버튼
        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            prefs.edit().remove("userEmail").apply();

            Intent intent = new Intent(MyPageSettingActivity.this, LoginPageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // ✅ 회원탈퇴 버튼
        Button deleteButton = findViewById(R.id.delete_id_button);
        deleteButton.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(MyPageSettingActivity.this)
                    .setTitle("회원 탈퇴")
                    .setMessage("정말로 회원 탈퇴를 진행하시겠습니까?\n모든 데이터가 삭제됩니다.")
                    .setPositiveButton("탈퇴", (dialog, which) -> {
                        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                        String email = prefs.getString("userEmail", null);

                        if (email != null) {
                            Call<ResponseBody> call = apiService.deleteMember(email);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(MyPageSettingActivity.this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                        prefs.edit().clear().apply();

                                        Intent intent = new Intent(MyPageSettingActivity.this, LoginPageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(MyPageSettingActivity.this, "탈퇴 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(MyPageSettingActivity.this, "서버 요청 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(MyPageSettingActivity.this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });
    }
}
