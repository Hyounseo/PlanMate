package com.planmate.demo.study.service;

import com.planmate.demo.study.model.StudyRecord;
import com.planmate.demo.study.repository.StudyRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class StudyService {

    private final StudyRecordRepository studyRecordRepository;

    public StudyService(StudyRecordRepository studyRecordRepository) {
        this.studyRecordRepository = studyRecordRepository;
    }

    // 타이머 시작: 진행중인 기록이 있으면 막고, 없으면 새 기록 생성
    public StudyRecord startTimer(Long userId, Long subjectId) {
        studyRecordRepository.findFirstByUserIdAndEndTimeIsNull(userId)
                .ifPresent(r -> {
                    throw new IllegalStateException("이미 진행중인 타이머가 있습니다. 먼저 종료해주세요.");
                });

        StudyRecord record = new StudyRecord(userId, subjectId, LocalDate.now(), LocalTime.now());
        return studyRecordRepository.save(record);
    }

    // 타이머 종료: recordId로 찾아서 end_time, duration_seconds 채움
    public StudyRecord stopTimer(Long recordId, Long userId) {
        StudyRecord record = studyRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("기록을 찾을 수 없습니다."));

        if (!record.getUserId().equals(userId)) {
            throw new IllegalStateException("본인의 기록만 종료할 수 있습니다.");
        }
        if (!record.isOngoing()) {
            throw new IllegalStateException("이미 종료된 기록입니다.");
        }

        record.finish(LocalTime.now());
        return studyRecordRepository.save(record);
    }

    // 현재 진행중인 타이머 조회 (새로고침 시 프론트에서 상태 복구용)
    public StudyRecord getOngoingTimer(Long userId) {
        return studyRecordRepository.findFirstByUserIdAndEndTimeIsNull(userId).orElse(null);
    }

    // 특정 날짜의 기록 전체 조회 (오늘 기록 리스트 화면용)
    public java.util.List<StudyRecord> getRecordsByDate(Long userId, java.time.LocalDate date) {
        return studyRecordRepository.findByUserIdAndStudyDateBetween(userId, date, date);
    }
}