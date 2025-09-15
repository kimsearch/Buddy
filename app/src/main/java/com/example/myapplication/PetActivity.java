package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetActivity extends AppCompatActivity {

    private ImageView petGif;
    private ImageView heartIcon;
    private TextView heartCountText;
    private TextView petLevelText;
    private ProgressBar progressBar;
    private TextView tutorialText;

    private AppCompatImageButton navHome, navGroup, navSearch, navMyPage;

    private int currentHeartCount = 207;
    private int currentExp = 0;
    private int level = 1;
    private boolean isTutorialVisible = false;
    private boolean isAnimating = false;

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
        tutorialText = findViewById(R.id.pet_tutorial);

        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navMyPage = findViewById(R.id.nav_mypage);

        // 서버에서 먹이 개수 가져오기
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        long memberId = prefs.getLong("memberId", -1);

        if (memberId != -1) {
            Retrofit_interface apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);
            Call<Integer> call = apiService.getFeedCount(memberId);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentHeartCount = response.body();
                    } else {
                        Toast.makeText(PetActivity.this, "먹이 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    updateUI();
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Toast.makeText(PetActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    updateUI();
                }
            });
        } else {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            updateUI();
        }

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

        AppCompatImageButton infoBtn = findViewById(R.id.pet_info);
        infoBtn.setOnClickListener(v -> toggleTutorial());

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

        // 레벨별 펫 이미지
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
        if (isAnimating) return;

        if (tutorialText.getVisibility() == View.VISIBLE) {
            isAnimating = true;
            tutorialText.animate()
                    .alpha(0f)
                    .setDuration(180)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> {
                        tutorialText.setVisibility(View.INVISIBLE); // 자리 유지!
                        tutorialText.setAlpha(1f);
                        isAnimating = false;
                        isTutorialVisible = false;
                    })
                    .start();
        } else {
            tutorialText.setAlpha(0f);
            tutorialText.setVisibility(View.VISIBLE);
            isAnimating = true;
            tutorialText.animate()
                    .alpha(1f)
                    .setDuration(180)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(() -> {
                        isAnimating = false;
                        isTutorialVisible = true;
                    })
                    .start();
        }
    }
}
