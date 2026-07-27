package com.planmate.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String password;
    private String name;
    private String email;

    private String secondPassword; // 2차 보안 비밀번호

    private int failCount = 0; // 로그인 실패 횟수
    private boolean isLocked = false; // 계정 잠금 상태

    // --- 데이터 꺼내고 넣는 기능들 (Getter & Setter) ---
    // 이 부분이 있어야 Service에서 에러가 안 납니다!

    public Long getId() { return id; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSecondPassword() { return secondPassword; }
    public void setSecondPassword(String secondPassword) { this.secondPassword = secondPassword; }

    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
}
