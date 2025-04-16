package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GroupCommunityWriteActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText;
    private Button addPhotoButton, submitPostButton;
    private ImageView previewImageView;

    private boolean isEditMode = false;
    private Uri selectedImageUri = null;

    // ✅ 이미지 선택 런처 (ActivityResult API 사용)
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        previewImageView.setImageURI(selectedImageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_community_write);

        titleEditText = findViewById(R.id.post_input_title);
        contentEditText = findViewById(R.id.post_input_content);
        addPhotoButton = findViewById(R.id.add_photo_button);
        submitPostButton = findViewById(R.id.post_submit_button);
        previewImageView = findViewById(R.id.preview_image);

        // ✅ 수정 모드인지 확인
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEdit", false);
        if (isEditMode) {
            titleEditText.setText(intent.getStringExtra("title"));
            contentEditText.setText(intent.getStringExtra("content"));

            String imageUriStr = intent.getStringExtra("imageUri");
            if (imageUriStr != null) {
                selectedImageUri = Uri.parse(imageUriStr);
                previewImageView.setImageURI(selectedImageUri);
            }

            submitPostButton.setText("수정하기");
        }

        // ✅ 사진 추가 버튼
        addPhotoButton.setOnClickListener(view -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType("image/*");
            imagePickerLauncher.launch(pickIntent);
        });

        // ✅ 게시/수정 버튼
        submitPostButton.setOnClickListener(view -> {
            String title = titleEditText.getText().toString().trim();
            String content = contentEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("content", content);
                if (selectedImageUri != null) {
                    resultIntent.putExtra("imageUri", selectedImageUri.toString());
                }
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
