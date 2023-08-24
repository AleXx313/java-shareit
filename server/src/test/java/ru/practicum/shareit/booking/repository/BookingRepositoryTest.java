package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private TestEntityManager em;

    private User owner;
    private User booker;
    private Item item;


    private PageRequest pageRequest = PageRequest.of(0, 10);

    private Booking makeBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        return Booking.builder()
                .end(end)
                .start(start)
                .booker(booker)
                .status(status)
                .item(item)
                .build();
    }

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@email.ru");
        em.persist(owner);

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email.ru");
        em.persist(booker);

        item = new Item();
        item.setName("hammer");
        item.setDescription("knock knock");
        item.setOwner(owner);
        item.setAvailable(true);
        em.persist(item);
    }

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void checkBeforeEach() {
        assertThat(owner.getId()).isNotNull().isGreaterThan(0L);
        assertThat(booker.getId()).isNotNull().isGreaterThan(0L);
        assertThat(item.getId()).isNotNull().isGreaterThan(0L);
    }

    @Test
    void testFindAllByBookerId() {
        long bookerId = booker.getId();
        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = LocalDateTime.now().plusHours(4);
        Booking booking1 = bookingRepository.save(makeBooking(start, end, BookingStatus.APPROVED));
        Booking booking2 = bookingRepository.save(makeBooking(
                start.plusHours(4),
                end.plusHours(4),
                BookingStatus.APPROVED));

        List<Booking> result = bookingRepository.findAllByBookerId(pageRequest, bookerId);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(booking1);
        assertThat(result.get(1)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        long bookerId = booker.getId();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingRepository.save(makeBooking(start.plusHours(1), end.plusHours(1), BookingStatus.APPROVED));
        bookingRepository.save(makeBooking(start.plusHours(4), end.plusHours(4), BookingStatus.APPROVED));

        List<Booking> result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                pageRequest,
                bookerId,
                LocalDateTime.now(),
                LocalDateTime.now());

        assertThat(result.isEmpty()).isTrue();

        bookingRepository.save(makeBooking(start.minusHours(1), end, BookingStatus.APPROVED));

        result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                pageRequest,
                bookerId,
                LocalDateTime.now(),
                LocalDateTime.now());

        assertThat(result.isEmpty()).isFalse();
    }

    @Test
    void testFindAllByBookerIdAndEndIsBefore() {
        long bookerId = booker.getId();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingRepository.save(makeBooking(start.plusHours(1), end.plusHours(1), BookingStatus.APPROVED));
        bookingRepository.save(makeBooking(start.plusHours(4), end.plusHours(4), BookingStatus.APPROVED));

        List<Booking> result = bookingRepository.findAllByBookerIdAndEndIsBefore(
                pageRequest,
                bookerId,
                LocalDateTime.now());

        assertThat(result.isEmpty()).isTrue();

        bookingRepository.save(makeBooking(start.minusHours(3), end.minusHours(3), BookingStatus.APPROVED));

        result = bookingRepository.findAllByBookerIdAndEndIsBefore(
                pageRequest,
                bookerId,
                LocalDateTime.now());

        assertThat(result.isEmpty()).isFalse();
    }

    @Test
    void testFindAllByBookerIdAndStartIsAfter() {
        long bookerId = booker.getId();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingRepository.save(makeBooking(start.minusHours(3), end.plusHours(1), BookingStatus.APPROVED));
        bookingRepository.save(makeBooking(start.minusHours(1), end.plusHours(4), BookingStatus.APPROVED));

        List<Booking> result = bookingRepository.findAllByBookerIdAndStartIsAfter(
                pageRequest,
                bookerId,
                LocalDateTime.now());

        assertThat(result.isEmpty()).isTrue();

        bookingRepository.save(makeBooking(start.plusHours(1), end.plusHours(5), BookingStatus.APPROVED));

        result = bookingRepository.findAllByBookerIdAndStartIsAfter(
                pageRequest,
                bookerId,
                LocalDateTime.now());

        assertThat(result.isEmpty()).isFalse();
    }

    @Test
    void testFindAllByItemOwnerIdAndStatus() {
        long bookerId = booker.getId();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        bookingRepository.save(makeBooking(start, end, BookingStatus.APPROVED));
        bookingRepository.save(makeBooking(start.plusHours(1), end.plusHours(1), BookingStatus.APPROVED));

        List<Booking> result = bookingRepository.findAllByBookerIdAndStatus(
                pageRequest,
                bookerId,
                BookingStatus.WAITING);

        assertThat(result.isEmpty()).isTrue();

        bookingRepository.save(makeBooking(start.plusHours(4), end.plusHours(4), BookingStatus.WAITING));

        result = bookingRepository.findAllByBookerIdAndStatus(
                pageRequest,
                bookerId,
                BookingStatus.WAITING);

        assertThat(result.isEmpty()).isFalse();
    }
}