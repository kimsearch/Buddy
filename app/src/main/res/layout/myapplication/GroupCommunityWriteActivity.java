package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupCommunityWriteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button addPhotoButton, submitPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_write);

        // XML에서 요소 연결
        titleEditText = findViewById(R.id.post_input_title);
        contentEditText = findViewById(R.id.post_input_content);
        addPhotoButton = findViewById(R.id.add_photo_button);
        submitPostButton = findViewById(R.id.post_submit_button);

        // 사진 추가 버튼 클릭
        addPhotoButton.setOnClickListener(view -> {
            Toast.makeText(this, "사진 추가 기능은 추후 구현 예정입니다.", Toast.LENGTH_SHORT).show();
        });

        // 게시하기 버튼 클릭
        submitPostButton.setOnClickListener(view -> {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // 게시물 처리 로직 (서버 전송 등) 여기에 추가 가능
                Toast.makeText(this, "게시물이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                finish(); // 화면 닫기
            }
        });
    }
}
