package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAllByRequester(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new ModelNotFoundException(String.format("Пользователь с id %d отсутствует", userId)));
        List<ItemRequest> requests = requestRepository.findByRequesterId(userId);
        List<ItemRequestDto> result = requests.stream()
                .map(ItemRequestMapper::requestToDto)
                .collect(Collectors.toList());
        result.forEach(a -> a.setItems(itemRepository.findByRequestId(a.getId())));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> findAll(PageRequest pageRequest, Long userId) {
        Page<ItemRequest> page = requestRepository.findAllByRequesterIdNot(pageRequest, userId);
        List<ItemRequestDto> requests = ItemRequestMapper.listToDtosList(page.getContent());
        requests.forEach(a -> a.setItems(itemRepository.findByRequestId(a.getId())));
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto findById(Long id, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new ModelNotFoundException(String.format("Пользователь с id %d отсутствует", userId)));
        ItemRequest itemRequest = requestRepository.findById(id).orElseThrow(
                () -> new ModelNotFoundException(String.format("Запрос с id %d отсутствует", id)));

        ItemRequestDto result = ItemRequestMapper.requestToDto(itemRequest);
        result.setItems(itemRepository.findByRequestId(result.getId()));
        return result;
    }

    @Override
    @Transactional
    public ItemRequestDto save(Long userId, ItemRequest itemRequest) {
        itemRequest.setRequester(userRepository.findById(userId).orElseThrow(
                () -> new ModelNotFoundException(String.format("Пользователь с id %d отсутствует", userId))));
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.requestToDto(requestRepository.save(itemRequest));
    }
}
