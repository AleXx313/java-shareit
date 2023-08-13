package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto save(long userId, ItemDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ModelNotFoundException(
                String.format("Пользователь с id - %d не найден!", userId)));
        Item itemToSave = ItemMapper.dtoToItem(dto);
        itemToSave.setOwner(user);
        if(dto.getRequestId() != null){
            ItemRequest itemRequest = requestRepository.findById(dto.getRequestId()).orElseThrow(
                    () -> new ModelNotFoundException(String.format("Запрос с id - %d не найден!", dto.getRequestId())));
            itemToSave.setRequest(itemRequest);
        }
        Item item = itemRepository.save(itemToSave);
        log.info("Предмет - {} с id - {} добавлен!", item.getName(), item.getId());
        return ItemMapper.itemToDto(item);
    }

    @Override
    @Transactional
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
        if (item.getOwner().getId() != userId) {
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
    @Transactional(readOnly = true)
    public ItemDtoResponse getById(long id, long userId) {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            throw new ModelNotFoundException(
                    String.format("Предмет с id - %d отсутствует!",
                            id)
            );
        }
        Item item = itemOpt.get();

        BookingDtoShort nextBooking = null;
        BookingDtoShort lastBooking = null;
        if (item.getOwner().getId() == userId) {
            nextBooking = bookingRepository.findTopByItemIdAndStatusAndStartIsAfterOrderByStart(id,
                    BookingStatus.APPROVED, LocalDateTime.now());
            lastBooking = bookingRepository.findTopByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(id,
                    BookingStatus.APPROVED, LocalDateTime.now());
        }

        List<Comment> comments = commentRepository.findByItemId(id);

        log.info("Предмет - {} с id - {} запрошен!", item.getName(), item.getId());

        return ItemMapper.itemToResponse(item, nextBooking, lastBooking, CommentMapper.listToDtoList(comments));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getByUserId(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(pageRequest, userId);
        if (items.isEmpty()) {
            log.info("Список предметов пользователя с id {} пуст!", userId);
            return Collections.emptyList();
        }
        log.info("Получен список предметов пользователя с id {}!", userId);
        return items.stream().map(i -> getById(i.getId(), userId)).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String query, int from, int size) {
        if (query.isBlank()) {
            log.info("Пустой параметр запроса!");
            return Collections.emptyList();
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.search(pageRequest, query);
        if (items.isEmpty()) {
            log.info("По заданному порядку букв - {} предметов не найдено!", query);
            return Collections.emptyList();
        }
        log.info("Получен список предметов соответсвующий поиску по заданному порядку букв - {}!", query);
        return ItemMapper.listToDtoList(items);
    }

    @Override
    @Transactional
    public CommentDto saveComment(Long itemId, Long userId, CommentRequest commentRequest) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ModelNotFoundException(
                String.format("Предмет с id - %d не найден!", itemId)));

        User user = userRepository.findById(userId).orElseThrow(() -> new ModelNotFoundException(
                String.format("Пользователь с id - %d не найден!", userId)));

        bookingRepository.findTopByItemIdAndBookerIdAndStatusAndEndIsBefore(
                        itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new InvalidBookingException(
                        String.format("Пользователь с id - %d предмет с id - %d ранее не бронировал!",
                                userId, itemId)));

        Comment comment = CommentMapper.requestToComment(item, user, commentRequest.getText());

        Comment savedComment = commentRepository.save(comment);


        return CommentMapper.commentToDto(savedComment);
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

    private void isValid(Item item) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Переданы некорректные данные для обновления!");
        }
    }
}
