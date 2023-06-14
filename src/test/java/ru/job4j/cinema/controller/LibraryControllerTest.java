package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.service.FilmService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


class LibraryControllerTest {

    private FilmService filmService;
    private LibraryController libraryController;

    @BeforeEach
    public void initService() {
        filmService = mock(FilmService.class);
        libraryController = new LibraryController(filmService);
    }


    @Test
    public void whenRequestLibraryPageThenGetPageWithFilms() {
        var film1 = new FilmDto(1, "name1", "test1", 2000, 10, 100, "genre1", 1);
        var film2 = new FilmDto(2, "name2", "test2", 2000, 10, 100, "genre2", 2);
        var expectedFilms = List.of(film1, film2);
        when(filmService.findAll()).thenReturn(expectedFilms);

        var model = new ConcurrentModel();
        var view = libraryController.getAll(model);
        var actualFilms = model.getAttribute("films");

        assertThat(view).isEqualTo("library/list");
        assertThat(actualFilms).isEqualTo(expectedFilms);
    }


}