package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostItem> postList;
    private OnPostItemClickListener listener;
    private Context context;

    public PostAdapter(Context context, List<PostItem> postList, OnPostItemClickListener listener) {
        this.context = context;
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem post = postList.get(position);

        SharedPreferences prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        Long memberId = prefs.getLong("memberId", -1L);
        String nickname = prefs.getString("userNickname", "익명");

        Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);

        // 이미지 표시 여부 처리
        Uri imageUri = post.getImageUri();
        if (imageUri != null) {
            holder.image.setVisibility(View.VISIBLE);
            holder.image.setImageURI(imageUri);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());

        holder.btnLike.setImageResource(post.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart);

        api.hasLiked(post.getPostId(), memberId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean liked = response.body();
                    post.setLiked(liked);
                    holder.btnLike.setImageResource(liked ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(context, "좋아요 상태 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });

        api.countLikes(post.getPostId()).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long count = response.body();
                    post.setLikeCount((int) count);
                    holder.likeCount.setText(String.valueOf(count));
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Toast.makeText(context, "좋아요 수 로딩 실패", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnLike.setOnClickListener(v -> {
            if (memberId == -1L) {
                Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
                return;
            }

            api.toggleLike(post.getPostId(), memberId).enqueue(new Callback<LikeResponse>() {
                @Override
                public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean liked = response.body().isLiked();
                        post.setLiked(liked);

                        if (liked) {
                            post.incrementLike();
                            holder.btnLike.setImageResource(R.drawable.ic_heart_filled);
                        } else {
                            post.decrementLike();
                            holder.btnLike.setImageResource(R.drawable.ic_heart);
                        }

                        holder.likeCount.setText(String.valueOf(post.getLikeCount()));
                    }
                }

                @Override
                public void onFailure(Call<LikeResponse> call, Throwable t) {
                    Toast.makeText(context, "좋아요 처리 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.commentRecycler.setVisibility(View.GONE);
        api.getCommentsByPost(post.getPostId()).enqueue(new Callback<List<CommentItem>>() {
            @Override
            public void onResponse(Call<List<CommentItem>> call, Response<List<CommentItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CommentItem> serverComments = response.body();
                    post.setCommentList(serverComments);
                    holder.commentCount.setText(String.valueOf(serverComments.size()));

                    CommentAdapter commentAdapter = new CommentAdapter(serverComments, nickname, commentId -> {
                        notifyItemChanged(holder.getAdapterPosition());
                    });
                    holder.commentRecycler.setLayoutManager(new LinearLayoutManager(context));
                    holder.commentRecycler.setAdapter(commentAdapter);
                } else {
                    Toast.makeText(context, "댓글 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CommentItem>> call, Throwable t) {
                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnComment.setOnClickListener(v -> {
            holder.commentRecycler.setVisibility(
                    holder.commentRecycler.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        holder.btnSubmitComment.setOnClickListener(v -> {
            String commentText = holder.editComment.getText().toString().trim();
            if (commentText.isEmpty()) return;

            if (memberId == -1L) {
                Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
                return;
            }

            api.createComment(post.getPostId(), memberId, commentText).enqueue(new Callback<PostComment>() {
                @Override
                public void onResponse(Call<PostComment> call, Response<PostComment> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PostComment createdComment = response.body();
                        CommentItem newComment = new CommentItem(
                                createdComment.getId(),
                                createdComment.getMemberNickname(),
                                createdComment.getContent()
                        );
                        post.getCommentList().add(newComment);
                        holder.editComment.setText("");
                        notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(context, "댓글 등록 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PostComment> call, Throwable t) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.moreButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.moreButton);
            popupMenu.getMenuInflater().inflate(R.menu.menu_post_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    if (item.getItemId() == R.id.menu_delete) {
                        PostItem targetPost = postList.get(pos);
                        if (memberId == -1L) {
                            Toast.makeText(context, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        api.deletePost(targetPost.getPostId(), memberId).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    postList.remove(pos);
                                    notifyItemRemoved(pos);
                                    Toast.makeText(context, "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "서버 오류", Toast.LENGTH_SHORT).show();
                            }
                        });

                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, likeCount, commentCount;
        ImageView image;
        AppCompatImageButton btnLike, btnComment, moreButton;
        EditText editComment;
        AppCompatButton btnSubmitComment;
        RecyclerView commentRecycler;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.post_title);
            content = itemView.findViewById(R.id.post_content);
            image = itemView.findViewById(R.id.post_image);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnComment = itemView.findViewById(R.id.btn_comment);
            moreButton = itemView.findViewById(R.id.btn_more);
            likeCount = itemView.findViewById(R.id.text_like_count);
            commentCount = itemView.findViewById(R.id.text_comment_count);
            editComment = itemView.findViewById(R.id.edit_comment);
            btnSubmitComment = itemView.findViewById(R.id.btn_submit_comment);
            commentRecycler = itemView.findViewById(R.id.recycler_view_comments);
        }
    }

    public interface OnPostItemClickListener {
        void onEditClicked(PostItem post, int position);
        void onDeleteClicked(int position);
    }
}
