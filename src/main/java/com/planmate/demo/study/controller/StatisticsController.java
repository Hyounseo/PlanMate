package com.planmate.demo.study.controller;

import com.planmate.demo.study.service.StatisticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // TODO: 팀원1 세션 키 확정되면 맞춰서 수정 (StudyController와 동일)
    private Long getCurrentUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return userId;
    }

    // 일별 총 공부시간(초)
    @GetMapping("/daily")
    public Map<String, Integer> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session) {
        Long userId = getCurrentUserId(session);
        return Map.of("totalSeconds", statisticsService.getDailyTotalSeconds(userId, date));
    }

    // 주별 총 공부시간(초) - 월~일 기준
    @GetMapping("/weekly")
    public Map<String, Integer> weekly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session) {
        Long userId = getCurrentUserId(session);
        return Map.of("totalSeconds", statisticsService.getWeeklyTotalSeconds(userId, date));
    }

    // 월별 총 공부시간(초)
    @GetMapping("/monthly")
    public Map<String, Integer> monthly(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session) {
        Long userId = getCurrentUserId(session);
        return Map.of("totalSeconds", statisticsService.getMonthlyTotalSeconds(userId, date));
    }

    // 과목별 공부시간 합계 - Chart.js 그래프용 (subjectId -> 초)
    @GetMapping("/by-subject")
    public Map<Long, Integer> bySubject(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpSession session) {
        Long userId = getCurrentUserId(session);
        return statisticsService.getSecondsBySubject(userId, startDate, endDate);
    }
}
