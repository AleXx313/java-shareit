package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto save(long userId, ItemDto dto);

    ItemDto update(long userId, long id, ItemDto dto);

    ItemDto getById(long id);

    List<ItemDto> getByUserId(long userId);

    List<ItemDto> search(String query);
}
