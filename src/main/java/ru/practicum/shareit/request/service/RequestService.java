package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {
    List<ItemRequestDto> findAllByRequester(Long userId);

    List<ItemRequestDto> findAll(PageRequest pageRequest, Long userId);

    ItemRequestDto findById(Long id, Long userId);

    ItemRequestDto save(Long userId, ItemRequest itemRequest);

}
