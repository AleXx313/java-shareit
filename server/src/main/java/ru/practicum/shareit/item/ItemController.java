package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.HeaderConstant;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> save(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                        @RequestBody ItemDto dto) {
        return new ResponseEntity<>(service.save(userId, dto), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<ItemDto> update(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                          @PathVariable Long id,
                                          @RequestBody ItemDto dto) {
        return new ResponseEntity<>(service.update(userId, id, dto), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemDtoResponse> getById(@PathVariable(value = "id") Long id,
                                                   @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId) {
        return new ResponseEntity<>(service.getById(id, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDtoResponse>> getListByUserId(
            @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(service.getByUserId(userId, from, size), HttpStatus.OK);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam(value = "text") String text,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(service.search(text, from, size), HttpStatus.OK);
    }

    @PostMapping(path = "/{id}/comment")
    public ResponseEntity<CommentDto> saveComment(@PathVariable(value = "id") Long itemId,
                                                  @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                                  @RequestBody CommentRequest commentRequest) {
        return new ResponseEntity<>(service.saveComment(itemId, userId, commentRequest), HttpStatus.OK);
    }
}
