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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());

        // 🔹 댓글 개수 숫자만 표시
        int commentSize = post.getCommentList().size();
        holder.commentCount.setText(String.valueOf(commentSize));

        // 이미지 표시
        Uri imageUri = post.getImageUri();
        if (imageUri != null) {
            holder.image.setImageURI(imageUri);
        } else {
            holder.image.setImageResource(R.drawable.ic_heart);
        }

        // 좋아요 상태
        holder.btnLike.setImageResource(post.isLiked() ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));

        // 좋아요 버튼
        holder.btnLike.setOnClickListener(v -> {
            boolean current = post.isLiked();
            post.setLiked(!current);
            if (current) post.decrementLike();
            else post.incrementLike();
            notifyItemChanged(holder.getAdapterPosition());
        });

        // SharedPreferences에서 닉네임 꺼내기
        SharedPreferences prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String nickname = prefs.getString("userNickname", "익명");

        // 댓글 어댑터 설정
        CommentAdapter commentAdapter = new CommentAdapter(post.getCommentList(), nickname, () -> {
            notifyItemChanged(holder.getAdapterPosition());
        });
        holder.commentRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.commentRecycler.setAdapter(commentAdapter);

        holder.commentRecycler.setVisibility(View.GONE);

        // 댓글창 토글
        holder.btnComment.setOnClickListener(v -> {
            if (holder.commentRecycler.getVisibility() == View.VISIBLE) {
                holder.commentRecycler.setVisibility(View.GONE);
            } else {
                holder.commentRecycler.setVisibility(View.VISIBLE);
            }
        });

        // 댓글 등록 버튼
        holder.btnSubmitComment.setOnClickListener(v -> {
            String commentText = holder.editComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                CommentItem newComment = new CommentItem(nickname, commentText);
                post.addComment(newComment);
                holder.editComment.setText("");
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        // 점 메뉴
        holder.moreButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.moreButton);
            popupMenu.getMenuInflater().inflate(R.menu.menu_post_options, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    if (item.getItemId() == R.id.menu_edit) {
                        listener.onEditClicked(postList.get(pos), pos);
                        return true;
                    } else if (item.getItemId() == R.id.menu_delete) {
                        listener.onDeleteClicked(pos);
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
        TextView title, content, likeCount, commentCount; // 🔹 commentCount 추가됨
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
            commentCount = itemView.findViewById(R.id.text_comment_count); // 🔹 이 줄 추가됨
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
