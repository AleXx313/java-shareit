package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.HeaderConstant;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> findAllByRequester(
            @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId) {
        return new ResponseEntity<>(requestService.findAllByRequester(userId), HttpStatus.OK);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<ItemRequestDto>> findAll(
            @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(requestService.findAll(from, size, userId), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<ItemRequestDto> findById(@PathVariable(value = "id") Long id,
                                                   @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId) {
        return new ResponseEntity<>(requestService.findById(id, userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> save(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                               @RequestBody ItemRequestDto itemRequestDto) {
        return new ResponseEntity<>(requestService.save(userId, itemRequestDto), HttpStatus.CREATED);
    }
}
