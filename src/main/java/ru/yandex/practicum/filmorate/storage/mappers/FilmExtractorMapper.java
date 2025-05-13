package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmExtractorMapper implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> films = new HashMap<>();
        while (rs.next()) {
            Integer id = rs.getInt("Film_id");
            if (films.containsKey(id)) {
                Film existingFilm = films.get(id);
                Integer genreId = rs.getInt("Genre_id");
                String genreName = rs.getString("Genre_name");
                Genre genre = Genre.builder().id(genreId).name(genreName).build();
                existingFilm.getGenres().add(genre);
            } else {
                String name = rs.getString("Film_name");
                String description = rs.getString("Film_description");
                Timestamp date = rs.getTimestamp("Film_release_date");
                LocalDate releaseDate = date.toLocalDateTime().toLocalDate();
                Integer duration = rs.getInt("Film_duration");
                Integer mpaId = rs.getInt("Mpa_id");
                String mpaName = rs.getString("Mpa_name");
                Integer genreId = rs.getInt("Genre_id");
                String genreName = rs.getString("Genre_name");
                Mpa mpa = Mpa.builder().id(mpaId).name(mpaName).build();
                Genre genre = Genre.builder().id(genreId).name(genreName).build();
                List<Genre> genres = new ArrayList<>();
                genres.add(genre);
                Film film = Film.builder().id(id).name(name).description(description).releaseDate(releaseDate).duration(duration)
                        .mpa(mpa).genres(genres).build();
                films.put(id, film);
            }

        }
        return new ArrayList<>(films.values());
    }
}
