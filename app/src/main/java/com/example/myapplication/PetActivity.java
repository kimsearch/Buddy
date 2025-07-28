package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;

public class PetActivity extends AppCompatActivity {

    private ImageView petGif;
    private ImageView heartIcon;
    private TextView heartCountText;
    private TextView petLevelText;
    private ProgressBar progressBar;
    private TextView tutorialText;

    private ImageButton navHome, navGroup, navSearch, navMyPage;

    private int currentHeartCount = 207;
    private int currentExp = 0;
    private int level = 1;
    private boolean isTutorialVisible = false;

    private final int[] requiredExp = {0, 20, 20, 20, 20};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet);

        // 뷰 연결
        petGif = findViewById(R.id.pet_gif);

        heartIcon = findViewById(R.id.heart_icon);
        heartCountText = findViewById(R.id.heart_count);
        petLevelText = findViewById(R.id.pet_level);
        progressBar = findViewById(R.id.pet_progress_bar);
        tutorialText = findViewById(R.id.pet_tutorial);  // 추가된 튜토리얼 텍스트뷰

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navMyPage = findViewById(R.id.nav_mypage);

        updateUI();

        // 먹이 주기 버튼
        LinearLayout heartContainer = findViewById(R.id.heart_button_container);
        heartContainer.setOnClickListener(v -> {
            if (currentHeartCount > 0) {
                currentHeartCount--;
                currentExp++;

                int maxExp = requiredExp[Math.min(level, requiredExp.length - 1)];

                if (currentExp >= maxExp && level < 5) {
                    level++;
                    currentExp = 0;
                    Toast.makeText(this, "레벨업!", Toast.LENGTH_SHORT).show();
                }

                updateUI();
            } else {
                Toast.makeText(this, "먹이가 부족합니다!", Toast.LENGTH_SHORT).show();
            }
        });

        // 튜토리얼 버튼
        AppCompatImageButton infoBtn = findViewById(R.id.pet_info);
        infoBtn.setOnClickListener(v -> toggleTutorial());

        // 하단 네비게이션
        navHome.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(v -> startActivity(new Intent(this, GroupSearchPageActivity.class)));
        navMyPage.setOnClickListener(v -> startActivity(new Intent(this, MyPageMainActivity.class)));
    }

    private void updateUI() {
        heartCountText.setText(String.valueOf(currentHeartCount));
        petLevelText.setText("레벨 " + level);

        int maxExp = requiredExp[Math.min(level, requiredExp.length - 1)];
        int progressPercent = (int) (((float) currentExp / maxExp) * 100);
        progressBar.setProgress(progressPercent);

        // 그래프용 이미지 연동
        switch (level) {
            case 1:
                petGif.setImageResource(R.drawable.p_cat1);
                break;
            case 2:
                petGif.setImageResource(R.drawable.p_cat2);
                break;
            case 3:
                petGif.setImageResource(R.drawable.p_cat3);
                break;
            case 4:
                petGif.setImageResource(R.drawable.p_cat4);
                break;
            case 5:
                petGif.setImageResource(R.drawable.p_cat5);
                break;
        }
    }

    private void toggleTutorial() {
        if (isTutorialVisible) {
            TranslateAnimation slideOut = new TranslateAnimation(0, -tutorialText.getWidth(), 0, 0);
            slideOut.setDuration(300);
            tutorialText.startAnimation(slideOut);
            tutorialText.setVisibility(View.GONE);
        } else {
            tutorialText.setVisibility(View.VISIBLE);
            TranslateAnimation slideIn = new TranslateAnimation(-tutorialText.getWidth(), 0, 0, 0);
            slideIn.setDuration(300);
            tutorialText.startAnimation(slideIn);
        }
        isTutorialVisible = !isTutorialVisible;
    }
}