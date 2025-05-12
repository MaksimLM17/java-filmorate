package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;

@RequiredArgsConstructor
@Slf4j
public class FilmUtilDb {

    protected final JdbcTemplate jdbcTemplate;
    protected final FilmRowMapper filmRowMapper;

    protected static final String FIND_ALL_QUERY = "SELECT " +
            "\"F\".\"Film_id\", \"F\".\"Film_name\", \"F\".\"Film_description\", " +
            "\"F\".\"Film_release_date\", \"F\".\"Film_duration\", " +
            "\"M\".\"Mpa_id\", \"M\".\"Mpa_name\" " +
            "FROM \"Films\" AS \"F\" " +
            "LEFT JOIN \"Mpa\" AS \"M\" ON \"F\".\"Mpa_id\" = \"M\".\"Mpa_id\"";

    protected static final String INSERT_FILM_QUERY = "INSERT INTO \"Films\"(\"Film_name\", \"Film_description\", " +
            "\"Film_release_date\", \"Film_duration\", \"Mpa_id\") VALUES(?,?,?,?,?)";

    protected static final String INSERT_GENRE_FILM_QUERY = "INSERT INTO \"Genre_film\"(\"Film_id\", \"Genre_id\")" +
            "VALUES(?,?)";

    protected static final String UPDATE_FILM_QUERY = "UPDATE \"Films\" SET \"Film_name\" = ?, \"Film_description\" = ?, " +
            "\"Film_release_date\" = ?, \"Film_duration\" = ?, \"Mpa_id\" = ? WHERE \"Film_id\" = ?";

    protected static final String DELETE_GENRE_FILM_QUERY = "DELETE FROM \"Genre_film\" WHERE \"Film_id\" = ?";

    protected static final String FIND_FILM_BY_ID = "SELECT \"F\".\"Film_id\", \"F\".\"Film_name\", \"F\".\"Film_description\", " +
            "\"F\".\"Film_release_date\", \"F\".\"Film_duration\", \"M\".\"Mpa_id\", \"M\".\"Mpa_name\" " +
            "FROM \"Films\" AS \"F\" " +
            "LEFT JOIN \"Mpa\" AS \"M\" ON \"F\".\"Mpa_id\" = \"M\".\"Mpa_id\" " +
            "WHERE \"F\".\"Film_id\" = ?";
    protected static final String ADD_LIKE_FILM_QUERY = "INSERT INTO \"Likes\"(\"Film_id\", \"User_id\") VALUES(?,?)";
    protected static final String DELETE_LIKE_FILM_QUERY = "DELETE FROM \"Likes\" WHERE \"Film_id\" = ? AND \"User_id\" = ?";



    protected static final String FIND_POPULAR_FILM_QUERY = "SELECT \"F\".\"Film_id\", \"F\".\"Film_name\"," +
            " \"F\".\"Film_description\", \"F\".\"Film_release_date\", \"F\".\"Film_duration\"," +
            " \"M\".\"Mpa_id\", \"M\".\"Mpa_name\" " +
            " FROM \"Films\" AS \"F\" LEFT JOIN (SELECT \"Film_id\"," +
            " COUNT(\"User_id\") AS \"like_count\" FROM \"Likes\" GROUP BY \"Film_id\") AS \"L\" " +
            "ON \"F\".\"Film_id\" = \"L\".\"Film_id\"" +
            " LEFT JOIN \"Mpa\" AS \"M\" ON \"F\".\"Mpa_id\" = \"M\".\"Mpa_id\" " +
            " ORDER BY \"L\".\"like_count\" DESC LIMIT ?";

    protected Integer insert(Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            log.error("Не удалось сохранить данные при добавлении фильма");
            throw new InternalServerException("Не удалось сохранить данные при добавлении фильма");
        }
    }

    protected void updateFilm(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные фильма");
        }
    }

    protected void insertGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(INSERT_GENRE_FILM_QUERY, film.getId(), genre.getId());
        }
    }

    protected void deleteGenreFilm(Integer id) {
        int rowsDeleted = jdbcTemplate.update(DELETE_GENRE_FILM_QUERY, id);
        if (rowsDeleted == 0) {
            log.info("Не удалось удалить записи из таблицы Genre_film");
        } else {
            log.info("Удалено {} строки из таблицы Genre_film", rowsDeleted);
        }
    }
}
