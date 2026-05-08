package com.example.sport_finder.repository;

import com.example.sport_finder.model.Court;
import com.example.sport_finder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByOwnerOrderByCreatedAtDesc(User owner);

    @Query("SELECT c FROM Court c LEFT JOIN FETCH c.sport LEFT JOIN FETCH c.owner ORDER BY c.createdAt DESC")
    List<Court> findAllWithDetails();

    @Query("SELECT c FROM Court c LEFT JOIN FETCH c.sport WHERE c.sport.id = :sportId ORDER BY c.createdAt DESC")
    List<Court> findBySportId(@Param("sportId") Long sportId);

    @Query("SELECT c FROM Court c LEFT JOIN FETCH c.sport WHERE LOWER(c.courtType) = LOWER(:type) ORDER BY c.createdAt DESC")
    List<Court> findByCourtType(@Param("type") String type);

    @Query("SELECT c FROM Court c LEFT JOIN FETCH c.sport WHERE LOWER(c.name) LIKE LOWER(CONCAT('%',:search,'%')) OR LOWER(c.city) LIKE LOWER(CONCAT('%',:search,'%')) ORDER BY c.createdAt DESC")
    List<Court> searchByNameOrCity(@Param("search") String search);
}
