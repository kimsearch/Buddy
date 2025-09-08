package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountManagementActivity extends AppCompatActivity {

    // Retrofit
    private Retrofit_interface apiService;

    // 상단
    private ImageButton backButton;

    // 목록 항목 (XML에서 TextView!)
    private TextView emailButton;     // 이메일 찾기
    private TextView logoutButton;    // 로그아웃
    private TextView deleteIdButton;  // 회원 탈퇴

    // 하단 네비게이션
    private AppCompatImageButton navHome;
    private AppCompatImageButton navGroup;
    private AppCompatImageButton navSearch;
    private AppCompatImageButton navPet;
    private AppCompatImageButton navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_management);

        // Retrofit
        apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // ===== 뷰 바인딩 =====
        backButton     = findViewById(R.id.back_button);

        emailButton    = findViewById(R.id.email_button);
        logoutButton   = findViewById(R.id.logout_button);
        deleteIdButton = findViewById(R.id.delete_id_button);

        navHome   = findViewById(R.id.nav_home);
        navGroup  = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet    = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        // ===== 클릭 리스너 =====
        // 1) 뒤로가기 -> 설정 화면으로
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // 일반적으로 finish() 만으로 이전 화면 복귀
                // (스택이 없는 특수 케이스면 CLEAR_TOP으로 강제 복귀하도록 변경 가능)
                finish();
            }
        });

        // 7) 이메일 찾기 -> EmailFindMainActivity
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(AccountManagementActivity.this, EmailFindMainActivity.class));
            }
        });

        // 로그아웃
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                doLogout();
            }
        });

        // 회원 탈퇴
        deleteIdButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                confirmAndDeleteAccount();
            }
        });

        // ===== 하단 네비게이션 =====
        // 홈 -> MainActivity
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(AccountManagementActivity.this, MainActivity.class));
            }
        });

        // 그룹 -> GroupPageActivity
        navGroup.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(AccountManagementActivity.this, GroupPageActivity.class));
            }
        });

        // 검색 -> GroupSearchPageActivity
        navSearch.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(AccountManagementActivity.this, GroupSearchPageActivity.class));
            }
        });

        // 펫 -> PetActivity
        navPet.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(AccountManagementActivity.this, PetActivity.class));
            }
        });

        // 마이페이지 -> MyPageMainActivity
        navMyPage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent(AccountManagementActivity.this, MyPageMainActivity.class));
            }
        });
    }

    /** 로그아웃 처리: 로그인 정보 정리 후 로그인 화면으로 이동 */
    private void doLogout() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        // 프로젝트에서 사용하는 키에 맞춰 정리
        // 예: prefs.edit().remove("userEmail").apply();
        // 전체 정리 원하면 clear()
        prefs.edit().clear().apply();

        Intent intent = new Intent(AccountManagementActivity.this, LoginPageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /** 회원 탈퇴 확인 → 서버 요청 → 성공 시 세션 정리 후 로그인 화면으로 */
    private void confirmAndDeleteAccount() {
        new AlertDialog.Builder(AccountManagementActivity.this)
                .setTitle("회원 탈퇴")
                .setMessage("정말로 회원 탈퇴를 진행하시겠습니까?\n모든 데이터가 삭제됩니다.")
                .setPositiveButton("탈퇴", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    String email = prefs.getString("userEmail", null);

                    if (email == null || email.isEmpty()) {
                        Toast.makeText(AccountManagementActivity.this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Call<ResponseBody> call = apiService.deleteMember(email);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AccountManagementActivity.this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                // 세션/저장 정보 정리
                                prefs.edit().clear().apply();

                                // 로그인 화면으로 이동 (스택 초기화)
                                Intent intent = new Intent(AccountManagementActivity.this, LoginPageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(AccountManagementActivity.this, "탈퇴 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(AccountManagementActivity.this, "서버 요청 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
