package com.planmate.demo.dashboard.repository;

import com.planmate.demo.dashboard.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

}