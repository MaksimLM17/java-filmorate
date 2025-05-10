package ru.yandex.practicum.filmorate.storage.mappers;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;
    private final MpaRowMapper mpaRowMapper;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("Film_id"));
        film.setName(rs.getString("Film_name"));
        film.setDescription(rs.getString("Film_description"));

        Timestamp releaseDate = rs.getTimestamp("Film_release_date");
        film.setReleaseDate(LocalDate.from(releaseDate.toLocalDateTime()));
        film.setDuration(rs.getInt("Film_duration"));
        film.setMpa(getMpaId(film.getId()));
        film.setGenres(getGenresId(film.getId()));

        return film;
    }

    private List<Genre> getGenresId(Integer filmId) {
        String query = "SELECT * FROM \"Genres\"" +
                " JOIN \"Genre_film\" ON \"Genres\".\"Genre_id\" = \"Genre_film\".\"Genre_id\"" +
                " WHERE \"Genre_film\".\"Film_id\" = ?";
        return jdbcTemplate.query(query, genreRowMapper, filmId);
    }

    private Mpa getMpaId(Integer filmId) {
        String query = "SELECT * FROM \"Mpa\" JOIN \"Films\" ON \"Mpa\".\"Mpa_id\" = \"Films\".\"Mpa_id\"" +
                " WHERE \"Film_id\" = ?";
        return jdbcTemplate.queryForObject(query, mpaRowMapper, filmId);
    }
}
