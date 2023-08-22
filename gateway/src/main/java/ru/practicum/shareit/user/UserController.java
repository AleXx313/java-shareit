package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.util.Marker;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Validated({Marker.OnCreate.class}) UserRequestDto requestDto) {
        log.info("Сохранение пользователя - {}", requestDto);
        return userClient.save(requestDto);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> update(@PathVariable(value = "id") Long id,
                                         @RequestBody @Validated({Marker.OnUpdate.class}) UserRequestDto requestDto) {
        log.info("Обновление пользователя с id - {}", id);
        return userClient.update(id, requestDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getById(@PathVariable(value = "id") Long id) {
        log.info("Запрос сведения о пользователе с id - {}", id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getById() {
        log.info("Запрос сведения обо всех пользователях");
        return userClient.getAll();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable(value = "id") Long id) {
        log.info("Удаление пользователя с id - {}", id);
        return userClient.delete(id);
    }
}
