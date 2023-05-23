package ru.job4j.cinema.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.service.FileService;
import ru.job4j.cinema.service.FilmService;

@Controller
@RequestMapping("/library")
public class LibraryController {

    private final FilmService filmService;
    private final FileService fileService;

    public LibraryController(FilmService filmService, FileService fileService) {
        this.filmService = filmService;
        this.fileService = fileService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("films", filmService.findAll());
        return "library/list";
    }
}
