package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Slf4j
@RequiredArgsConstructor
public class UserUtilDb {

    protected final JdbcTemplate jdbcTemplate;
    protected final UserRowMapper userRowMapper;

    protected static final String FIND_ALL_USER_QUERY = "SELECT * FROM \"Users\"";
    protected static final String CREATE_USER_QUERY = "INSERT INTO \"Users\"(\"Login\", \"Username\", \"Email\", " +
            "\"Birthday\") VALUES (?,?,?,?)";
    protected static final String UPDATE_USER_QUERY = "UPDATE \"Users\" SET \"Email\" = ?, \"Login\" = ?, \"Username\" = ?," +
            "\"Birthday\" = ? WHERE \"User_id\" = ?";
    protected static final String ADD_FRIEND_QUERY = "INSERT INTO \"Friends\" (\"User_id\", \"Friend_id\") VALUES(?,?)";
    protected static final String DELETE_FRIEND_QUERY = "DELETE FROM \"Friends\" WHERE \"User_id\" = ? AND \"Friend_id\" = ?";
    protected static final String FIND_ALL_FRIENDS_USER_QUERY = "SELECT \"Friend_id\" FROM \"Friends\" WHERE \"User_id\" = ?";
    protected static final String FIND_USER_ID_QUERY = "SELECT * FROM \"Users\" WHERE \"User_id\" = ?";
    protected static final String CHECK_USER_ID_QUERY = "SELECT COUNT(*) FROM \"Users\" WHERE \"User_id\" = ?";

    protected Integer insert(Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage());
            throw new InternalServerException("Не удалось сохранить данные при создании пользователя");
        }
        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные при создании пользователя");
        }
    }

    protected void updateUser(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные пользователя");
        }
    }
}
