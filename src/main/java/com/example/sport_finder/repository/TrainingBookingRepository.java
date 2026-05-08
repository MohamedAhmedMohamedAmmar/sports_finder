package com.example.sport_finder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.sport_finder.model.Training;
import com.example.sport_finder.model.TrainingBooking;
import com.example.sport_finder.model.User;

@Repository
public interface TrainingBookingRepository extends JpaRepository<TrainingBooking, Long> {

    List<TrainingBooking> findByTraineeOrderByCreatedAtDesc(User trainee);

    List<TrainingBooking> findByTraining(Training training);

    @Query("SELECT b FROM TrainingBooking b JOIN FETCH b.training t JOIN FETCH b.trainee WHERE t.coach = :coach ORDER BY b.createdAt DESC")
    List<TrainingBooking> findByCoach(@Param("coach") User coach);

    Optional<TrainingBooking> findByIdAndTraineeId(Long id, Long traineeId);

    @Query("SELECT b FROM TrainingBooking b JOIN FETCH b.training t JOIN FETCH b.trainee WHERE t.coach = :coach AND b.id = :id")
    Optional<TrainingBooking> findByIdAndCoach(@Param("id") Long id, @Param("coach") User coach);
}
