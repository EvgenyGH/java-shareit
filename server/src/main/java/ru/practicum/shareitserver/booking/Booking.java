package ru.practicum.shareitserver.booking;

import lombok.*;
import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    //уникальный идентификатор бронирования
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    //дата начала бронирования
    private LocalDateTime startDate;

    //дата конца бронирования
    private LocalDateTime endDate;

    //вещь, которую пользователь бронирует
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    //пользователь, который осуществляет бронирование;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    //статус бронирования.
    @Enumerated(EnumType.STRING)
    private Status status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
