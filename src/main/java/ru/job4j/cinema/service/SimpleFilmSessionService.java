package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class SimpleFilmSessionService implements FilmSessionService {

    private final FilmSessionRepository filmSessionRepository;
    private final FilmRepository filmRepository;
    private final HallRepository hallRepository;

    public SimpleFilmSessionService(FilmSessionRepository filmSessionRepository, FilmRepository filmRepository, HallRepository hallRepository) {
        this.filmSessionRepository = filmSessionRepository;
        this.filmRepository = filmRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public Collection<FilmSessionDto> findAll() {
        var filmSessions = filmSessionRepository.findAll();
        var filmSessionsDto = filmSessions.stream().map(v -> new FilmSessionDto(
                v.getId(),
                filmRepository.findById(v.getFilmId()).get().getName(),
                hallRepository.findById(v.getHallsId()).get().getName(),
                hallRepository.findById(v.getHallsId()).get().getDescription(),
                v.getStartTime(),
                v.getEndTime(),
                v.getPrice()
        )).collect(Collectors.toList());
        return filmSessionsDto;
    }
}
