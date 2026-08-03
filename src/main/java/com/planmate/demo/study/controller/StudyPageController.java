package com.planmate.demo.study.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 공부 기록/통계 화면을 반환하는 Controller.
 * JSON 데이터는 별도의 REST Controller가 담당한다.
 */
@Controller
public class StudyPageController {

    @GetMapping("/study/statistics")
    public String statisticsPage(HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        return "study/statistics";
    }

    @GetMapping("/study/record")
    public String recordPage(HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        return "study/record";
    }
}