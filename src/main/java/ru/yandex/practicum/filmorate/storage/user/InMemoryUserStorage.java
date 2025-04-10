package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<UserDto> getAll() {
        if (users.values().isEmpty()) {
            log.warn("Ошибка валидации, список пользователей пуст!");
            throw new NotFoundException("Список пользователей пуст");
        }
        log.info("Получен список пользователей.");
        return users.values().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = convertToEntity(userDto);

        setDefaultNameIfEmpty(user);
        user.setId(generateNextId());

        users.put(user.getId(), user);
        log.info("Пользователь с именем {} добавлен", user.getName());
        return convertToDto(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        Integer id = userDto.getId();
        validateUserId(id);

        User existingUser = users.get(id);
        updateData(existingUser, userDto);

        log.info("Пользователь с id {} обновлен", id);
        return convertToDto(existingUser);
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        User user = users.get(id);
        User userFriend = users.get(friendId);
        user.addFriend(friendId);
        userFriend.addFriend(id);
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
    public Collection<UserDto> getUsersFriends(Integer id) {
        User user = users.get(id);
        return user.getFriends().stream()
                .map(users::get)
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Collection<UserDto> getFriendsCommon(Integer id, Integer otherId) {
        User user = users.get(id);
        User otherUser = users.get(otherId);
        return users.values().stream()
                .filter(friend ->  user.contains(friend.getId()) &&  otherUser.contains(friend.getId()))
                .map(this::convertToDto)
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

    private void setDefaultNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUserId(Integer id) {
        if (id == null) {
            log.error("Не указан id при запросе на обновление пользователя");
            throw new BadRequestException("Id должен быть указан");
        }
        if (!users.containsKey(id)) {
            log.warn("По переданному id {} пользователь не найден.", id);
            throw new NotFoundException("Пользователь не обнаружен по id: " + id);
        }
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setLogin(userDto.getLogin());
        user.setName(userDto.getName());
        user.setBirthday(userDto.getBirthday());
        return user;
    }

    private void updateData(User existingUser, UserDto userDto) {
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getLogin() != null) {
            existingUser.setLogin(userDto.getLogin());
        }
        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getBirthday() != null) {
            existingUser.setBirthday(userDto.getBirthday());
        }
    }
}
