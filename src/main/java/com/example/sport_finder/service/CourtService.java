package com.sportfinder.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sportfinder.dto.CourtForm;
import com.sportfinder.model.Amenity;
import com.sportfinder.model.Court;
import com.sportfinder.model.Sport;
import com.sportfinder.model.User;
import com.sportfinder.repository.AmenityRepository;
import com.sportfinder.repository.CourtRepository;
import com.sportfinder.repository.SportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final CourtRepository    courtRepository;
    private final SportRepository    sportRepository;
    private final AmenityRepository  amenityRepository;

    public List<Court> getAllCourts() {
        return courtRepository.findAllWithDetails();
    }

    public List<Court> getCourtsByOwner(User owner) {
        return courtRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Court getCourtById(Long id) {
        return courtRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Court not found: " + id));
    }

    public List<Sport> getAllSports() {
        return sportRepository.findByIsActiveTrue();
    }

    public List<Amenity> getAllAmenities() {
        return amenityRepository.findByIsActiveTrue();
    }

    /** Filter courts for the browse page (search + sport + type + price). */
    public List<Court> getFilteredCourts(String search, List<Long> sportIds,
                                         double minPrice, double maxPrice,
                                         List<String> types) {
        List<Court> all = courtRepository.findAllWithDetails();
        return all.stream()
            .filter(c -> search == null || search.isBlank()
                      || c.getName().toLowerCase().contains(search.toLowerCase())
                      || c.getCity().toLowerCase().contains(search.toLowerCase()))
            .filter(c -> sportIds == null || sportIds.isEmpty()
                      || (c.getSport() != null && sportIds.contains(c.getSport().getId())))
            .filter(c -> c.getPricePerHour().doubleValue() >= minPrice
                      && c.getPricePerHour().doubleValue() <= maxPrice)
            .filter(c -> types == null || types.isEmpty()
                      || types.stream().anyMatch(t -> t.equalsIgnoreCase(c.getCourtType())))
            .toList();
    }

    @Transactional
    public Court createCourt(CourtForm form, User owner) {
        return saveCourtFromForm(new Court(), form, owner);
    }

    @Transactional
    public Court updateCourt(Long courtId, CourtForm form, User owner) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new IllegalArgumentException("Court not found"));
        if (!court.getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Access denied");
        }
        return saveCourtFromForm(court, form, owner);
    }

    @Transactional
    public void deleteCourt(Long courtId, User owner) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new IllegalArgumentException("Court not found"));
        if (!court.getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Access denied");
        }
        courtRepository.delete(court);
    }

    private Court saveCourtFromForm(Court court, CourtForm form, User owner) {
        Sport sport = sportRepository.findById(form.getSportId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sport"));

        Set<Amenity> amenities = new HashSet<>();
        if (form.getAmenityIds() != null && !form.getAmenityIds().isEmpty()) {
            amenities = new HashSet<>(amenityRepository.findAllById(form.getAmenityIds()));
        }

        court.setName(form.getName());
        court.setOwner(owner);
        court.setSport(sport);
        // Store lowercase to match real DB ENUM('indoor','outdoor')
        court.setCourtType(form.getCourtType() != null ? form.getCourtType().toLowerCase() : "indoor");
        court.setPricePerHour(form.getPricePerHour());
        court.setAddress(form.getAddress());
        court.setCity(form.getCity());
        court.setDescription(form.getDescription());
        court.setImageUrl(form.getImageUrl());
        court.setAmenities(amenities);

        return courtRepository.save(court);
    }
}
