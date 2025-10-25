package com.example.myapplication;

import android.app.AlertDialog;
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

        bmjuaFont = Typeface.createFromAsset(getAssets(), "font/bmjua.ttf");

        Button deleteConfirmBtn = findViewById(R.id.confirm_delete_button);
        deleteConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePopup();
            }
        });
    }

    private void showDeletePopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_delete_confirm, null);

        TextView message = popupView.findViewById(R.id.popup_message);
        Button cancelBtn = popupView.findViewById(R.id.cancel_button);
        Button confirmBtn = popupView.findViewById(R.id.confirm_button);

        message.setTypeface(bmjuaFont);
        cancelBtn.setTypeface(bmjuaFont);
        confirmBtn.setTypeface(bmjuaFont);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(popupView)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.popup_background_rounded);
        dialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppDeleteActivity.this, MyPageSettingsActivity.class);
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
