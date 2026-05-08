package com.example.sport_finder.repository;

import com.example.sport_finder.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {
    List<Sport> findByIsActiveTrue();
}
