package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto save(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                        @RequestBody @Valid ItemDto dto) {
        return service.save(userId, dto);
    }

    @PatchMapping(path = "/{id}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @RequestBody ItemDto dto) {
        return service.update(userId, id, dto);
    }

    @GetMapping(path = "/{id}")
    public ItemDtoResponse getById(@PathVariable(value = "id") Long id,
                                   @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return service.getById(id, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getListByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                 @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return service.getByUserId(userId, from, size);
    }

    @GetMapping(path = "/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text,
                                @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return service.search(text, from, size);
    }

    @PostMapping(path = "/{id}/comment")
    public CommentDto saveComment(@PathVariable(value = "id") Long itemId,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                  @RequestBody @Valid CommentRequest commentRequest) {
        return service.saveComment(itemId, userId, commentRequest);
    }

}
