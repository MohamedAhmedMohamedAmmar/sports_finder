package com.sportfinder.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
ALTER TABLE `trainings` CHANGE `id` `id` INT(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE trainings DROP CONSTRAINT fk_old_name;

-- 2. Add new foreign key referencing a different schema
ALTER TABLE trainings 
ADD CONSTRAINT fk_new_name 
FOREIGN KEY (column_name) 
REFERENCES other_schema.parent_table(parent_id);


*/
/**
 * Maps to the real `training_availability` table.
 */
@Entity
@Table(name = "training_availability")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class TrainingAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;  // Mon|Tue|Wed|Thu|Fri|Sat|Sun

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private boolean isAvailable = true;
}
