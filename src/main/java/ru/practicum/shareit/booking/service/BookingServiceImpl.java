package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidBookingException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
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
    public List<BookingDto> findByBooker(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new ModelNotFoundException(
                String.format("Пользователь с id %d не найден!", userId)));

        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            default:
                throw new InvalidBookingException("Unknown state: " + state);

        }
        return BookingMapper.listToDtoList(bookings);
    }

    @Override
    public List<BookingDto> findByOwner(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new ModelNotFoundException(
                String.format("Пользователь с id %d не найден!", userId)));

        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            default:
                throw new InvalidBookingException("Unknown state: " + state);

        }
        return BookingMapper.listToDtoList(bookings);
    }
}
