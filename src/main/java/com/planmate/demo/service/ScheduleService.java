package com.planmate.demo.service;

import com.planmate.demo.entity.Schedule;
import com.planmate.demo.repository.ScheduleRepository;
import com.planmate.demo.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final SubjectRepository subjectRepository;

    public ScheduleService(
            ScheduleRepository scheduleRepository,
            SubjectRepository subjectRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.subjectRepository = subjectRepository;
    }

    // 특정 기간의 일정 목록 조회
    public List<Schedule> getSchedules(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return scheduleRepository
                .findByUserIdAndScheduleDateBetweenOrderByScheduleDateAscStartTimeAsc(
                        userId,
                        startDate,
                        endDate
                );
    }

    // 새 일정 등록
    @Transactional
    public Schedule createSchedule(Long userId, Schedule request) {

        // 해당 사용자에게 실제로 존재하는 과목인지 확인
        validateSubject(request.getSubjectId(), userId);

        request.setScheduleId(null);
        request.setUserId(userId);

        // 완료 상태를 입력하지 않은 경우 기본값 설정
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            request.setStatus("PLANNED");
        }

        return scheduleRepository.save(request);
    }

    // 기존 일정 수정
    @Transactional
    public Schedule updateSchedule(
            Long scheduleId,
            Long userId,
            Schedule request
    ) {
        Schedule schedule = scheduleRepository
                .findByScheduleIdAndUserId(scheduleId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("일정을 찾을 수 없습니다.")
                );

        validateSubject(request.getSubjectId(), userId);

        schedule.setSubjectId(request.getSubjectId());
        schedule.setTitle(request.getTitle());
        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setMemo(request.getMemo());

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            schedule.setStatus("PLANNED");
        } else {
            schedule.setStatus(request.getStatus());
        }

        return scheduleRepository.save(schedule);
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository
                .findByScheduleIdAndUserId(scheduleId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("일정을 찾을 수 없습니다.")
                );

        scheduleRepository.delete(schedule);
    }

    // 선택한 과목이 현재 사용자의 과목인지 확인
    private void validateSubject(Long subjectId, Long userId) {
        if (subjectId == null) {
            throw new IllegalArgumentException("과목을 선택해야 합니다.");
        }

        subjectRepository
                .findBySubjectIdAndUserId(subjectId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("과목을 찾을 수 없습니다.")
                );
    }
}