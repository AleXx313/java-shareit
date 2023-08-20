package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.util.Marker;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> save(@RequestBody @Valid UserRequestDto requestDto) {
        return userClient.save(requestDto);
    }

    @PatchMapping(path = "/{id}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Object> update(@PathVariable(value = "id") Long id,
                                         @RequestBody @Valid UserRequestDto requestDto) {
        return userClient.update(id, requestDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getById(@PathVariable(value = "id") Long id) {
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getById() {
        return userClient.getAll();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable(value = "id") Long id) {
        return userClient.delete(id);
    }

}
