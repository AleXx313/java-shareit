package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto save(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.save(userId, bookingRequestDto);
    }

    @PatchMapping(path = "/{id}")
    public BookingDto updateStatus(@PathVariable(value = "id") Long id,
                                   @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                   @RequestParam(value = "approved") boolean approved) {
        return bookingService.update(id, userId, approved);
    }

    @GetMapping(path = "/{id}")
    public BookingDto findById(@PathVariable(value = "id") Long id,
                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.findById(id, userId);
    }

    //Поиск бронирований пользователя
    @GetMapping
    public List<BookingDto> findByBooker(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestParam(
                                                 value = "state",
                                                 required = false,
                                                 defaultValue = "ALL") String state) {
        return bookingService.findByBooker(userId, state);
    }

    //Поиск бронирований вещей пользователя (если они у него есть)
    @GetMapping(path = "/owner")
    public List<BookingDto> findByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestParam(
                                                value = "state",
                                                required = false,
                                                defaultValue = "ALL") String state) {
        return bookingService.findByOwner(userId, state);
    }
}
