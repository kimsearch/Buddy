package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

    private Retrofit_interface apiService;
    private Long memberId;
    private Long groupId;

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

        apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);

        titleEditText = findViewById(R.id.post_input_title);
        contentEditText = findViewById(R.id.post_input_content);
        addPhotoButton = findViewById(R.id.add_photo_button);
        submitPostButton = findViewById(R.id.post_submit_button);
        previewImageView = findViewById(R.id.preview_image);

        // ✅ 로그인 정보에서 memberId, groupId 불러오기
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        memberId = prefs.getLong("memberId", -1L);
        groupId = prefs.getLong("groupId", -1L); // 현재 그룹 ID가 저장되어 있어야 함

        Log.d("WriteActivity", "memberId: " + memberId + ", groupId: " + groupId);

        if (memberId == -1L || groupId == -1L) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
            String imageUrl = (selectedImageUri != null) ? selectedImageUri.toString() : null;

            if (title.isEmpty()) {
                Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrofit으로 서버에 게시글 등록
            apiService.createPost(memberId, groupId, title, content, imageUrl)
                    .enqueue(new retrofit2.Callback<Post>() {
                        @Override
                        public void onResponse(retrofit2.Call<Post> call, retrofit2.Response<Post> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(GroupCommunityWriteActivity.this, "게시글 작성 완료!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(GroupCommunityWriteActivity.this, "작성 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<Post> call, Throwable t) {
                            Toast.makeText(GroupCommunityWriteActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
