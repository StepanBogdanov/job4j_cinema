package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    public SimpleFilmService(FilmRepository filmRepository, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<FilmDto> getFilmById(int id) {
        var filmOptional = filmRepository.findById(id);
        if (filmOptional.isEmpty()) {
            return Optional.empty();
        }
        var genreOptional = genreRepository.findById(filmOptional.get().getGenreId());
        return Optional.of(new FilmDto(
                filmOptional.get().getId(),
                filmOptional.get().getName(),
                filmOptional.get().getDescription(),
                filmOptional.get().getYear(),
                filmOptional.get().getMinimalAge(),
                filmOptional.get().getDurationInMinutes(),
                genreOptional.get().getName(),
                filmOptional.get().getFileId()
        ));
    }

    @Override
    public Collection<FilmDto> findAll() {
        var films = filmRepository.findAll();
        var filmsDto = films.stream().map(v -> new FilmDto(
                v.getId(),
                v.getName(),
                v.getDescription(),
                v.getYear(),
                v.getMinimalAge(),
                v.getDurationInMinutes(),
                genreRepository.findById(v.getGenreId()).get().getName(),
                v.getFileId()
        )).collect(Collectors.toList());
        return filmsDto;
    }
}
