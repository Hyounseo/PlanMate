package com.planmate.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String homePage(HttpSession session) {

        // 이미 로그인한 사용자는 대시보드로 이동
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {

        // 이미 로그인한 사용자는 대시보드로 이동
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(HttpSession session) {

        // 이미 로그인한 사용자는 대시보드로 이동
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }

        return "signup";
    }

    @GetMapping("/unlock")
    public String unlockPage(HttpSession session) {

        // 이미 로그인한 사용자는 대시보드로 이동
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }

        return "unlock";
    }

    @GetMapping("/schedule")
    public String schedulePage(HttpSession session) {

        // 로그인하지 않은 사용자는 로그인 화면으로 이동
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        return "schedule";
    }

    @GetMapping("/goal")
    public String goalPage(HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        return "goal";
    }


}