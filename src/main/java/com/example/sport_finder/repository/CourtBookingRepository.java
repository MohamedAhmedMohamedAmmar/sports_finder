package com.sportfinder.repository;

import com.sportfinder.model.Court;
import com.sportfinder.model.CourtBooking;
import com.sportfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtBookingRepository extends JpaRepository<CourtBooking, Long> {

    List<CourtBooking> findByTraineeOrderByCreatedAtDesc(User trainee);

    @Query("SELECT b FROM CourtBooking b JOIN FETCH b.court c JOIN FETCH b.trainee WHERE c.owner = :owner ORDER BY b.createdAt DESC")
    List<CourtBooking> findByCourtOwner(@Param("owner") User owner);

    /** Conflict check: same court, same date, overlapping time, not cancelled. */
    @Query("SELECT b FROM CourtBooking b WHERE b.court = :court AND b.bookingDate = :date " +
           "AND b.status NOT IN ('Cancelled','Declined') " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<CourtBooking> findConflicting(@Param("court") Court court,
                                       @Param("date") LocalDate date,
                                       @Param("startTime") LocalTime startTime,
                                       @Param("endTime") LocalTime endTime);

    Optional<CourtBooking> findByIdAndTraineeId(Long id, Long traineeId);
}
