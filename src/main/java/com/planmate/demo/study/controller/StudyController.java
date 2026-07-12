package com.planmate.demo.study.controller;

import com.planmate.demo.study.dto.StudyRecordResponse;
import com.planmate.demo.study.model.StudyRecord;
import com.planmate.demo.study.service.StudyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study")
public class StudyController {

    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    // TODO: 팀원1(User) 파트에서 세션 키 이름이 확정되면 "userId" 부분 맞춰서 수정
    private Long getCurrentUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return userId;
    }

    // 타이머 시작
    @PostMapping("/start")
    public StudyRecordResponse start(@RequestParam Long subjectId, HttpSession session) {
        Long userId = getCurrentUserId(session);
        StudyRecord record = studyService.startTimer(userId, subjectId);
        return new StudyRecordResponse(record);
    }

    // 타이머 종료
    @PostMapping("/stop/{recordId}")
    public StudyRecordResponse stop(@PathVariable Long recordId, HttpSession session) {
        Long userId = getCurrentUserId(session);
        StudyRecord record = studyService.stopTimer(recordId, userId);
        return new StudyRecordResponse(record);
    }

    // 진행중인 타이머 조회 (새로고침 시 프론트에서 상태 복구용)
    @GetMapping("/ongoing")
    public StudyRecordResponse ongoing(HttpSession session) {
        Long userId = getCurrentUserId(session);
        StudyRecord record = studyService.getOngoingTimer(userId);
        return record == null ? null : new StudyRecordResponse(record);
    }

    // 특정 날짜 기록 리스트 조회 (기본값: 오늘) - record.html 테이블용
    @GetMapping("/list")
    public java.util.List<StudyRecordResponse> list(
            @RequestParam(required = false) java.time.LocalDate date,
            HttpSession session) {
        Long userId = getCurrentUserId(session);
        java.time.LocalDate targetDate = (date != null) ? date : java.time.LocalDate.now();
        return studyService.getRecordsByDate(userId, targetDate)
                .stream()
                .map(StudyRecordResponse::new)
                .toList();
    }
}