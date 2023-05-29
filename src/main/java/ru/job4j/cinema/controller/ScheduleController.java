package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final FilmSessionService filmSessionService;
    private final HallService hallService;

    public ScheduleController(FilmSessionService filmSessionService, HallService hallService) {
        this.filmSessionService = filmSessionService;
        this.hallService = hallService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("filmSessions", filmSessionService.findAll());
        model.addAttribute("halls", hallService.findAll());
        return "schedule/list";
    }

}
