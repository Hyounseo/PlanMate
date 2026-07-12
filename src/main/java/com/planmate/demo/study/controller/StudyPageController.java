package com.planmate.demo.study.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 화면(HTML)을 반환하는 Controller.
 * JSON 데이터는 StatisticsController(REST)가 담당하고,
 * 이 Controller는 화면 진입점 역할만 함.
 */
@Controller
public class StudyPageController {

    @GetMapping("/study/statistics")
    public String statisticsPage() {
        // templates/study/statistics.html 을 렌더링
        return "study/statistics";
    }

    @GetMapping("/study/record")
    public String recordPage() {
        // 공부시간 입력(타이머) 화면 - 다음 단계에서 채울 예정
        return "study/record";
    }
}
