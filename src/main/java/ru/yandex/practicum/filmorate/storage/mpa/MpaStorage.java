package ru.yandex.practicum.filmorate.storage.mpa;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM \"Mpa\"";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"Mpa\" WHERE \"Mpa_id\" = ?";

    public Collection<Mpa> getAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, mapper);
    }

    public Optional<Mpa> getById(Integer id) {
        try {
            Mpa mpa = jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, mapper, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            log.error("Рейтинг с данным id не найден: {}", id);
            return Optional.empty();
        }
    }
}
