package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.util.HeaderConstant;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> findAllByRequester(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId) {
        log.info("Запрошен список запросов пользователя с id -{}", userId);
        return requestClient.findAllByRequester(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> findAll(
            @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Запрошен список всех запросов со страницы - {} размером - {}", from, size);
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Object> findById(@PathVariable(value = "id") Long id,
                                           @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId) {
        log.info("Запрошены сведения о запросе с id - {}", id);
        return requestClient.findById(userId, id);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                       @RequestBody @Valid RequestDto requestDto) {
        log.info("Запрос на сохранение запроса от пользователя с id - {}", userId);
        return requestClient.save(userId, requestDto);
    }
}
