// Alarm.java
package com.example.myapplication;

// 알림 하나가 갖고 있는 정보는 메세지 내용과 알림의 종류
// TYPE_REQUEST인 경우엔 그룹 초대처럼 "수락/거절" 버튼이 있어야 하는 알림
public class Alarm {
    public static final int TYPE_NORMAL = 0; // 일반 알림
    public static final int TYPE_REQUEST = 1; // 수락/거절 버튼 있는 알림

    private String message; // 보여줄 텍스트
    private int type; //이게 버튼이 있는 알림인지 없는 알림인지 구분

    public Alarm(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}
