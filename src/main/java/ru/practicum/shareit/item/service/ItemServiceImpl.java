package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final UserService userService;

    @Override
    public ItemDto save(long userId, ItemDto dto) {
        if (userService.getById(userId) == null) {
            throw new ModelNotFoundException(
                    String.format("Пользователь с id - %d не найден!", userId)
            );
        }
        dto.setUserId(userId);
        Item item = storage.save(ItemMapper.dtoToItem(dto));
        log.info("Предмет - {} с id - {} добавлен!", item.getName(), item.getId());
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto update(long userId, long id, ItemDto dto) {
        Item item = storage.getById(id);
        if (item == null) {
            throw new ModelNotFoundException(
                    String.format("Предмет - %s с id - %d отсутствует!",
                            dto.getName(),
                            dto.getId())
            );
        }
        if (item.getUserId() != userId) {
            throw new UserHaveNotAccessException(
                    String.format("Пользователь с id - %d не имеет право изменять предмет - %s с id - %d!",
                            userId,
                            dto.getName(),
                            dto.getId())
            );
        }
        Item updatedItem = updateItemFields(item, dto);
        log.info("Предмет - {} с id - {} обновлен!", updatedItem.getName(), updatedItem.getId());
        return ItemMapper.itemToDto(updatedItem);
    }

    @Override
    public ItemDto getById(long id) {
        Item item = storage.getById(id);
        if (item == null) {
            throw new ModelNotFoundException(
                    String.format("Предмет с id - %d отсутствует!",
                            id)
            );
        }
        log.info("Предмет - {} с id - {} запрошен!", item.getName(), item.getId());
        return ItemMapper.itemToDto(item);
    }

    @Override
    public List<ItemDto> getByUserId(long userId) {
        List<Item> items = storage.getByUserId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("Получен список предметов пользователя с id {}!", userId);
        return ItemMapper.listToDtoList(items);
    }

    @Override
    public List<ItemDto> search(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        }
        String lowQuery = query.toLowerCase();
        List<Item> items = storage.search(lowQuery);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("Получен список предметов соответсвующий поиску по заданному порядку букв - {}!", query);
        return ItemMapper.listToDtoList(items);
    }

    private Item updateItemFields(Item item, ItemDto dto) {
        if (dto.getName() != null && !dto.getName().equals(item.getName())) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().equals(item.getDescription())) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null && dto.getAvailable() != item.getAvailable()) {
            item.setAvailable(dto.getAvailable());
        }
        return item;
    }
}
