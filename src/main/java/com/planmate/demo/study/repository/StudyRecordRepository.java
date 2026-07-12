package com.planmate.demo.study.repository;

import com.planmate.demo.study.model.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    // 특정 유저의 특정 기간 기록 전체 (일별/주별/월별 조회에 공통으로 사용)
    List<StudyRecord> findByUserIdAndStudyDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // 특정 유저 + 특정 과목의 기간별 기록 (과목별 통계용)
    List<StudyRecord> findByUserIdAndSubjectIdAndStudyDateBetween(
            Long userId, Long subjectId, LocalDate startDate, LocalDate endDate);

    // 현재 진행중인 타이머가 있는지 확인 (end_time이 null인 기록)
    Optional<StudyRecord> findFirstByUserIdAndEndTimeIsNull(Long userId);
}
