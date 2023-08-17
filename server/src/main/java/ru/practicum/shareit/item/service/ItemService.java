package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {

    ItemDto save(long userId, ItemDto dto);

    ItemDto update(long userId, long id, ItemDto dto);

    ItemDtoResponse getById(long id, long userId);

    List<ItemDtoResponse> getByUserId(long userId, int from, int size);

    List<ItemDto> search(String query, int from, int size);

    CommentDto saveComment(Long itemId, Long userId, CommentRequest commentRequest);
}
