package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper mapper;
    private static final String FIND_ALL_QUERY = "SELECT * FROM \"Genres\"";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"Genres\" WHERE \"Genre_id\" = ?";

    public Collection<Genre> getAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, mapper);
    }

    public Optional<Genre> getById(Integer id) {
        try {
            Genre genre = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException ignored) {
            log.error("Жанр с данным id не найден: {}", id);
            return Optional.empty();
        }
    }
}
