package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto save(@RequestBody @Valid UserDto dto) {
        return service.save(dto);
    }

    @PatchMapping(path = "/{id}")
    public UserDto update(@PathVariable(value = "id") Long id,
                          @RequestBody UserDto dto) {
        return service.update(id, dto);
    }

    @GetMapping(path = "/{id}")
    public UserDto getById(@PathVariable(value = "id") Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAllUsers();
    }

    @DeleteMapping(path = "/{id}")
    public void deleteById(@PathVariable(value = "id") Long id) {
        service.deleteById(id);
    }

}
