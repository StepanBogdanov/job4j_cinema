package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.TicketService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScheduleControllerTest {

    private FilmSessionService filmSessionService;
    private HallService hallService;
    private FilmService filmService;
    private TicketService ticketService;
    private ScheduleController scheduleController;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    public void initService() {
        filmSessionService = mock(FilmSessionService.class);
        hallService = mock(HallService.class);
        filmService = mock(FilmService.class);
        ticketService = mock(TicketService.class);
        scheduleController = new ScheduleController(filmSessionService, hallService, filmService, ticketService);
        startTime = now().truncatedTo(ChronoUnit.MINUTES);
        endTime = now().truncatedTo(ChronoUnit.MINUTES).plusHours(1);
    }

    @Test
    public void whenRequestSchedulePageThenGetPageWithSessions() {
        var hall1 = new Hall(1, "hall1", 10, 10, "test1");
        var hall2 = new Hall(2, "hall2", 10, 10, "test2");
        var expectedHalls = List.of(hall1, hall2);
        var filmSession1 = new FilmSessionDto(1, "film1", "hall1", "test1", startTime, endTime, 100);
        var filmSession2 = new FilmSessionDto(2, "film2", "hall2", "test2", startTime, endTime, 100);
        var expectedSessions = List.of(filmSession1, filmSession2);
        when(hallService.findAll()).thenReturn(expectedHalls);
        when(filmSessionService.findAll()).thenReturn(expectedSessions);

        var model = new ConcurrentModel();
        var view = scheduleController.getAll(model);
        var actualHalls = model.getAttribute("halls");
        var actualSessions = model.getAttribute("filmSessions");

        assertThat(view).isEqualTo("schedule/list");
        assertThat(actualHalls).isEqualTo(expectedHalls);
        assertThat(actualSessions).isEqualTo(expectedSessions);
    }

    @Test
    public void whenRequestBuyPageThenGetPageWithSession() {
        var filmSession = new FilmSession(1, 1, 1, startTime, endTime, 100);
        var film = new FilmDto(1, "name", "desc", 2000, 18, 120, "genre", 1);
        var hall = new Hall(1, "hall", 10, 10, "desc");
        when(filmSessionService.findById(anyInt())).thenReturn(Optional.of(filmSession));
        when(filmService.getFilmById(anyInt())).thenReturn(Optional.of(film));
        when(hallService.findById(anyInt())).thenReturn(Optional.of(hall));

        var model = new ConcurrentModel();
        var view = scheduleController.getBuyPage(model, 1);
        var actualFilmSession = model.getAttribute("filmSession");
        var actualFilm = model.getAttribute("film");
        var actualHall = model.getAttribute("hall");

        assertThat(view).isEqualTo("schedule/one");
        assertThat(actualFilmSession).isEqualTo(filmSession);
        assertThat(actualFilm).isEqualTo(film);
        assertThat(actualHall).isEqualTo(hall);
    }

    @Test
    public void whenRequestBuyPageWithNotExistingFilmSessionThenGetErrorPage() {
        when(filmSessionService.findById(anyInt())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = scheduleController.getBuyPage(model, 1);

        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenRequestBuyPageWithNotExistingFilmThenGetErrorPage() {
        when(filmService.getFilmById(anyInt())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = scheduleController.getBuyPage(model, 1);

        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenRequestBuyPageWithNotExistingHallThenGetErrorPage() {
        when(hallService.findById(anyInt())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = scheduleController.getBuyPage(model, 1);

        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenBuyTicketOnFreePlaceThenGetSuccessPage() {
        var ticket = new Ticket(1, 1, 1, 1, 1);
        var ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        when(ticketService.findByUniqueParameters(anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        when(ticketService.save(ticketArgumentCaptor.capture())).thenReturn(Optional.of(ticket));

        var model = new ConcurrentModel();
        var view = scheduleController.buyTicket(ticket, model);
        var actualTicket = ticketArgumentCaptor.getValue();

        assertThat(view).isEqualTo("schedule/success");
        assertThat(actualTicket).isEqualTo(ticket);
    }

    @Test
    public void whenBuyTicketOnOccupiedPlaceThenGetFailPage() {
        var ticket = new Ticket(1, 1, 1, 1, 1);
        when(ticketService.findByUniqueParameters(anyInt(), anyInt(), anyInt())).thenReturn(Optional.of(ticket));

        var model = new ConcurrentModel();
        var view = scheduleController.buyTicket(ticket, model);

        assertThat(view).isEqualTo("schedule/fail");
    }
}