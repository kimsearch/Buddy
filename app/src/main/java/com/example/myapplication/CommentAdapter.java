package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentItem> commentList;
    private String currentUserNickname;
    private OnCommentDeleteListener deleteListener; // 🔹 콜백 추가

    // ✅ 콜백 인터페이스 정의
    public interface OnCommentDeleteListener {
        void onCommentDeleted();
    }

    // ✅ 생성자에 콜백 포함
    public CommentAdapter(List<CommentItem> commentList, String currentUserNickname, OnCommentDeleteListener deleteListener) {
        this.commentList = commentList;
        this.currentUserNickname = currentUserNickname;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem comment = commentList.get(position);
        holder.author.setText(comment.getAuthor());
        holder.text.setText(comment.getText());

        // 🔹 삭제 버튼 표시 조건
        if (comment.getAuthor().equals(currentUserNickname)) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        // 🔹 삭제 버튼 클릭
        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                commentList.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);

                // ✅ 댓글 삭제 콜백 호출
                if (deleteListener != null) {
                    deleteListener.onCommentDeleted();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView author, text;
        AppCompatImageButton deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            text = itemView.findViewById(R.id.comment_text);
            deleteButton = itemView.findViewById(R.id.comment_delete_button);
        }
    }
}
