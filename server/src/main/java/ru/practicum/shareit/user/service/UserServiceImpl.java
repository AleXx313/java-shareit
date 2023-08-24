package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ModelNotFoundException;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto save(UserDto dto) {
        User savedUser = userRepository.save(UserMapper.dtoToUser(dto));
        log.info("Создан пользователь по имени - {} с id - {}!", savedUser.getName(), savedUser.getId());
        return UserMapper.userToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ModelNotFoundException(
                    String.format("Пользователь с id - %d не найден!",
                            dto.getId())
            );
        }
        User user = userOpt.get();

        User updatedUser = userRepository.save(updateUserFields(user, dto));
        log.info("Обновлен пользователь по имени - {} с id - {}!", updatedUser.getName(), updatedUser.getId());
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ModelNotFoundException(
                    String.format("Пользователь с id - %d не найден!",
                            id)
            );
        }
        log.info("Получен пользователь с id - {}!", id);
        return UserMapper.userToDto(userOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("Получен список пользователей!");
        return UserMapper.listToDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        log.info("Удален пользователь с id - {}!", id);
        userRepository.deleteById(id);
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
}
