package com.example.myapplication;

//랭킹
public class RankingItem {

    private Long memberId;
    private String nickname;
    private int successCount;

    // 👇 이 두 개는 클라이언트에서 UI용으로 세팅할 거야
    private int profileResId;
    private int progress; // 최대 100 기준

    public RankingItem(Long memberId, String nickname, int successCount) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.successCount = successCount;
    }

    // --- Getter ---
    public String getNickname() {
        return nickname;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public Long getMemberId() {
        return memberId;
    }

    public int getProfileResId() {
        return profileResId;
    }

    public int getProgress() {
        return progress;
    }

    // --- Setter ---
    public void setProfileResId(int profileResId) {
        this.profileResId = profileResId;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
