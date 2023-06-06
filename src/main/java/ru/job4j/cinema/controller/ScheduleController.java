package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final FilmSessionService filmSessionService;
    private final HallService hallService;
    private final FilmService filmService;
    private final TicketService ticketService;

    public ScheduleController(FilmSessionService filmSessionService, HallService hallService, FilmService filmService, TicketService ticketService) {
        this.filmSessionService = filmSessionService;
        this.hallService = hallService;
        this.filmService = filmService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("filmSessions", filmSessionService.findAll());
        model.addAttribute("halls", hallService.findAll());
        return "schedule/list";
    }

    @GetMapping("/{id}")
    public String getBuyPage(Model model, @PathVariable int id, HttpSession session) {

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
        var user = (User) session.getAttribute("user");
        model.addAttribute("filmSession", filmSessionOptional.get());
        model.addAttribute("film", filmOptional.get());
        model.addAttribute("hall", hallOptional.get());
        model.addAttribute("user", user);
        return "schedule/one";
    }

    @PostMapping("/buy")
    public String buyTicket(@ModelAttribute Ticket ticket, Model model) {
        if (ticketService.findByUniqueParameters(ticket.getSessionId(), ticket.getRowNumber(), ticket.getPlaceNumber()).isPresent()) {
            model.addAttribute("message", "Не удалось приобрести билет на заданное место. " +
                    "Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
            model.addAttribute("ticket", ticket);
            return "schedule/fail";
        }
        ticketService.save(ticket);
        String success = String.format("Вы успешно приобрели билет на место %d-%d", ticket.getRowNumber(), ticket.getPlaceNumber());
        model.addAttribute("message", success);
        return "schedule/success";
    }

}
