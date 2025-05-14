package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("userStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        log.info("Получен список пользователей.");
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(generateNextId());
        users.put(user.getId(), user);

        log.info("Пользователь с именем {} добавлен", user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        Integer id = user.getId();
        User newUser = users.get(id);
        updateData(newUser, user);

        log.info("Пользователь с id {} обновлен", id);
        return newUser;
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        User user = users.get(id);
        user.addFriend(friendId);
        log.info("Пользователь с id: {}, добавил в друзья пользователя с id: {}",id, friendId);
    }

    @Override
    public void removeFriend(Integer id, Integer friendId) {
        User user = users.get(id);
        User userFriend = users.get(friendId);
        user.removeFriend(friendId);
        userFriend.removeFriend(id);
        log.info("Пользователь с id: {}, удалил из друзей пользователя с id: {}",id, friendId);
    }

    @Override
    public Collection<User> getUsersFriends(Integer id) {
        User user = users.get(id);
        return user.getFriends().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> getFriendsCommon(Integer id, Integer otherId) {
        User user = users.get(id);
        User otherUser = users.get(otherId);
        return users.values().stream()
                .filter(friend ->  user.isFriend(friend.getId()) &&  otherUser.isFriend(friend.getId()))
                .toList();
    }

    public boolean checkUserId(Integer id) {
        return users.containsKey(id);
    }

    private int generateNextId() {
        int currentMaxId = users.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void updateData(User user, User newUser) {
        if (newUser.getEmail() != null) {
            user.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            user.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            user.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null) {
            user.setBirthday(newUser.getBirthday());
        }
    }
}
