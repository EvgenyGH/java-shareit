package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.id = ?1 " +
            "AND (b.booker.id = ?2 OR b.item.owner.id = ?2)")
    Optional<Booking> getOwnerOrBookerBooking(Long bookingId, Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByBookerStatusAllOrdered(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByBookerStatusOrdered(Long bookerId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.endDate < ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByBookerStatusPastOrdered(long userId, LocalDateTime dateTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.startDate > ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByBookerStatusFutureOrdered(long userId, LocalDateTime dateTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.endDate > ?2 " +
            "AND b.startDate < ?2" +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByBookerStatusCurrentOrdered(long userId, LocalDateTime dateTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByOwnerStatusAllOrdered(Long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByOwnerStatusOrdered(Long userId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.endDate < ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByOwnerStatusPastOrdered(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.startDate > ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByOwnerStatusFutureOrdered(Long userId, LocalDateTime time);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.endDate > ?2 " +
            "AND b.startDate < ?2" +
            "ORDER BY b.startDate DESC")
    List<Booking> getBookingByOwnerStatusCurrentOrdered(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.startDate < ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> getLastItemBookingOrdered(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.startDate > ?2 " +
            "ORDER BY b.startDate ASC")
    List<Booking> getNextItemBookingOrdered(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.booker.id = ?2 ")
    List<Booking> getBookingByItemBooker(Long itemId, Long bookerId);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.item.id = ?2 " +
            "AND b.status = ?3 " +
            "AND b.endDate < ?4")
    Optional<Booking> getFinishedBookingByBookerItemStatus(Long bookerId
            , Long itemId, Status status, LocalDateTime now);
}
