package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User save(User user);

    //User update(User user);

    User getById(long id);

    List<User> getAllUsers();

    void deleteById(long id);
}
