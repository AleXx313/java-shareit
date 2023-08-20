package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.util.HeaderConstant;
import ru.practicum.shareit.util.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> saveItem(@RequestHeader(HeaderConstant.USER_ID_HEADER) long userId,
                                           @RequestBody @Valid ItemRequestDto requestDto) {
        return itemClient.saveItem(userId, requestDto);
    }

    @PatchMapping(path = "/{id}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Object> updateItem(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                             @PathVariable(value = "id") Long id,
                                             @RequestBody @Valid ItemRequestDto requestDto) {
        return itemClient.updateItem(userId, id, requestDto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                          @PathVariable(value = "id") Long id) {
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getItemByOwner(
            @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItemByOwner(userId, from, size);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> search(@RequestParam(value = "text") String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        if(text.isBlank()){
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        return itemClient.search(text, from, size);
    }

    @PostMapping(path = "/{id}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                              @PathVariable(value = "id") Long id,
                                              @RequestBody @Valid CommentRequestDto requestDto) {
        return itemClient.saveComment(userId, id, requestDto);
    }
}
