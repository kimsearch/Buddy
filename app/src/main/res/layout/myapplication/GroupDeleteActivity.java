package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GroupDeleteActivity extends AppCompatActivity {

    private Button confirmDeleteButton;
    private Button cancelDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_delete);

        confirmDeleteButton = findViewById(R.id.confirm_exit_button);
        cancelDeleteButton = findViewById(R.id.cancel_exit_button);

        // "그룹 삭제" 버튼 클릭 시
        confirmDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        // "취소" 버튼 클릭 시
        cancelDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 액티비티 종료
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("그룹 삭제")
                .setMessage("정말로 이 그룹을 삭제하시겠습니까?\n삭제한 그룹은 복구할 수 없습니다.")
                .setPositiveButton("삭제하기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 실제 그룹 삭제 처리 로직이 여기에 들어감
                        Toast.makeText(GroupDeleteActivity.this, "그룹이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 삭제 후 현재 화면 종료
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
