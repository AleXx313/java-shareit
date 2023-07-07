package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public UserDto save(UserDto dto) {
        checkEmailExistException(dto.getEmail());
        User savedUser = storage.save(UserMapper.dtoToUser(dto));
        log.debug("Создан пользователь по имени - {} с id - {}!", savedUser.getName(), savedUser.getId());
        return UserMapper.userToDto(savedUser);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User user = storage.getById(id);
        if (user == null) {
            throw new ModelNotFoundException(
                    String.format("Пользователь с id - %d не найден!",
                            dto.getId())
            );
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            checkEmailExistException(dto.getEmail());
        }
        User updatedUser = updateUserFields(user, dto);
        log.debug("Обновлен пользователь по имени - {} с id - {}!", updatedUser.getName(), updatedUser.getId());
        return UserMapper.userToDto(storage.update(updatedUser));
    }

    @Override
    public UserDto getById(long id) {
        User user = storage.getById(id);
        if (user == null) {
            throw new ModelNotFoundException(
                    String.format("Пользователь с id - %d не найден!",
                            id)
            );
        }
        log.debug("Получен пользователь с id - {}!", id);
        return UserMapper.userToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Получен список пользователей!");
        return UserMapper.listToDtoList(storage.getAllUsers());
    }

    @Override
    public void deleteById(long id) {
        log.debug("Удален пользователь с id - {}!", id);
        storage.deleteById(id);
    }

    private User updateUserFields(User user, UserDto dto) {
        if (dto.getName() != null && dto.getName() != user.getName()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && dto.getEmail() != user.getEmail()) {
            user.setEmail(dto.getEmail());
        }
        return user;
    }

    private void checkEmailExistException(String email) {
        if (storage.getAllUsers().stream().anyMatch(a -> a.getEmail().equals(email)))
            throw new EmailAlreadyExistException(
                    String.format("Электронная почта %s уже зарегистрирована!", email)
            );
    }
}