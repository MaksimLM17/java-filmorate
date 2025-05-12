package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository("userStorageDb")
@Slf4j
public class UserStorageDb extends UserUtilDb implements UserStorage {

    public UserStorageDb(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        super(jdbcTemplate, userRowMapper);
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(FIND_USERS_QUERY, userRowMapper);
    }

    @Override
    public User create(User user) {
        Integer id = insert(user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());;
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        updateUser(UPDATE_USER_QUERY, user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(ADD_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        int rowsDeleted = jdbcTemplate.update(DELETE_FRIEND_QUERY, userId, friendId);
        if (rowsDeleted == 0) {
            log.info("Не удалось удалить записи из таблицы Friends");
        } else {
            log.info("Удалено {} строк(и) из таблицы Friends", rowsDeleted);
        }
    }

    @Override
    public Collection<User> getUsersFriends(Integer userId) {
        List<Integer> friendsId = jdbcTemplate.queryForList(FIND_ALL_FRIENDS_USER_QUERY, new Object[]{userId}, Integer.class);
        List<User> friends = new ArrayList<>();
        for (Integer id : friendsId) {
            friends.add(jdbcTemplate.queryForObject(FIND_USER_ID_QUERY, userRowMapper, id));
        }
        return friends;
    }

    @Override
    public Collection<User> getFriendsCommon(Integer userId, Integer otherId) {
        List<Integer> userFriends = jdbcTemplate.queryForList(FIND_ALL_FRIENDS_USER_QUERY, new Object[]{userId}, Integer.class);
        List<Integer> otherUserFriends = jdbcTemplate.queryForList(FIND_ALL_FRIENDS_USER_QUERY, new Object[]{otherId}, Integer.class);

        userFriends.retainAll(otherUserFriends);

        if (userFriends.isEmpty()) {
            throw new NotFoundException("У данных пользователей нет общих друзей.");
        }

        List<User> commonFriends = new ArrayList<>();
        for (Integer friendId : userFriends) {
            commonFriends.add(jdbcTemplate.queryForObject(FIND_USER_ID_QUERY, userRowMapper, friendId));
        }

        return commonFriends;
    }

    @Override
    public boolean checkUserId(Integer id) {
        Integer number = jdbcTemplate.queryForObject(CHECK_USER_ID_QUERY, Integer.class, id);
        return number != null && number > 0;
    }
}
