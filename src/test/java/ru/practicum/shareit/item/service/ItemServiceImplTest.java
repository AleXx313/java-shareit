package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    void save_whenUserNotExist_thenThrowModelNotFound() {
        long userId = 1L;
        ItemDto itemDto = ItemDto.builder().id(1L).name("Item").description("some item").available(true).build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.save(userId, itemDto)).
                isInstanceOf(ModelNotFoundException.class).hasMessage("Пользователь с id - 1 не найден!");

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(itemRepository);

    }

    @Test
    void save_whenUserExist_thenSaveItemAndReturnDto(){
        long userId = 1L;
        User user = new User(1L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("Item").description("some item").available(true).owner(user).build();
        ItemDto itemDto = ItemDto.builder().id(1L).name("Item").description("some item").available(true).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto savedItem = itemService.save(userId, itemDto);

        assertThat(savedItem.getName()).isEqualTo("Item");
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();
        assertThat(capturedItem.getOwner()).isEqualTo(user);
    }

    @Test
    void save_whenItemDtoHasRequestId_thenItemShouldBeSavedWithRequestId(){
        long userId = 1L;
        User user = new User(1L, "User", "user@mail.ru");
        User requester = new User(2L, "Requester", "requester@mail.ru");
        ItemRequest itemRequest = ItemRequest.builder().id(1L)
                .requester(requester)
                .description("some request")
                .created(LocalDateTime.now())
                .build();
        Item item = Item.builder().id(1L).name("Item")
                .request(itemRequest)
                .description("some item")
                .available(true)
                .owner(user)
                .build();
        ItemDto itemDto = ItemDto.builder().id(1L)
                .name("Item")
                .requestId(1L)
                .description("some item")
                .available(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto savedItem = itemService.save(userId, itemDto);

        assertThat(savedItem.getRequestId()).isEqualTo(1L);

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item capturedItem = itemArgumentCaptor.getValue();
        assertThat(capturedItem.getRequest()).isEqualTo(itemRequest);
    }
    @Test
    void save_whenItemDtoHasRequestIdAndRequestDoesNotExist_thenThrowModelNotFoundException(){
        long userId = 1L;
        User user = new User(1L, "User", "user@mail.ru");
        ItemDto itemDto = ItemDto.builder().id(1L)
                .name("Item")
                .requestId(1L)
                .description("some item")
                .available(true)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(()->itemService.save(userId, itemDto))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Запрос с id - 1 не найден!");
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(1L);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void update_whenItemNotExist_thenThrowModeNotFound() {
        long userId = 1L;
        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder().id(1L).name("Item").build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.update(userId, itemId, itemDto)).
                isInstanceOf(ModelNotFoundException.class).hasMessage("Предмет - Item с id - 1 отсутствует!");

        verify(itemRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void update_whenUserNotExist_thenThrowUserHaveNotAccessException() {
        long userId = 2L;
        long itemId = 1L;
        User user = new User(1L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        ItemDto itemDto = ItemDto.builder().id(1L).name("Item").build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.update(userId, itemId, itemDto))
                .isInstanceOf(UserHaveNotAccessException.class)
                .hasMessage("Пользователь с id - 2 не имеет право изменять предмет - Item с id - 1!");

        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void update_whenUpdateIsValid_thenUpdateItem(){
        long userId = 1L;
        long itemId = 1L;
        User user = new User(1L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("NOT Item AT ALL")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        ItemDto itemDto = ItemDto.builder().id(1L).name("Item").description("some item").available(true).build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto updatedItem = itemService.update(userId, itemId, itemDto);

        assertThat(updatedItem.getName()).isEqualTo("Item");

    }

    @Test
    void getById_whenUserIsOwnerAndItemExistAndCommentsIsPresent_thenReturnFullDtoResponse() {
        long userId = 1L;
        long itemId = 1L;
        BookingDtoShort nextBooking = new BookingDtoShort(1L, 3L);
        BookingDtoShort lastBooking = new BookingDtoShort(2L, 4L);
        User user = new User(1L, "User", "user@mail.ru");
        User user3 = new User(3L, "User3", "user3@mail.ru");
        User user4 = new User(4L, "User4", "user4@mail.ru");
        Item item = Item.builder().id(1L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1L, "comment1", item, user3, LocalDateTime.now()));
        comments.add(new Comment(2L, "comment2", item, user3, LocalDateTime.now().plusDays(1L)));
        comments.add(new Comment(3L, "comment3", item, user4, LocalDateTime.now().plusDays(2L)));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findTopByItemIdAndStatusAndStartIsAfterOrderByStart(
                eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(nextBooking);
        when(bookingRepository.findTopByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(
                eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(lastBooking);
        when(commentRepository.findByItemId(itemId)).thenReturn(comments);

        ItemDtoResponse response = itemService.getById(itemId, userId);

        assertThat(response.getComments().size()).isEqualTo(3);
        assertThat(response.getLastBooking()).isEqualTo(lastBooking);
        assertThat(response.getLastBooking().getBookerId()).isEqualTo(4L);
        assertThat(response.getNextBooking()).isEqualTo(nextBooking);
        assertThat(response.getName()).isEqualTo("Item");
    }
    @Test
    void getById_whenUserIsNotOwnerAndItemExistAndHasNoComments_thenReturnNotFullDtoResponse() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(2L, "User", "user@mail.ru");
        Item item = Item.builder().id(itemId).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(Collections.emptyList());

        ItemDtoResponse response = itemService.getById(itemId, userId);

        assertThat(response.getComments().isEmpty()).isTrue();
        assertThat(response.getLastBooking()).isNull();
        assertThat(response.getNextBooking()).isNull();
    }


    @Test
    void getById_whenItemNotExist_thenThrowModelNotFound(){
        long userId = 1L;
        long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getById(userId, itemId)).
                isInstanceOf(ModelNotFoundException.class).hasMessage("Предмет с id - 1 отсутствует!");

        verify(itemRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(itemRepository);

    }

    @Test
    void getByUserId_whenUserHasItems_thenReturnListOfItemDtoResponse() {
        long userId = 1L;
        User user = new User(2L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        Item item2 = Item.builder().id(2L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        Item item3 = Item.builder().id(3L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);
        items.add(item3);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item3));
        when(commentRepository.findByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(itemRepository.findAllByOwnerIdOrderById(PageRequest.of(0,10,
                Sort.by(Sort.Direction.ASC, "id")), userId)).thenReturn(items);

        List<ItemDtoResponse> responses = itemService.getByUserId(userId, 0, 10);

        assertThat(responses.size()).isEqualTo(3);
    }

    @Test
    void getByUserId_whenUserHasNoItems_thenReturnEmptyList() {
        long userId = 1L;
        User user = new User(2L, "User", "user@mail.ru");
        when(itemRepository.findAllByOwnerIdOrderById(PageRequest.of(0,10,
                Sort.by(Sort.Direction.ASC, "id")), userId)).thenReturn(Collections.emptyList());

        List<ItemDtoResponse> responses = itemService.getByUserId(userId, 0, 10);

        assertThat(responses.size()).isEqualTo(0);
    }

    @Test
    void search_whenQueryIsBlank_thenReturnEmptyList() {
        String query = "";
        int from = 0;
        int size = 10;

        List<ItemDto> items = itemService.search(query, from, size);;

        assertThat(items.isEmpty()).isTrue();
    }
    @Test
    void search_whenQueryIsNotBlank_thenReturnListOfItemsSatisfyingQuery() {
        String query = "item";
        int from = 0;
        int size = 10;
        User user = new User(2L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        Item item2 = Item.builder().id(2L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        List<Item> items = List.of(item, item2);

        when(itemRepository.search(PageRequest.of(from/size,size), query)).thenReturn(items);

        List<ItemDto> itemsDto = itemService.search(query, from, size);

        assertThat(itemsDto.size()).isEqualTo(2);
    }
    @Test
    void search_whenQueryIsNotBlankAndNoSatisfyingItemsFound_thenReturnEmptyList() {
        String query = "item";
        int from = 0;
        int size = 10;


        when(itemRepository.search(PageRequest.of(from/size,size), query)).thenReturn(Collections.emptyList());

        List<ItemDto> itemsDto = itemService.search(query, from, size);

        assertThat(itemsDto.isEmpty()).isTrue();
    }

    @Test
    void saveComment_whenItemDoesNotExist_thenThrowModelNotFoundException() {
        long itemId = 1L;
        long userId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("description");

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(()->itemService.saveComment(itemId, userId, commentRequest))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Предмет с id - 1 не найден!");
    }

    @Test
    void saveComment_whenUserDoesNotExist_thenThrowModelNotFoundException() {
        long itemId = 1L;
        long userId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("comment");
        User user = new User(2L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(()->itemService.saveComment(itemId, userId, commentRequest))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Пользователь с id - 1 не найден!");
    }

    @Test
    void saveComment_whenUserNeverBookedItem_thenThrowInvalidBookingException() {
        long itemId = 1L;
        long userId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("comment");
        User user = new User(2L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("Item")
                .owner(user)
                .description("some item")
                .available(true)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findTopByItemIdAndBookerIdAndStatusAndEndIsBefore(eq(itemId), eq(userId),
                eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        assertThatThrownBy(()->itemService.saveComment(itemId, userId, commentRequest))
                .isInstanceOf(InvalidBookingException.class)
                .hasMessage("Пользователь с id - 1 предмет с id - 1 ранее не бронировал!");
    }

    @Test
    void saveComment_whenExceptionNotThrown_thenSaveCommentAndReturnCommentDto() {
        long itemId = 1L;
        long userId = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setText("comment");

        User commenter = new User(userId, "Commenter", "Commenter@mail.ru");
        User owner = new User(2L, "User", "user@mail.ru");
        Item item = Item.builder().id(1L).name("Item")
                .owner(owner)
                .description("some item")
                .available(true)
                .build();
        Comment comment = Comment.builder()
                .author(commenter)
                .text("comment")
                .item(item)
                .id(1L)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(commenter));
        when(bookingRepository.findTopByItemIdAndBookerIdAndStatusAndEndIsBefore(eq(itemId), eq(userId),
                eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto savedComment = itemService.saveComment(itemId,userId, commentRequest);

        assertThat(savedComment.getAuthorName()).isEqualTo("Commenter");
    }
}