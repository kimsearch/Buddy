package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.bumptech.glide.Glide;
import androidx.core.content.ContextCompat;
public class PetActivity extends AppCompatActivity {

    private ImageView petGif;
    private ImageView heartIcon;
    private TextView heartCountText;
    private TextView petLevelText;
    private ProgressBar progressBar;

    private ImageButton navHome, navGroup, navSearch, navMyPage;

    private int currentHeartCount = 207;
    private int progress = 25;
    private int level = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet);

        AppCompatImageButton infoButton = findViewById(R.id.pet_info);
        //infoButton.setOnClickListener(v -> showInfoPopup());

        petGif = findViewById(R.id.pet_gif);
        Glide.with(this)
                .asGif()
                .load(R.raw.pet)
                .into(petGif);

        heartIcon = findViewById(R.id.heart_icon);
        heartCountText = findViewById(R.id.heart_count);
        petLevelText = findViewById(R.id.pet_level);
        progressBar = findViewById(R.id.pet_progress_bar);
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navMyPage = findViewById(R.id.nav_mypage);
        updateUI();

        LinearLayout heartContainer = findViewById(R.id.heart_button_container);
        heartContainer.setOnClickListener(v -> {
            if (currentHeartCount > 0) {
                currentHeartCount--;
                progress += 10;

                if (progress >= 100) {
                    progress = 0;
                    level++;
                }
                updateUI();
            } else {
                Toast.makeText(this, "먹이가 부족합니다!", Toast.LENGTH_SHORT).show();
            }
        });

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(PetActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navGroup.setOnClickListener(v -> {
            Intent intent = new Intent(PetActivity.this, GroupPageActivity.class);
            startActivity(intent);
        });

        navSearch.setOnClickListener(v -> {
            Intent intent = new Intent(PetActivity.this, GroupSearchPageActivity.class);
            startActivity(intent);
        });

        navMyPage.setOnClickListener(v -> {
            Intent intent = new Intent(PetActivity.this, MyPageMainActivity.class);
            startActivity(intent);
        });
    }

    private void updateUI() {
        heartCountText.setText(String.valueOf(currentHeartCount));
        petLevelText.setText("레벨 " + level);
        progressBar.setProgress(progress);
    }

/*
    private void showInfoPopup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.popup_info, null);

        TextView message = customView.findViewById(R.id.info_text);
        message.setText("미션을 완료할 시 자동으로 먹이가 주어져요!");
        message.setTextColor(ContextCompat.getColor(this, R.color.custom_info_color));
        message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bmjua.ttf")); // 폰트 적용

        builder.setView(customView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialog.show();
    }
*/
}
