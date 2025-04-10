package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.dto.UserDto;

import java.util.Collection;

public interface UserStorage {

    public Collection<UserDto> getAll();

    public UserDto create(UserDto userDto);

    public UserDto update(UserDto userDto);

    public void addFriend(Integer id, Integer friendId);

    public void removeFriend(Integer id, Integer friendId);

    public Collection<UserDto> getUsersFriends(Integer id);

    public Collection<UserDto> getFriendsCommon(Integer id, Integer otherId);

    public boolean checkUserId(Integer id);
}
