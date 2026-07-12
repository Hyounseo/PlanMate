package com.planmate.demo.controller;

import com.planmate.demo.domain.User;
import com.planmate.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController // 웹 프론트엔드와 소통하는 역할임을 선언
@RequestMapping("/api/user") // 주소창에 /api/user로 시작하는 모든 요청을 여기서 받습니다.
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // [회원가입 주소: /api/user/signup]
    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        userService.registerUser(user);
        return "회원가입이 완료되었습니다!";
    }

    // [로그인 주소: /api/user/login]
    @PostMapping("/login")
    public String login(@RequestParam String loginId, @RequestParam String password, HttpSession session) {
        // 주방장(Service)에게 로그인 검사를 시킵니다.
        String result = userService.login(loginId, password);

        // 결과가 "로그인 성공"이면, 목욕탕 사물함(세션)에 사용자 아이디를 넣어둡니다! (이게 세션 유지입니다)
        if (result.equals("로그인 성공")) {
            session.setAttribute("loggedInUser", loginId);
        }

        return result; // 결과를 프론트엔드 화면으로 돌려보냅니다.
    }

    // [잠금 해제 주소: /api/user/unlock]
    @PostMapping("/unlock")
    public String unlock(@RequestParam String loginId, @RequestParam String secondPassword) {
        return userService.unlockAccount(loginId, secondPassword);
    }

    // [로그아웃 주소: /api/user/logout]
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 팔찌(세션)를 끊어버립니다. (기억 상실)
        return "로그아웃 되었습니다.";
    }
}
