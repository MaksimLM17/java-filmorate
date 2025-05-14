package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    public Collection<User> getAll();

    public User create(User user);

    public User update(User user);

    public void addFriend(Integer userId, Integer friendId);

    public void removeFriend(Integer userId, Integer friendId);

    public Collection<User> getUsersFriends(Integer userId);

    public Collection<User> getFriendsCommon(Integer userId, Integer otherId);

    public boolean checkUserId(Integer userId);
}
