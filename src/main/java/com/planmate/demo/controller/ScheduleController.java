package com.planmate.demo.controller;

import com.planmate.demo.entity.Schedule;
import com.planmate.demo.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 로그인 기능 통합 전 임시 사용자 번호
    private static final Long TEMP_USER_ID = 1L;

    // 특정 기간의 일정 조회
    // 예: /api/schedules?startDate=2026-07-01&endDate=2026-07-31
    @GetMapping
    public List<Schedule> getSchedules(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return scheduleService.getSchedules(
                TEMP_USER_ID,
                startDate,
                endDate
        );
    }

    // 새 일정 등록
    @PostMapping
    public Schedule createSchedule(@RequestBody Schedule request) {
        return scheduleService.createSchedule(
                TEMP_USER_ID,
                request
        );
    }

    // 일정 수정
    @PutMapping("/{scheduleId}")
    public Schedule updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody Schedule request
    ) {
        return scheduleService.updateSchedule(
                scheduleId,
                TEMP_USER_ID,
                request
        );
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long scheduleId
    ) {
        scheduleService.deleteSchedule(
                scheduleId,
                TEMP_USER_ID
        );

        return ResponseEntity.noContent().build();
    }
}
