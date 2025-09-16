package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class GoalLogDto {
    public Long id;

    @SerializedName("groupId")   public Long groupId;
    @SerializedName("memberId")  public Long memberId;
    @SerializedName("cycleStart") public String cycleStart; // "yyyy-MM-dd"
    @SerializedName("cycleEnd")   public String cycleEnd;   // "yyyy-MM-dd"

    // Gson boolean 이름 충돌 방지용으로 SerializedName 명시
    @SerializedName("isSuccess") public Boolean isSuccess;  // 1=성공, 0=실패(또는 미기록)
}
