package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.core.content.res.ResourcesCompat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

public class MyPageMainActivity extends AppCompatActivity {

    AppCompatImageButton settingsButton, delete1Button, delete2Button;
    AppCompatImageButton navHome, navGroup, navSearch, navPet, navMyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_main);

        // 1. 설정 버튼 → mypage_setting.xml
        settingsButton = findViewById(R.id.settings_title);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, MyPageSettingActivity.class);
            startActivity(intent);
        });

        // 2. 삭제 팝업
        delete1Button = findViewById(R.id.history_delete_1);
        delete2Button = findViewById(R.id.history_delete_2);

        delete1Button.setOnClickListener(v -> showDeleteDialog());
        delete2Button.setOnClickListener(v -> showDeleteDialog());

        // 3~5. 네비게이션 바 연결
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navPet = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, GroupSearchPageActivity.class);
            startActivity(intent);
        });

        navPet.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageMainActivity.this, PetActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(v -> {
            // 현재 페이지 → 아무 작업 안 함
        });
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);

        builder.setTitle("삭제 확인");
        builder.setMessage("정말 삭제하시겠어요?");

        builder.setPositiveButton("삭제", (dialog, which) -> {
            // TODO: 삭제 로직
            dialog.dismiss();
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // 배경 둥글게 설정
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        // 팝업 내부 글꼴 적용
        Typeface customFont = ResourcesCompat.getFont(this, R.font.bmjua);
        TextView messageView = dialog.findViewById(android.R.id.message);
        TextView titleView = dialog.findViewById(getResources().getIdentifier("alertTitle", "id", "android"));
        if (messageView != null) messageView.setTypeface(customFont);
        if (titleView != null) titleView.setTypeface(customFont);
    }

}
