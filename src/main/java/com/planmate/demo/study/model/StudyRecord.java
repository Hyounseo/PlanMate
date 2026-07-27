package com.planmate.demo.study.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * ERD의 Study_Record 테이블과 1:1 매핑되는 엔티티
 * record_id, user_id, subject_id, study_date, start_time, end_time, duration_seconds
 *
 * end_time / duration_seconds 가 null 이면 "타이머 진행중" 상태를 의미함
 */
@Entity
@Table(name = "study_record")
public class StudyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    protected StudyRecord() {
        // JPA 기본 생성자
    }

    // 타이머 "시작" 시점에 사용하는 생성자 (end_time, duration은 아직 없음)
    public StudyRecord(Long userId, Long subjectId, LocalDate studyDate, LocalTime startTime) {
        this.userId = userId;
        this.subjectId = subjectId;
        this.studyDate = studyDate;
        this.startTime = startTime;
    }

    // 타이머 "종료" 처리 - end_time을 받아서 duration_seconds까지 계산
    public void finish(LocalTime endTime) {
        if (this.startTime == null) {
            throw new IllegalStateException("시작 시각이 없는 기록은 종료할 수 없습니다.");
        }
        this.endTime = endTime;
        this.durationSeconds = (int) Duration.between(this.startTime, endTime).getSeconds();
    }

    public boolean isOngoing() {
        return this.endTime == null;
    }

    // Getter / Setter
    public Long getRecordId() {
        return recordId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public LocalDate getStudyDate() {
        return studyDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }
}