package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDate;

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
    private Long id;

    //дата начала бронирования
    private LocalDate startDate;

    //дата конца бронирования
    private LocalDate endDate;

    //вещь, которую пользователь бронирует
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    //пользователь, который осуществляет бронирование;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    //статус бронирования.
    @Enumerated
    private Status status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Booking booking = (Booking) o;

        return id == booking.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
