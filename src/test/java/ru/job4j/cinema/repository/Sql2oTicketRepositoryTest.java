package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Ticket;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oTicketRepositoryTest {

    private static Sql2oTicketRepository sql2oTicketRepository;

    @BeforeAll
    public static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oTicketRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
    }

    @AfterEach
    public void clearTickets() {
        var tickets = sql2oTicketRepository.findAll();
        for (Ticket ticket : tickets) {
            sql2oTicketRepository.deleteById(ticket.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var ticket = sql2oTicketRepository.save(new Ticket(1, 1, 1, 1, 1)).get();
        var savedTicket = sql2oTicketRepository.findByUniqueParameters(1, 1, 1).get();
        assertThat(savedTicket).isEqualTo(ticket);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var ticket1 = sql2oTicketRepository.save(new Ticket(1, 1, 1, 1, 1)).get();
        var ticket2 = sql2oTicketRepository.save(new Ticket(1, 1, 1, 2, 1)).get();
        var ticket3 = sql2oTicketRepository.save(new Ticket(1, 1, 1, 3, 1)).get();
        var savedTickets = sql2oTicketRepository.findAll();
        assertThat(savedTickets).isEqualTo(List.of(ticket1, ticket2, ticket3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oTicketRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oTicketRepository.findByUniqueParameters(1, 1, 1)).isEmpty();
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var ticket = sql2oTicketRepository.save(new Ticket(1, 1, 1, 1, 1)).get();
        var isDeleted = sql2oTicketRepository.deleteById(ticket.getId());
        var savedTicket = sql2oTicketRepository.findByUniqueParameters(1, 1, 1).get();
        assertThat(isDeleted).isTrue();
        assertThat(savedTicket).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oTicketRepository.deleteById(0)).isFalse();
    }


}