package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto save(Long userId, BookingRequestDto bookingRequestDto);

    BookingDto update(Long id, Long userId, boolean approved);

    BookingDto findById(Long id, Long userId);

    List<BookingDto> findByBooker(Long userId, String state);

    List<BookingDto> findByOwner(Long userId, String state);
}
