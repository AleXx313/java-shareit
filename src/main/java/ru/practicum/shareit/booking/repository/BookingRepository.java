package ru.practicum.shareit.booking.repository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(PageRequest pageRequest, Long userId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(PageRequest pageRequest,
                                                                 Long userId,
                                                                 LocalDateTime now,
                                                                 LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndIsBefore(PageRequest pageRequest, Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfter(PageRequest pageRequest, Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatus(PageRequest pageRequest, Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerId(PageRequest pageRequest, Long userId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(PageRequest pageRequest,
                                                                         Long userId,
                                                                         LocalDateTime now,
                                                                         LocalDateTime now2);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(PageRequest pageRequest, Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(PageRequest pageRequest, Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatus(PageRequest pageRequest, Long userId, BookingStatus bookingStatus);


    BookingDtoShort findTopByItemIdAndStatusAndStartIsAfterOrderByStart(Long itemId, BookingStatus status,
                                                                        LocalDateTime now);

    BookingDtoShort findTopByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(Long itemId, BookingStatus status,
                                                                           LocalDateTime now);

    Optional<Booking> findTopByItemIdAndBookerIdAndStatusAndEndIsBefore(Long itemId,
                                                                        Long userId,
                                                                        BookingStatus status,
                                                                        LocalDateTime now);
}
