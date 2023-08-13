package ru.practicum.shareit.booking;

import  lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
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

    @GetMapping
    public List<BookingDto> findByBooker(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestParam(
                                                 value = "state",
                                                 required = false,
                                                 defaultValue = "ALL") BookingState state,
                                         @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                         @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return bookingService.findByBooker(userId, state, from, size);
    }

    @GetMapping(path = "/owner")
    public List<BookingDto> findByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                        @RequestParam(
                                                value = "state",
                                                required = false,
                                                defaultValue = "ALL") BookingState state,
                                        @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                        @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return bookingService.findByOwner(userId, state, from, size);
    }
}
