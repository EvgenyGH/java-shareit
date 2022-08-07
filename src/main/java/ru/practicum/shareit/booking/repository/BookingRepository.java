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
            "WHERE b.id = ?1 AND (b.booker.id = ?2 OR b.item.owner.id = ?2)")
    Optional<Booking> getOwnerOrBookerBooking(Long bookingId, Long bookerId);

    List<Booking> findAllByBooker_idOrderByStartDateDesc(Long bookerId);

    List<Booking> findAllByBooker_idAndStatusOrderByStartDateDesc(Long bookerId, Status status);

    List<Booking> findAllByBooker_idAndStartDateIsBeforeOrderByStartDateDesc(long userId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_idAndStartDateIsAfterOrderByStartDateDesc(long userId, LocalDateTime dateTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.endDate > ?2 " +
            "AND b.startDate < ?2" +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByBooker_idAndStartDateIsBeforeAndEndDateIsAfterSorted(long userId, LocalDateTime dateTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllOwnerBookingSorted(Long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllOwnerBookingStatusSorted(Long userId, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.startDate < ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllOwnerBookingPastSorted(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.startDate > ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllOwnerBookingFutureSorted(Long userId, LocalDateTime time);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.endDate > ?2 " +
            "AND b.startDate < ?2" +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllOwnerBookingCurrentSorted(Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.startDate < ?2 " +
            "ORDER BY b.startDate DESC")
    List<Booking> findAllByItemAndStartDateBeforeNowSorted(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.startDate > ?2 " +
            "ORDER BY b.startDate ASC")
    List<Booking> findAllByItemAndStartDateAfterNowSorted(Long itemId, LocalDateTime now);

    List<Booking> findAllByItem_idAndBooker_id(Long itemId, Long bookerId);

    Optional<Booking> findFirstByBooker_idAndItem_idAndStatusEqualsAndEndDateBefore(Long bookerId
            , Long itemId, Status status, LocalDateTime now);
}
