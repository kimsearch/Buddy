package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GroupExitActivity extends AppCompatActivity {

    private Button confirmExitButton;
    private Button cancelExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_exit);

        confirmExitButton = findViewById(R.id.confirm_exit_button);
        cancelExitButton = findViewById(R.id.cancel_exit_button);

        // "그룹 나가기" 버튼 클릭
        confirmExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitConfirmationDialog();
            }
        });

        // "취소" 버튼 클릭
        cancelExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();  // 현재 액티비티 종료
            }
        });
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("그룹 나가기")
                .setMessage("정말 그룹을 나가시겠습니까?\n이 작업은 되돌릴 수 없습니다.")
                .setPositiveButton("나가기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 실제 그룹 나가기 로직을 여기에 구현
                        Toast.makeText(GroupExitActivity.this, "그룹에서 나갔습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 나간 후 현재 화면 종료
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}

