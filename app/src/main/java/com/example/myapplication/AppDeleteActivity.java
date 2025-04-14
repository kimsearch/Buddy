package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AppDeleteActivity extends AppCompatActivity {

    private Typeface bmjuaFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_delete);

        // BMJUA 폰트 로드
        bmjuaFont = Typeface.createFromAsset(getAssets(), "font/bmjua.ttf");

        // 탈퇴 확인 버튼
        Button deleteConfirmBtn = findViewById(R.id.confirm_delete_button);
        deleteConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePopup();
            }
        });
    }

    private void showDeletePopup() {
        // 커스텀 뷰 inflate
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_delete_confirm, null);

        // 팝업 안의 글씨체 설정
        TextView message = popupView.findViewById(R.id.popup_message);
        Button cancelBtn = popupView.findViewById(R.id.cancel_button);
        Button confirmBtn = popupView.findViewById(R.id.confirm_button);

        message.setTypeface(bmjuaFont);
        cancelBtn.setTypeface(bmjuaFont);
        confirmBtn.setTypeface(bmjuaFont);

        // 팝업 생성
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(popupView)
                .create();

        // 팝업 외부 테두리 둥글게
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_background_rounded);
        dialog.show();

        // 버튼 리스너
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mypage_setting으로 이동
                Intent intent = new Intent(AppDeleteActivity.this, MyPageSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // login_page로 이동
                Intent intent = new Intent(AppDeleteActivity.this, LoginPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
