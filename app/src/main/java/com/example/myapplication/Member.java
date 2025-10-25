package com.example.myapplication;

public class Member {
    private Long id;
    private String nickname;
    private String email;
    private String password;
    private String birthday;

    public Member(String nickname, String email, String password, String birthday) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
    }

    public Member() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
}
