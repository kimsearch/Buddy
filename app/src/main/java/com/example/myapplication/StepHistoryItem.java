package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class StepHistoryItem {

    // 서버에서 오는 JSON key가 'date'가 아닐 경우를 대비해 추가하면 더 안정적입니다.
    // 만약 서버 key가 'date'가 맞다면 @SerializedName("date")는 생략 가능합니다.
    @SerializedName("date")
    private String date;  // yyyy-MM-dd

    // 서버에서 오는 JSON key가 'stepCount'가 아닌 'value'일 가능성이 높습니다.
    // 'value'로 오는 데이터를 stepCount 변수에 매핑합니다.
    @SerializedName("value")
    private int stepCount;

    public String getDate() {
        return date;
    }

    public int getStepCount() {
        return stepCount;
    }

    // getValue()가 실제 값을 반환하도록 수정합니다.
    public int getValue() {
        return stepCount;
    }
}
