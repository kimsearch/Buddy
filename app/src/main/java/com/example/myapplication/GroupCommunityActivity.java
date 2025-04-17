package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupCommunityActivity extends AppCompatActivity implements PostAdapter.OnPostItemClickListener {

    private static final int WRITE_REQUEST_CODE = 1;
    private static final int EDIT_REQUEST_CODE = 2;

    ImageButton btnMypage, btnWritePost;
    ImageButton navHome, navGroup, navSearch, navAlarm, navMyPage;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<PostItem> postList;

    private int editingPosition = -1;  // 수정 중인 포지션 저장용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_community);

        recyclerView = findViewById(R.id.recycler_view_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        adapter = new PostAdapter(this, postList, this);
        recyclerView.setAdapter(adapter);

        // 상단 버튼
        btnMypage = findViewById(R.id.btn_mypage);
        btnMypage.setOnClickListener(view -> startActivity(new Intent(this, MyPageMainActivity.class)));

        // 글쓰기 버튼
        btnWritePost = findViewById(R.id.btn_write_post);
        btnWritePost.setOnClickListener(view -> {
            Intent intent = new Intent(this, GroupCommunityWriteActivity.class);
            startActivityForResult(intent, WRITE_REQUEST_CODE);
        });

        // 하단 네비게이션
        navHome = findViewById(R.id.nav_home);
        navGroup = findViewById(R.id.nav_group);
        navSearch = findViewById(R.id.nav_search);
        navAlarm = findViewById(R.id.nav_alarm);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(view -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(view -> startActivity(new Intent(this, SampleLayoutActivity.class)));
        navAlarm.setOnClickListener(view -> startActivity(new Intent(this, AlarmPageActivity.class)));
        navMyPage.setOnClickListener(view -> startActivity(new Intent(this, MyPageMainActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");
            String imageUriStr = data.getStringExtra("imageUri");
            Uri imageUri = imageUriStr != null ? Uri.parse(imageUriStr) : null;

            if (title != null && content != null) {
                if (requestCode == WRITE_REQUEST_CODE) {
                    PostItem post = new PostItem(title, content, imageUri);
                    postList.add(0, post);
                    adapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                } else if (requestCode == EDIT_REQUEST_CODE && editingPosition != -1) {
                    PostItem post = postList.get(editingPosition);
                    post.setTitle(title);
                    post.setContent(content);
                    post.setImageUri(imageUri);
                    adapter.notifyItemChanged(editingPosition);
                    editingPosition = -1;
                }
            }
        }
    }

    @Override
    public void onEditClicked(PostItem post, int position) {
        Intent intent = new Intent(this, GroupCommunityWriteActivity.class);
        intent.putExtra("title", post.getTitle());
        intent.putExtra("content", post.getContent());
        intent.putExtra("imageUri", post.getImageUri() != null ? post.getImageUri().toString() : null);
        intent.putExtra("isEdit", true);
        editingPosition = position;
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @Override
    public void onDeleteClicked(int position) {
        postList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
