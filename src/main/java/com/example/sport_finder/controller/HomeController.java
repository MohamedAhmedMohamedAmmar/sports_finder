package com.example.sport_finder.controller;

import com.example.sport_finder.service.CourtService;
import com.example.sport_finder.service.TrainingService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourtService   courtService;
    private final TrainingService trainingService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Browse courts with optional filters matching the PHP browse.php logic:
     *   ?search=&sports[]=&min_price=&max_price=&types[]=
     */
    @GetMapping("/browse")
    public String browseCourts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(name = "sports", required = false) List<Long> sports,
            @RequestParam(name = "min_price", required = false, defaultValue = "0") double minPrice,
            @RequestParam(name = "max_price", required = false, defaultValue = "10000") double maxPrice,
            @RequestParam(name = "types", required = false) List<String> types,
            Model model) {

        model.addAttribute("courts",       courtService.getFilteredCourts(search, sports, minPrice, maxPrice, types));
        model.addAttribute("sports",       courtService.getAllSports());
        model.addAttribute("search",       search);
        model.addAttribute("selectedSports", sports   != null ? sports   : List.of());
        model.addAttribute("selectedTypes",  types    != null ? types    : List.of());
        model.addAttribute("minPrice",     minPrice);
        model.addAttribute("maxPrice",     maxPrice);
        return "courts/browse";
    }

    @GetMapping("/browse-sessions")
    public String browseSessions(Model model) {
        model.addAttribute("trainings", trainingService.getAllActiveTrainings());
        model.addAttribute("sports",    courtService.getAllSports());
        return "trainings/browse";
    }
}
