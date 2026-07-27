package com.planmate.demo.repository;

import com.planmate.demo.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByUserId(Long userId);

    Optional<Subject> findBySubjectIdAndUserId(Long subjectId, Long userId);
}
