package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    public List<CommentItem> commentList;
    private String currentUserNickname;
    public OnCommentDeleteListener deleteListener;

    // ✅ 콜백 인터페이스
    public interface OnCommentDeleteListener {
        void onCommentDeleted(Long commentId);
    }

    // ✅ 생성자
    public CommentAdapter(List<CommentItem> commentList, String currentUserNickname, OnCommentDeleteListener deleteListener) {
        this.commentList = commentList;
        this.currentUserNickname = currentUserNickname;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view, this); // 🔹 CommentAdapter 참조 전달
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem comment = commentList.get(position);

        String author = comment.getAuthor();
        String text = comment.getText();

        holder.text.setText(text != null ? text : "(내용 없음)");
        holder.author.setText(author != null ? author : "(익명)");

        if (author != null && author.equals(currentUserNickname)) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                Long commentId = comment.getId();

                SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                Long memberId = prefs.getLong("memberId", -1L);
                if (memberId == -1L) {
                    Toast.makeText(holder.itemView.getContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
                api.deleteComment(commentId, memberId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                commentList.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                                if (deleteListener != null) {
                                    deleteListener.onCommentDeleted(commentId);
                                }
                            }
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "댓글 삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(holder.itemView.getContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateComments(List<CommentItem> newComments) {
        this.commentList.clear();
        this.commentList.addAll(newComments);
        notifyDataSetChanged();
    }

    public void addComment(CommentItem comment) {
        commentList.add(comment);
        notifyItemInserted(commentList.size() - 1);
    }

    // ✅ ViewHolder 정의 (static → adapter 참조 받음)
    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView author, text;
        AppCompatImageButton deleteButton;

        public CommentViewHolder(@NonNull View itemView, CommentAdapter adapter) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            text = itemView.findViewById(R.id.comment_text);
            deleteButton = itemView.findViewById(R.id.comment_delete_button);
        }
    }

}
