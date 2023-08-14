package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;


    private BookingRequestDto makeBookingRequestDto(long itemId) {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusHours(1));
        requestDto.setEnd(LocalDateTime.now().plusHours(3));
        requestDto.setItemId(itemId);
        return requestDto;
    }

    @Test
    void save_whenUserNotFoundInDataBase_thenThrowModelNotFound() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.save(1L, makeBookingRequestDto(1L)))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Пользователь с id - 1 не найден!");
    }

    @Test
    void save_whenItemNotFoundInDataBase_thenThrowModelNotFound() {
        long userId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");

        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.save(1L, requestDto))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Предмет с id - 1 не найден!");
    }

    @Test
    void save_whenItemIdEqualsBookerId_thenThrowModelNotFound() {
        long userId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(2L, "Booker", "booker@mail.ru");
        Item item = new Item(1L, booker, "hammer", "knock knock", true, null);

        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.save(1L, requestDto))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Невозможно забронировать свою вещь!");
    }

    @Test
    void save_whenItemIsNotAvailable_thenThrowInvalidBooking() {
        long userId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(2L, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", false, null);

        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.save(1L, requestDto))
                .isInstanceOf(InvalidBookingException.class)
                .hasMessage("Предмет hammer c id - 1 недоступен для бронирования!");
    }

    @Test
    void save_whenActionIsValid_thenInvokeSaveMethodAndReturnDto() {
        long userId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(2L, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", true, null);
        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(requestDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto dto = bookingService.save(userId, requestDto);

        assertThat(dto.getBooker().getName()).isEqualTo("Booker");
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void update_whenBookingNotFound_thenThrowModelNotFound() {
        long userId = 1L;
        long bookId = 1L;

        when(bookingRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.update(bookId, userId, true))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Бронирование с id - 1 не найдено!");
    }

    @Test
    void update_whenUserNotOwner_thenThrowModelNotFound() {
        long userId = 1L;
        long bookId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(2L, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", true, null);
        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        when(bookingRepository.findById(bookId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookId, userId, true))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Статус бронирования может менять только владелец вещи!");
    }

    @Test
    void update_whenBookingStatusIsApproved_thenThrowInvalidBooking() {
        long userId = 1L;
        long bookId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(2L, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", true, null);
        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(booker)
                .build();
        when(bookingRepository.findById(bookId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(bookId, owner.getId(), true))
                .isInstanceOf(InvalidBookingException.class)
                .hasMessage("Бронирование с id 1 уже подтверждено!");
    }

    @Test
    void update_whenTrue_thenSendToSaveApprovedBooking() {
        long ownerId = 2L;
        long bookId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(ownerId, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", true, null);
        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        when(bookingRepository.findById(bookId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.update(bookId, ownerId, true);

        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking result = bookingArgumentCaptor.getValue();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void update_whenFalse_thenSendToSaveRejectedBooking() {
        long ownerId = 2L;
        long bookId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(ownerId, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", true, null);
        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        when(bookingRepository.findById(bookId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        bookingService.update(bookId, ownerId, false);

        verify(bookingRepository, times(1)).save(bookingArgumentCaptor.capture());
        Booking result = bookingArgumentCaptor.getValue();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void findById_whenUserNotOwnerAndNotBooker_thenThrowModelNotFound() {
        long ownerId = 2L;
        long bookId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(ownerId, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", true, null);
        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        when(bookingRepository.findById(bookId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.findById(bookId, 3L))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Сведения о бронировании может получать только пользователь, " +
                        "оставивший бронь или владелец вещи!");
    }

    @Test
    void findById_whenBookingNotFound_thenThrowModelNotFound() {
        long userId = 1L;
        long bookId = 1L;

        when(bookingRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findById(bookId, userId))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Бронирование с id - 1 не найдено!");
    }

    @Test
    void findById_whenValid_thenReturnDto() {
        long ownerId = 2L;
        long bookId = 1L;
        User booker = new User(1L, "Booker", "booker@mail.ru");
        User owner = new User(ownerId, "Booker", "booker@mail.ru");
        Item item = new Item(1L, owner, "hammer", "knock knock", true, null);
        BookingRequestDto requestDto = makeBookingRequestDto(1L);
        Booking booking = Booking.builder()
                .id(1L)
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();
        when(bookingRepository.findById(bookId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findById(bookId, 2L);

        assertThat(result.getBooker().getName()).isEqualTo("Booker");
    }

    @Test
    void findByBooker_whenBookerNotFound_thenThrowModelNotFound() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findByBooker(userId, BookingState.ALL, 0, 10))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Пользователь с id 1 не найден!");
    }

    @Test
    void findByBooker_whenStateAll_thenInvokeFindAllByBookerId() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User booker = new User(userId, "Booker", "booker@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        bookingService.findByBooker(userId, BookingState.ALL, 0, 10);

        verify(bookingRepository, times(1)).findAllByBookerId(request, userId);

    }

    @Test
    void findByBooker_whenStateCurrent_thenInvokeFindAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User booker = new User(userId, "Booker", "booker@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        bookingService.findByBooker(userId, BookingState.CURRENT, 0, 10);

        verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                eq(request),
                eq(userId),
                any(),
                any());
    }

    @Test
    void findByBooker_whenStatePast_thenInvokeFindAllByBookerIdAndEndIsBefore() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User booker = new User(userId, "Booker", "booker@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        bookingService.findByBooker(userId, BookingState.PAST, 0, 10);

        verify(bookingRepository, times(1)).findAllByBookerIdAndEndIsBefore(
                eq(request),
                eq(userId),
                any());
    }

    @Test
    void findByBooker_whenStateFuture_thenInvokeFindAllByBookerIdAndStartIsAfter() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User booker = new User(userId, "Booker", "booker@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        bookingService.findByBooker(userId, BookingState.FUTURE, 0, 10);

        verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsAfter(
                eq(request),
                eq(userId),
                any());
    }

    @Test
    void findByBooker_whenStateRejected_thenInvokeFindAllByBookerIdAndStatus() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User booker = new User(userId, "Booker", "booker@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        bookingService.findByBooker(userId, BookingState.REJECTED, 0, 10);

        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(
                request,
                userId,
                BookingStatus.REJECTED);
    }

    @Test
    void findByBooker_whenStateWaiting_thenInvokeFindAllByBookerIdAndStatus() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User booker = new User(userId, "Booker", "booker@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        bookingService.findByBooker(userId, BookingState.WAITING, 0, 10);

        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(
                request,
                userId,
                BookingStatus.WAITING);
    }

    @Test
    void findByOwner_whenOwnerNotFound_thenThrowModelNotFound() {
        long ownerId = 1L;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findByOwner(ownerId, BookingState.ALL, 0, 10))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Пользователь с id 1 не найден!");
    }

    @Test
    void findByOwner_whenStateAll_thenInvokeFindAllByItemOwnerId() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User owner = new User(userId, "Owner", "owner@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        bookingService.findByOwner(userId, BookingState.ALL, 0, 10);

        verify(bookingRepository, times(1)).findAllByItemOwnerId(
                request,
                userId);
    }

    @Test
    void findByOwner_whenStateCurrent_thenInvokeFindAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User owner = new User(userId, "Owner", "owner@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        bookingService.findByOwner(userId, BookingState.CURRENT, 0, 10);

        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                eq(request),
                eq(userId),
                any(),
                any());
    }

    @Test
    void findByOwner_whenStatePast_thenInvokeFindAllByItemOwnerIdAndEndIsBefore() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User owner = new User(userId, "Owner", "owner@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        bookingService.findByOwner(userId, BookingState.PAST, 0, 10);

        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndIsBefore(
                eq(request),
                eq(userId),
                any());
    }

    @Test
    void findByOwner_whenStateFuture_thenInvokeFindAllByItemOwnerIdAndStartIsAfter() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User owner = new User(userId, "Owner", "owner@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        bookingService.findByOwner(userId, BookingState.FUTURE, 0, 10);

        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsAfter(
                eq(request),
                eq(userId),
                any());
    }

    @Test
    void findByOwner_whenStateRejected_thenInvokeFindAllByItemOwnerIdAndStatus() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User owner = new User(userId, "Owner", "owner@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        bookingService.findByOwner(userId, BookingState.REJECTED, 0, 10);

        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(
                request,
                userId,
                BookingStatus.REJECTED);
    }

    @Test
    void findByOwner_whenStateWaiting_thenInvokeFindAllByItemOwnerIdAndStatus() {
        long userId = 1L;
        int from = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        PageRequest request = PageRequest.of(from / size, size, sort);
        User owner = new User(userId, "Owner", "owner@mail.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));

        bookingService.findByOwner(userId, BookingState.WAITING, 0, 10);

        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(
                request,
                userId,
                BookingStatus.WAITING);
    }


}