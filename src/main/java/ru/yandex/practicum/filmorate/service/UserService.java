package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<UserDto> getAll() {
        log.debug("Получен запрос на получение списка пользователей!");
        return userStorage.getAll();
    }

    public UserDto create(UserDto userDto) {
        log.debug("Получен запрос на создание пользователя с данными: {}", userDto);
        return userStorage.create(userDto);
    }

    public UserDto update(UserDto userDto) {
        log.debug("Получен запрос на обновление пользователя с данными: {}", userDto);
        return userStorage.update(userDto);
    }

    public void addFriend(Integer id, Integer friendId) {
        log.debug("Получен запрос на добавление в друзья с данными id: {} и friendId: {}", id, friendId);
        checkId(id, friendId);
        userStorage.addFriend(id,friendId);
    }

    public void removeFriend(Integer id, Integer friendId) {
        log.debug("Получен запрос на удаление из друзей с данными id: {} и friendId: {}", id, friendId);
        checkId(id, friendId);
        userStorage.removeFriend(id, friendId);
    }

    public Collection<UserDto> getUsersFriends(Integer id) {
        log.debug("Получен запрос на получение списка друзей с данными id: {}", id);
        if (id <= 0) {
            log.error("В запросе получено некорректное значение id: {}", id);
            throw new BadRequestException("Id пользователя не может быть меньше или равно нулю!");
        }
        if (!userStorage.checkUserId(id)) {
            log.error("Не найден пользователь по id: {}", id);
            throw new NotFoundException("Пользователь с id: " + id + " не найден!");
        }
        return userStorage.getUsersFriends(id);
    }

    public Collection<UserDto> getFriendsCommon(Integer id, Integer otherId) {
        log.debug("Получен запрос на получение общих друзей с данными id: {} и otherId: {}", id, otherId);
        checkId(id, otherId);
        return userStorage.getFriendsCommon(id, otherId);
    }

    private void checkId(Integer id, Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            log.error("В запросе получено некорректное значение id: {} или friendId: {}", id, friendId);
            throw new BadRequestException("Id пользователя не может быть меньше или равно нулю!");
        }
        if (!userStorage.checkUserId(id)) {
            log.error("Не найден пользователь по id: {}", id);
            throw new NotFoundException("Пользователь с id: " + id + " не найден!");
        }
        if (!userStorage.checkUserId(friendId)) {
            log.error("Не найден пользователь по friendId: {}", friendId);
            throw new NotFoundException("Пользователь с id: " + friendId + " не найден!");
        }
    }
}
