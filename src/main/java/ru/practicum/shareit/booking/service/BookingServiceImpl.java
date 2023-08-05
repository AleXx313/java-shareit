package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto save(Long userId, BookingRequestDto bookingRequestDto) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ModelNotFoundException(
                    String.format("Пользователь с id - %d не найден!",
                            userId)
            );
        }
        User booker = userOpt.get();

        Optional<Item> itemOpt = itemRepository.findById(bookingRequestDto.getItemId());
        if (itemOpt.isEmpty()) {
            throw new ModelNotFoundException(
                    String.format("Предмет с id - %d не найден!",
                            bookingRequestDto.getItemId())
            );
        }
        Item item = itemOpt.get();
        if (item.getOwner().getId().equals(userId)) {
            throw new ModelNotFoundException("Невозможно забронировать свою вещь!");
        }
        if (!item.getAvailable()) {
            throw new InvalidBookingException(
                    String.format("Предмет %s c id - %d недоступен для бронирования!",
                            item.getName(), item.getId()));
        }

        Booking booking = BookingMapper.requestDtoToBooking(bookingRequestDto, item, booker);
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(Long id, Long userId, boolean approved) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ModelNotFoundException(
                String.format("Бронирование> с id - %d не найдено!",
                        id)));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ModelNotFoundException("Статус бронирования может менять только владелец вещи!");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new InvalidBookingException(String.format("Бронирование с id %d уже подтверждено!", id));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.bookingToDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findById(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ModelNotFoundException(
                String.format("Бронирование> с id - %d не найдено!",
                        id)));
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.bookingToDto(booking);
        } else {
            throw new ModelNotFoundException("Сведения о бронировании может получать только пользователь, " +
                    "оставивший бронь или владелец вещи!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findByBooker(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new ModelNotFoundException(
                String.format("Пользователь с id %d не найден!", userId)));

        PageRequest request = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(
                        request,
                        userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        request,
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(
                        request,
                        userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(
                        request,
                        userId,
                        LocalDateTime.now());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        request,
                        userId,
                        BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        request,
                        userId,
                        BookingStatus.WAITING);
                break;
            default:
                throw new InvalidBookingException("Unknown state: " + state);
        }
        return BookingMapper.listToDtoList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findByOwner(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new ModelNotFoundException(
                String.format("Пользователь с id %d не найден!", userId)));
        PageRequest request = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(request, userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        request,
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(
                        request,
                        userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                        request,
                        userId,
                        LocalDateTime.now());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                        request,
                        userId,
                        BookingStatus.REJECTED);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                        request,
                        userId,
                        BookingStatus.WAITING);
                break;
            default:
                throw new InvalidBookingException("Unknown state: " + state);

        }
        return BookingMapper.listToDtoList(bookings);
    }
}
