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

    // 로그인한 사용자의 과목 목록 조회
    @GetMapping
    public List<Subject> getSubjects(
            @SessionAttribute("userId") Long userId
    ) {
        return subjectService.getSubjects(userId);
    }

    // 로그인한 사용자의 새 과목 등록
    @PostMapping
    public Subject createSubject(
            @SessionAttribute("userId") Long userId,
            @RequestBody Subject request
    ) {
        return subjectService.createSubject(userId, request);
    }

    // 로그인한 사용자의 과목 수정
    @PutMapping("/{subjectId}")
    public Subject updateSubject(
            @SessionAttribute("userId") Long userId,
            @PathVariable Long subjectId,
            @RequestBody Subject request
    ) {
        return subjectService.updateSubject(
                subjectId,
                userId,
                request
        );
    }

    // 로그인한 사용자의 과목 삭제
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(
            @SessionAttribute("userId") Long userId,
            @PathVariable Long subjectId
    ) {
        subjectService.deleteSubject(subjectId, userId);

        return ResponseEntity.noContent().build();
    }
}