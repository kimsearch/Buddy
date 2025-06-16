package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupCommunityActivity extends AppCompatActivity implements PostAdapter.OnPostItemClickListener {

    private static final int WRITE_REQUEST_CODE = 1;
    private static final int EDIT_REQUEST_CODE = 2;

    ImageButton btnMypage, btnWritePost;
    ImageButton navHome, navGroup, navSearch, navAlarm, navMyPage;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ArrayList<PostItem> postList;

    private Retrofit_interface apiService;


    private int editingPosition = -1;  // 수정 중인 포지션 저장용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_community);

        apiService = Retrofit_client.getInstance().create(Retrofit_interface.class);

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
        navAlarm = findViewById(R.id.nav_pet);
        navMyPage = findViewById(R.id.nav_mypage);

        navHome.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        navGroup.setOnClickListener(view -> startActivity(new Intent(this, GroupPageActivity.class)));
        navSearch.setOnClickListener(view -> startActivity(new Intent(this, SampleLayoutActivity.class)));
        navAlarm.setOnClickListener(view -> startActivity(new Intent(this, AlarmPageActivity.class)));
        navMyPage.setOnClickListener(view -> startActivity(new Intent(this, MyPageMainActivity.class)));
        loadPostsFromServer();
    }

    private void loadPostsFromServer() {
        long groupId = getSharedPreferences("loginPrefs", MODE_PRIVATE).getLong("groupId", -1L);
        if (groupId == -1L) {
            Toast.makeText(this, "그룹 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getPosts(groupId).enqueue(new retrofit2.Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    for (Post post : response.body()) {
                        Uri imageUri = post.getImageUrl() != null ? Uri.parse(post.getImageUrl()) : null;
                        PostItem item = new PostItem(post.getId(), post.getTitle(), post.getContent(), imageUri);
                        postList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GroupCommunityActivity.this, "게시글 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(GroupCommunityActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
