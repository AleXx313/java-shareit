package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid ItemRequestDto requestDto) {
        return itemClient.saveItem(userId, requestDto);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                             @PathVariable(value = "id") Long id,
                                             @RequestBody ItemRequestDto requestDto) {
        return itemClient.updateItem(userId, id, requestDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                          @PathVariable(value = "id") Long id) {
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemByOwner(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItemByOwner(userId, from, size);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text") String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.search(text, from, size);
    }

    @PostMapping(path = "/{id}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                              @PathVariable(value = "id") Long id,
                                              @RequestBody @Valid CommentRequestDto requestDto) {
        return itemClient.saveComment(userId, id, requestDto);
    }
}
