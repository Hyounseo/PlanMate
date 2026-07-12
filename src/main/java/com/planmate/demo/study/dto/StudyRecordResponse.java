package com.planmate.demo.study.dto;

import com.planmate.demo.study.model.StudyRecord;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudyRecordResponse {

    private final Long recordId;
    private final Long subjectId;
    private final LocalDate studyDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final Integer durationSeconds;
    private final boolean ongoing;

    public StudyRecordResponse(StudyRecord record) {
        this.recordId = record.getRecordId();
        this.subjectId = record.getSubjectId();
        this.studyDate = record.getStudyDate();
        this.startTime = record.getStartTime();
        this.endTime = record.getEndTime();
        this.durationSeconds = record.getDurationSeconds();
        this.ongoing = record.isOngoing();
    }

    public Long getRecordId() {
        return recordId;
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

    public boolean isOngoing() {
        return ongoing;
    }
}
