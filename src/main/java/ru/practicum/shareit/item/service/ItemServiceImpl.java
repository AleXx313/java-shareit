package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto save(long userId, ItemDto dto) {
        userService.getById(userId);
        dto.setUserId(userId);
        Item item = itemRepository.save(ItemMapper.dtoToItem(dto));
        log.info("Предмет - {} с id - {} добавлен!", item.getName(), item.getId());
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto update(long userId, long id, ItemDto dto) {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            throw new ModelNotFoundException(
                    String.format("Предмет - %s с id - %d отсутствует!",
                            dto.getName(),
                            dto.getId())
            );
        }
        Item item = itemOpt.get();
        if (item.getUserId() != userId) {
            throw new UserHaveNotAccessException(
                    String.format("Пользователь с id - %d не имеет право изменять предмет - %s с id - %d!",
                            userId,
                            dto.getName(),
                            dto.getId())
            );
        }
        Item updatedItem = itemRepository.save(updateItemFields(item, dto));
        log.info("Предмет - {} с id - {} обновлен!", updatedItem.getName(), updatedItem.getId());
        return ItemMapper.itemToDto(updatedItem);
    }

    @Override
    public ItemDto getById(long id) {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            throw new ModelNotFoundException(
                    String.format("Предмет с id - %d отсутствует!",
                            id)
            );
        }
        Item item = itemOpt.get();
        log.info("Предмет - {} с id - {} запрошен!", item.getName(), item.getId());
        return ItemMapper.itemToDto(item);
    }

    @Override
    public List<ItemDto> getByUserId(long userId) {
        List<Item> items = itemRepository.findByUserId(userId);
        if (items.isEmpty()) {
            log.info("Список предметов пользователя с id {} пуст!", userId);
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
        isValid(item);
        return item;
    }

    private boolean isValid(Item item){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (violations.isEmpty()){
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Переданы некорректные данные для обновления!");
        }
    }
}
