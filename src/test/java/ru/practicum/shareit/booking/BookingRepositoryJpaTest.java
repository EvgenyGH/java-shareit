package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryJpaTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User userFirst;
    private User userSecond;
    private User userThird;
    private Item item;
    private Booking bookingFirst;
    private Booking bookingSecond;
    private Booking bookingThird;

    @BeforeEach
    void initialization() {
        userFirst = new User(125L, "name 125", "email@125.com");
        userSecond = new User(135L, "name 135", "email@135.com");
        userThird = new User(145L, "name 145", "email@145.com");
        userFirst = userRepository.save(userFirst);
        userSecond = userRepository.save(userSecond);

        item = new Item(105L, "name 105", "description 105"
                , true, userFirst, null);
        item = itemRepository.save(item);

        bookingFirst = new Booking(115L, LocalDateTime.now().minusDays(15)
                , LocalDateTime.now().minusDays(12), item, userSecond, Status.APPROVED);
        bookingSecond = new Booking(125L, LocalDateTime.now().plusDays(15)
                , LocalDateTime.now().plusDays(20), item, userSecond, Status.APPROVED);
        bookingThird = new Booking(130L, LocalDateTime.now().minusDays(1)
                , LocalDateTime.now().plusDays(1), item, userSecond, Status.APPROVED);
        bookingFirst = bookingRepository.save(bookingFirst);
        bookingSecond = bookingRepository.save(bookingSecond);
        bookingThird = bookingRepository.save(bookingThird);
    }

    @Test
    void getOwnerOrBookerBookingTest() {
        Booking bookingTest = bookingRepository
                .getOwnerOrBookerBooking(bookingFirst.getId(), userFirst.getId()).orElse(null);

        assertThat(bookingTest).isEqualTo(bookingFirst);
    }

    @Test
    void getOwnerOrBookerBookingWrongUserTest() {
        Booking bookingTest = bookingRepository
                .getOwnerOrBookerBooking(bookingFirst.getId(), userThird.getId()).orElse(null);

        assertThat(bookingTest).isEqualTo(null);
    }

    @Test
    void getBookingByBookerStatusAllOrderedTest() {
        List<Booking> bookingsTest = bookingRepository
                .getBookingByBookerStatusAllOrdered(userSecond.getId(), PageRequest.of(0, 10));

        List<Booking> bookings = new ArrayList<>();
        Collections.addAll(bookings, bookingFirst, bookingSecond, bookingThird);
        bookings.sort(Comparator.comparing(Booking::getStartDate).reversed());

        assertThat(bookingsTest).isEqualTo(bookings);
    }

    @Test
    void getBookingByBookerStatusOrderedStatusApprovedTest() {
        bookingSecond.setStatus(Status.WAITING);
        bookingRepository.save(bookingSecond);
        List<Booking> bookingsTest = bookingRepository
                .getBookingByBookerStatusOrdered(userSecond.getId(), Status.APPROVED, PageRequest.of(0, 10));

        List<Booking> bookings = new ArrayList<>();
        Collections.addAll(bookings, bookingFirst, bookingThird);
        bookings.sort(Comparator.comparing(Booking::getStartDate).reversed());

        assertThat(bookingsTest).isEqualTo(bookings);
    }

    @Test
    void getBookingByBookerStatusOrderedStatusRejectedTest() {
        bookingSecond.setStatus(Status.REJECTED);
        bookingFirst.setStatus(Status.REJECTED);
        bookingRepository.save(bookingFirst);
        bookingRepository.save(bookingSecond);
        List<Booking> bookingsTest = bookingRepository
                .getBookingByBookerStatusOrdered(userSecond.getId(), Status.REJECTED, PageRequest.of(0, 10));

        List<Booking> bookings = new ArrayList<>();
        Collections.addAll(bookings, bookingFirst, bookingSecond);
        bookings.sort(Comparator.comparing(Booking::getStartDate).reversed());

        assertThat(bookingsTest).isEqualTo(bookings);
    }

    @Test
    void getBookingByBookerStatusPastOrderedTest() {
        List<Booking> bookingsTest = bookingRepository
                .getBookingByBookerStatusPastOrdered(userSecond.getId(), LocalDateTime.now()
                        , PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(List.of(bookingFirst));
    }

    @Test
    void getBookingByBookerStatusFutureOrderedTest() {
        List<Booking> bookingsTest = bookingRepository
                .getBookingByBookerStatusFutureOrdered(userSecond.getId(), LocalDateTime.now()
                        , PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(List.of(bookingSecond));
    }

    @Test
    void getBookingByBookerStatusCurrentOrderedTest() {
        List<Booking> bookingsTest = bookingRepository
                .getBookingByBookerStatusCurrentOrdered(userSecond.getId(), LocalDateTime.now()
                        , PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(List.of(bookingThird));
    }

    @Test
    void getBookingByBookerStatusCurrentOrderedWrongBookerTest() {
        List<Booking> bookingsTest = bookingRepository
                .getBookingByBookerStatusCurrentOrdered(userFirst.getId(), LocalDateTime.now()
                        , PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(Collections.emptyList());
    }

    @Test
    void getBookingByOwnerStatusAllOrderedWrongOwnerTest() {
        List<Booking> bookingsTest = bookingRepository
                .getBookingByOwnerStatusAllOrdered(userSecond.getId(), PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(Collections.emptyList());
    }

    @Test
    void getBookingByOwnerStatusAllOrderedTest() {
        List<Booking> bookingsTest = bookingRepository
                .getBookingByOwnerStatusAllOrdered(userFirst.getId(), PageRequest.of(0, 10));

        List<Booking> bookings = new ArrayList<>();
        Collections.addAll(bookings, bookingFirst, bookingSecond, bookingThird);
        bookings.sort(Comparator.comparing(Booking::getStartDate).reversed());

        assertThat(bookingsTest).isEqualTo(bookings);
    }

    @Test
    void getBookingByOwnerStatusOrderedTest() {
        bookingSecond.setStatus(Status.CANCELED);
        bookingThird.setStatus(Status.CANCELED);
        bookingRepository.save(bookingSecond);
        bookingRepository.save(bookingThird);
        List<Booking> bookingsTest = bookingRepository
                .getBookingByOwnerStatusOrdered(userFirst.getId(), Status.CANCELED, PageRequest.of(0, 10));

        List<Booking> bookings = new ArrayList<>();
        Collections.addAll(bookings, bookingSecond, bookingThird);
        bookings.sort(Comparator.comparing(Booking::getStartDate).reversed());

        assertThat(bookingsTest).isEqualTo(bookings);
    }

    @Test
    void getBookingByOwnerStatusPastOrderedTest() {
        List<Booking> bookingsTest = bookingRepository.getBookingByOwnerStatusPastOrdered(userFirst.getId()
                , LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(List.of(bookingFirst));
    }

    @Test
    void getBookingByOwnerStatusFutureOrderedTest() {
        List<Booking> bookingsTest = bookingRepository.getBookingByOwnerStatusFutureOrdered(userFirst.getId()
                , LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(List.of(bookingSecond));
    }

    @Test
    void getBookingByOwnerStatusCurrentOrderedTest() {
        List<Booking> bookingsTest = bookingRepository.getBookingByOwnerStatusCurrentOrdered(userFirst.getId()
                , LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(List.of(bookingThird));
    }


    @Test
    void getLastItemBookingOrderedTest() {
        List<Booking> bookingsTest = bookingRepository.getLastItemBookingOrdered(item.getId()
                , LocalDateTime.now(), PageRequest.of(0, 10));

        List<Booking> bookings = new ArrayList<>();
        Collections.addAll(bookings, bookingFirst, bookingThird);
        bookings.sort(Comparator.comparing(Booking::getStartDate).reversed());

        assertThat(bookingsTest).isEqualTo(bookings);
    }

    @Test
    void getNextItemBookingOrderedTest() {
        List<Booking> bookingsTest = bookingRepository.getNextItemBookingOrdered(item.getId()
                , LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookingsTest).isEqualTo(List.of(bookingSecond));
    }

    @Test
    void getBookingByItemBookerTest() {
        List<Booking> bookingsTest = bookingRepository.getBookingByItemBooker(item.getId()
                , userSecond.getId());

        assertThat(bookingsTest).containsOnly(bookingFirst, bookingThird, bookingSecond);
    }

    @Test
    void getBookingByItemBookerWithNoBookingsTest() {
        List<Booking> bookingsTest = bookingRepository.getBookingByItemBooker(item.getId()
                , userFirst.getId());

        assertThat(bookingsTest).isEmpty();
    }

    @Test
    void getFinishedBookingByBookerItemStatusTest() {
        Booking bookingTest = bookingRepository.getFinishedBookingByBookerItemStatus(userSecond.getId()
                ,item.getId(), Status.APPROVED, LocalDateTime.now()).orElse(null);

        assertThat(bookingTest).isEqualTo(bookingFirst);
    }

    @Test
    void getFinishedBookingByBookerItemStatusNoBookingsTest() {
        Booking bookingTest = bookingRepository.getFinishedBookingByBookerItemStatus(userThird.getId()
                ,item.getId(), Status.APPROVED, LocalDateTime.now()).orElse(null);

        assertThat(bookingTest).isNull();
    }
}