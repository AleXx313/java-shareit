package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.HeaderConstant;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> save(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                           @RequestBody BookingRequestDto bookingRequestDto) {
        return new ResponseEntity<>(bookingService.save(userId, bookingRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<BookingDto> updateStatus(@PathVariable(value = "id") Long id,
                                                   @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                                   @RequestParam(value = "approved") boolean approved) {
        return new ResponseEntity<>(bookingService.update(id, userId, approved), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BookingDto> findById(@PathVariable(value = "id") Long id,
                                               @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId) {
        return new ResponseEntity<>(bookingService.findById(id, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> findByBooker(
            @RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(bookingService.findByBooker(userId, state, from, size), HttpStatus.OK);
    }

    @GetMapping(path = "/owner")
    public ResponseEntity<List<BookingDto>> findByOwner(@RequestHeader(HeaderConstant.USER_ID_HEADER) Long userId,
                                                        @RequestParam(
                                                                value = "state",
                                                                required = false,
                                                                defaultValue = "ALL") BookingState state,
                                                        @RequestParam(required = false, defaultValue = "0") int from,
                                                        @RequestParam(required = false, defaultValue = "10") int size) {
        return new ResponseEntity<>(bookingService.findByOwner(userId, state, from, size), HttpStatus.OK);
    }
}
