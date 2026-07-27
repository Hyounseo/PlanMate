package com.planmate.demo.service;

import com.planmate.demo.entity.Subject;
import com.planmate.demo.repository.ScheduleRepository;
import com.planmate.demo.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ScheduleRepository scheduleRepository;

    public SubjectService(
            SubjectRepository subjectRepository,
            ScheduleRepository scheduleRepository
    ) {
        this.subjectRepository = subjectRepository;
        this.scheduleRepository = scheduleRepository;
    }

    // 사용자의 과목 목록 조회
    public List<Subject> getSubjects(Long userId) {
        return subjectRepository.findByUserId(userId);
    }

    // 새 과목 등록
    @Transactional
    public Subject createSubject(Long userId, Subject request) {
        request.setSubjectId(null);
        request.setUserId(userId);

        return subjectRepository.save(request);
    }

    // 과목 수정
    @Transactional
    public Subject updateSubject(
            Long subjectId,
            Long userId,
            Subject request
    ) {
        Subject subject = subjectRepository
                .findBySubjectIdAndUserId(subjectId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("과목을 찾을 수 없습니다.")
                );

        subject.setSubjectName(request.getSubjectName());
        subject.setColor(request.getColor());
        if (request.getTargetTime() != null) {
            subject.setTargetTime(request.getTargetTime());
        }

        return subjectRepository.save(subject);
    }

    // 과목과 과목에 연결된 일정 삭제
    @Transactional
    public void deleteSubject(Long subjectId, Long userId) {
        Subject subject = subjectRepository
                .findBySubjectIdAndUserId(subjectId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("과목을 찾을 수 없습니다.")
                );

        // 해당 과목에 연결된 일정부터 삭제
        scheduleRepository.deleteBySubjectIdAndUserId(
                subjectId,
                userId
        );

        // 과목 삭제
        subjectRepository.delete(subject);
    }
}