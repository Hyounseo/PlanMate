package com.planmate.demo.controller;

import com.planmate.demo.domain.User;
import com.planmate.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입
    @PostMapping("/signup")
    public String signup(@ModelAttribute User user) {
        return userService.registerUser(user);
    }

    // 로그인
    @PostMapping("/login")
    public String login(
            @RequestParam String loginId,
            @RequestParam String password,
            HttpSession session
    ) {
        return userService.login(
                loginId,
                password,
                session
        );
    }

    // 2차 비밀번호로 잠금 해제 + 바로 로그인
    @PostMapping("/unlock")
    public String unlock(
            @RequestParam String loginId,
            @RequestParam String secondPassword,
            HttpSession session
    ) {
        return userService.unlockAccount(
                loginId,
                secondPassword,
                session
        );
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {

        // 세션 전체 삭제
        session.invalidate();

        return "로그아웃 성공";
    }
}
