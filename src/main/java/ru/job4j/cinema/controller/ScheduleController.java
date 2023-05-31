package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final FilmSessionService filmSessionService;
    private final HallService hallService;
    private final FilmService filmService;

    public ScheduleController(FilmSessionService filmSessionService, HallService hallService, FilmService filmService) {
        this.filmSessionService = filmSessionService;
        this.hallService = hallService;
        this.filmService = filmService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("filmSessions", filmSessionService.findAll());
        model.addAttribute("halls", hallService.findAll());
        return "schedule/list";
    }

    @GetMapping("/{id}")
    public String getBuyPage(Model model, @PathVariable int id) {

        var filmSessionOptional = filmSessionService.findById(id);
        if (filmSessionOptional.isEmpty()) {
            model.addAttribute("message", "Сеанс с указанным идентификатором не найден");
            return "errors/404";
        }
        var filmOptional = filmService.getFilmById(filmSessionOptional.get().getFilmId());
        if (filmOptional.isEmpty()) {
            model.addAttribute("message", "Фильм с указанным идентификатором не найден");
            return "errors/404";
        }
        var hallOptional = hallService.findById(filmSessionOptional.get().getHallsId());
        if (hallOptional.isEmpty()) {
            model.addAttribute("message", "Зал с указанным идентификатором не найден");
            return "errors/404";
        }
        model.addAttribute("filmSession", filmSessionOptional.get());
        model.addAttribute("film", filmOptional.get());
        model.addAttribute("hall", hallOptional.get());
        return "schedule/buy";
    }

}
