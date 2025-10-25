// AlarmAdapter.java: RecyclerView에 알림을 하나씩 그려주는 역할
package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.myapplication.Alarm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 알림들을 화면에 표시하는 중간 관리자
// AlarmAdapter: RecyclerView.Adapter를 상속 받고, 아이템 리스트를 관리하는 클래스
// AlarmViewHolder: 각각의 알림 하나를 화면에 그릴 때 사용할 레이아웃
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    // 표시할 알림 데이터들을 리스트로 들고 있음
    private List<Alarm> alarmList;

    public AlarmAdapter(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    // 알림 내용에 따라 수락/거절 버튼이 보이게 할지 말지 정하는 부분
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.messageTextView.setText(alarm.getMessage());

        Long groupId = alarm.getGroupId();
        Long requesterId = alarm.getRequesterId();

        // 수락/거절 버튼 보이기 여부
        if ("join_request".equals(alarm.getType())) {
            holder.buttonContainer.setVisibility(View.VISIBLE);
        } else {
            holder.buttonContainer.setVisibility(View.GONE);
        }

        holder.acceptButton.setOnClickListener(v -> {
            if (groupId == null || requesterId == null) {
                Toast.makeText(v.getContext(), "잘못된 요청입니다 (id가 null)", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
            JoinRequestHandleDto dto = new JoinRequestHandleDto(groupId, requesterId, true);

            api.handleJoinRequest(dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "요청을 수락했습니다", Toast.LENGTH_SHORT).show();
                        alarmList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(v.getContext(), "서버 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(v.getContext(), "요청 처리 실패", Toast.LENGTH_SHORT).show();
                }
            });

            Log.d("AlarmAdapter", "groupId: " + alarm.getGroupId() + ", requesterId: " + alarm.getRequesterId());
        });

        holder.declineButton.setOnClickListener(v -> {
            if (groupId == null || requesterId == null) {
                Toast.makeText(v.getContext(), "잘못된 요청입니다 (id가 null)", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit_interface api = Retrofit_client.getInstance().create(Retrofit_interface.class);
            JoinRequestHandleDto dto = new JoinRequestHandleDto(groupId, requesterId, false);

            api.handleJoinRequest(dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "요청을 거절했습니다", Toast.LENGTH_SHORT).show();
                        alarmList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(v.getContext(), "서버 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(v.getContext(), "요청 처리 실패", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        LinearLayout buttonContainer;
        Button acceptButton, declineButton;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.alarm_message);
            buttonContainer = itemView.findViewById(R.id.button_container);
            acceptButton = itemView.findViewById(R.id.btn_accept);
            declineButton = itemView.findViewById(R.id.btn_decline);
        }
    }
}
