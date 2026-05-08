package com.example.sport_finder.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sport_finder.dto.TrainingForm;
import com.example.sport_finder.model.Sport;
import com.example.sport_finder.model.Training;
import com.example.sport_finder.model.User;
import com.example.sport_finder.repository.SportRepository;
import com.example.sport_finder.repository.TrainingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final SportRepository    sportRepository;

    public List<Training> getAllActiveTrainings() {
        return trainingRepository.findAllActiveWithDetails();
    }

    public List<Training> getTrainingsByCoach(User coach) {
        return trainingRepository.findByCoachOrderByCreatedAtDesc(coach);
    }

    public Training getById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Training not found: " + id));
    }

    @Transactional
    public Training create(TrainingForm form, User coach) {
        Sport sport = sportRepository.findById(form.getSportId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sport"));

        Training t = Training.builder()
                .name(form.getName())
                .coach(coach)
                .sport(sport)
                .level(form.getLevel())
                .specialty(form.getSpecialty())
                .pricePerHour(form.getPricePerHour())
                .yearsExperience(form.getYearsExperience())
                .description(form.getDescription())
                .goals(form.getGoals())
                .minSessions(form.getMinSessions())
                .location(form.getLocation())
                .equipment(form.getEquipment())
                .requirements(form.getRequirements())
                .certifications(form.getCertifications())
                .achievements(form.getAchievements())
                .imageUrl(form.getImageUrl())
                .status("Active")   // Real DB: Title-case 'Active'
                .build();

        return trainingRepository.save(t);
    }

    @Transactional
    public void delete(Long id, User coach) {
        Training t = getById(id);
        if (!t.getCoach().getId().equals(coach.getId())) {
            throw new SecurityException("Access denied");
        }
        // Soft delete: mark training as "Deleted" instead of hard deleting
        t.setStatus("Deleted");
        trainingRepository.save(t);
    }
}
