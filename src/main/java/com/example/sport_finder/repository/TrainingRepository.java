package com.sportfinder.repository;

import com.sportfinder.model.Training;
import com.sportfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByCoachOrderByCreatedAtDesc(User coach);

    /** status column uses Title-case 'Active' */
    @Query("SELECT t FROM Training t LEFT JOIN FETCH t.sport LEFT JOIN FETCH t.coach WHERE t.status = 'Active' ORDER BY t.createdAt DESC")
    List<Training> findAllActiveWithDetails();

    @Query("SELECT t FROM Training t LEFT JOIN FETCH t.sport LEFT JOIN FETCH t.coach WHERE t.sport.id = :sportId AND t.status = 'Active'")
    List<Training> findActiveBySportId(@Param("sportId") Long sportId);
}
