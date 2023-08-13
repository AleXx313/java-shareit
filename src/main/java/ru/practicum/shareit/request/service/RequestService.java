package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    List<ItemRequestDto> findAllByRequester(Long userId);

    List<ItemRequestDto> findAll(int from, int size, Long userId);

    ItemRequestDto findById(Long id, Long userId);

    ItemRequestDto save(Long userId, ItemRequestDto itemRequestDto);

}
