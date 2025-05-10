package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.Collection;


@Repository
@Primary
@Slf4j
public class FilmStorageDb extends FilmUtilDb implements FilmStorage  {

    public FilmStorageDb(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        super(jdbcTemplate, filmRowMapper);
    }

    @Override
    public Collection<Film> getAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, filmRowMapper);
    }

    @Override
    public Film create(Film film) {
        Integer id = insert(
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(id);
        if (film.getGenres() != null) {
            insertGenres(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        updateFilm(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            deleteGenreFilm(film.getId());
            insertGenres(film);
        }
        return film;
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        jdbcTemplate.update(ADD_LIKE_FILM_QUERY, id, userId);
    }

    @Override
    public void removeLike(Integer id, Integer userId) {
        jdbcTemplate.update(DELETE_LIKE_FILM_QUERY, id, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return jdbcTemplate.query(FIND_POPULAR_FILM_QUERY, filmRowMapper, count);
    }

    @Override
    public boolean checkIdStorage(Integer id) {
        String query = "SELECT COUNT(*) FROM \"Films\" WHERE \"Film_id\" = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Film getById(Integer id) {
        return jdbcTemplate.queryForObject(FIND_FILM_BY_ID, filmRowMapper, id);
    }
}

