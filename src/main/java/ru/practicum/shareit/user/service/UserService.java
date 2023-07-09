package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto dto);

    UserDto update(Long id, UserDto dto);

    UserDto getById(long id);

    List<UserDto> getAllUsers();

    void deleteById(long id);
}
