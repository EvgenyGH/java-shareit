package ru.practicum.shareitserver.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareitserver.booking.dto.BookingDtoMapper;
import ru.practicum.shareitserver.booking.dto.BookingDtoRequest;
import ru.practicum.shareitserver.booking.dto.BookingDtoResponse;
import ru.practicum.shareitserver.booking.service.BookingService;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.dto.ItemDtoMapper;
import ru.practicum.shareitserver.item.service.ItemService;
import ru.practicum.shareitserver.user.User;
import ru.practicum.shareitserver.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceAndDbIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager manager;

    private User userFirst;
    private User userSecond;
    private Booking bookingFirst;

    @BeforeEach
    void initialization() {
        userFirst = new User(25L, "name 25", "email@25.com");
        userSecond = new User(35L, "name 35", "email@35.com");
        userFirst = userService.addUser(userFirst);
        userSecond = userService.addUser(userSecond);

        Item item = new Item(105L, "name 105", "description 105",
                true, userFirst, null);
        item = itemService.addItem(ItemDtoMapper.itemToDto(item), item.getOwner().getId());

        bookingFirst = new Booking(115L, LocalDateTime.now().minusDays(15),
                LocalDateTime.now().minusDays(12), item, userSecond, Status.APPROVED);
        Booking bookingSecond = new Booking(125L, LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusDays(20), item, userSecond, Status.APPROVED);
        BookingDtoResponse bookingDtoResponse = bookingService.bookItem(new BookingDtoRequest(
                bookingFirst.getStartDate(), bookingFirst.getEndDate(),
                bookingFirst.getItem().getId()), bookingFirst.getBooker().getId());
        bookingFirst.setId(bookingDtoResponse.getId());
        bookingDtoResponse = bookingService.bookItem(new BookingDtoRequest(
                bookingSecond.getStartDate(), bookingSecond.getEndDate(),
                bookingSecond.getItem().getId()), bookingSecond.getBooker().getId());
        bookingSecond.setId(bookingDtoResponse.getId());
    }

    @Test
    @Transactional
    void bookItemTest() {
        BookingDtoResponse dtoResponseTest = bookingService.bookItem(new BookingDtoRequest(
                bookingFirst.getStartDate(), bookingFirst.getEndDate(), bookingFirst.getItem().getId()),
                bookingFirst.getBooker().getId());

        Booking bookingDb = manager.createQuery("SELECT b FROM Booking b " +
                "WHERE b.id = ?1", Booking.class).setParameter(1, dtoResponseTest
                .getId()).getSingleResult();

        assertThat(dtoResponseTest).isEqualTo(BookingDtoMapper.bookingToDto(bookingDb));
    }

    @Test
    @Transactional
    void approveBookingFalseTest() {
        BookingDtoResponse dtoResponseTest = bookingService.approveBooking(
                userFirst.getId(), bookingFirst.getId(), false);

        Booking bookingDb = manager.createQuery("SELECT b FROM Booking b " +
                "WHERE b.id = ?1", Booking.class).setParameter(1, dtoResponseTest
                .getId()).getSingleResult();

        assertThat(dtoResponseTest.getStatus()).isEqualTo(bookingDb.getStatus());
    }

    @Test
    @Transactional
    void getBookingTest() {
        BookingDtoResponse dtoResponseTest = bookingService.getBooking(userSecond.getId(),
                bookingFirst.getId());

        Booking bookingDb = manager.createQuery("SELECT b FROM Booking b " +
                        "WHERE b.id = ?1", Booking.class).setParameter(1, bookingFirst.getId())
                .getSingleResult();

        assertThat(dtoResponseTest).isEqualTo(BookingDtoMapper.bookingToDto(bookingDb));
    }

    @Test
    @Transactional
    void getUserBookingByStatusAllTest() {
        List<BookingDtoResponse> bookingsDtoTest = bookingService
                .getUserBookingByStatus(userSecond.getId(), RequestStatus.ALL, 0, 10);

        List<Booking> bookingsDb = manager.createQuery("SELECT b FROM Booking b " +
                        "WHERE b.booker.id = ?1 " +
                        "ORDER BY b.startDate DESC", Booking.class)
                .setParameter(1, userSecond.getId())
                .getResultList();

        assertThat(bookingsDtoTest).isEqualTo(bookingsDb.stream()
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }

    @Test
    @Transactional
    void getAllUserBookingsStatusPastTest() {
        List<BookingDtoResponse> bookingsDtoTest = bookingService.getAllUserBookings(
                userFirst.getId(), RequestStatus.PAST, 0, 10);

        List<Booking> bookingsDb = manager.createQuery("SELECT b FROM Booking b " +
                        "WHERE b.item.owner.id = ?1 " +
                        "AND b.endDate < ?2 " +
                        "ORDER BY b.startDate DESC", Booking.class)
                .setParameter(1, userFirst.getId()).setParameter(2, LocalDateTime.now())
                .getResultList();

        assertThat(bookingsDtoTest).isEqualTo(bookingsDb.stream()
                .map(BookingDtoMapper::bookingToDto).collect(Collectors.toList()));
    }
}