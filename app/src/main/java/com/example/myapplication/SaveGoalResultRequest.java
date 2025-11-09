package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class SaveGoalResultRequest {
    @SerializedName("memberId")   public Long memberId;
    @SerializedName("cycleStart") public String cycleStart; // "yyyy-MM-dd"
    @SerializedName("cycleEnd")   public String cycleEnd;   // "yyyy-MM-dd"
    @SerializedName("isSuccess")  public Boolean isSuccess; // true면 1로 승급, false면 0 유지

    public SaveGoalResultRequest(Long memberId, String cycleStart, String cycleEnd, Boolean isSuccess) {
        this.memberId = memberId;
        this.cycleStart = cycleStart;
        this.cycleEnd = cycleEnd;
        this.isSuccess = isSuccess;
    }
}
