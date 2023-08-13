package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ItemRequestDto> findAllByRequester(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return requestService.findAllByRequester(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> findAll(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                        @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return requestService.findAll(PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created")),
                userId);
    }

    @GetMapping(path = "{id}")
    public ItemRequestDto findById(@PathVariable(value = "id") Long id,
                                   @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return requestService.findById(id, userId);
    }

    @PostMapping
    public ItemRequestDto save(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                               @RequestBody @Valid ItemRequest itemRequest) {
        return requestService.save(userId, itemRequest);
    }
}
