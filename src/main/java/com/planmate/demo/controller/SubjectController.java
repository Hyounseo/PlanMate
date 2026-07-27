package com.planmate.demo.controller;

import com.planmate.demo.entity.Subject;
import com.planmate.demo.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // 로그인 기능 통합 전 임시 사용자 번호
    private static final Long TEMP_USER_ID = 1L;

    // 과목 목록 조회
    @GetMapping
    public List<Subject> getSubjects() {
        return subjectService.getSubjects(TEMP_USER_ID);
    }

    // 새 과목 등록
    @PostMapping
    public Subject createSubject(@RequestBody Subject request) {
        return subjectService.createSubject(TEMP_USER_ID, request);
    }

    // 과목 수정
    @PutMapping("/{subjectId}")
    public Subject updateSubject(
            @PathVariable Long subjectId,
            @RequestBody Subject request
    ) {
        return subjectService.updateSubject(
                subjectId,
                TEMP_USER_ID,
                request
        );
    }

    // 과목 삭제
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(
            @PathVariable Long subjectId
    ) {
        subjectService.deleteSubject(subjectId, TEMP_USER_ID);

        return ResponseEntity.noContent().build();
    }
}