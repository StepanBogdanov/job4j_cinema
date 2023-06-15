package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;

public interface TicketRepository {

    Optional<Ticket> save(Ticket ticket);

    Optional<Ticket> findByUniqueParameters(int sessionId, int rowNumber, int placeNumber);

    Collection<Ticket> findAll();

    boolean deleteById(int id);

}
