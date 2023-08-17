package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking requestDtoToBooking(BookingRequestDto dto, Item item, User user) {
        return Booking.builder()
                .item(item)
                .booker(user)
                .end(dto.getEnd())
                .start(dto.getStart())
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(ItemMapper.itemToShort(booking.getItem()))
                .booker(UserMapper.userToShort(booking.getBooker()))
                .end(booking.getEnd())
                .start(booking.getStart())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> listToDtoList(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::bookingToDto).collect(Collectors.toList());
    }
}
