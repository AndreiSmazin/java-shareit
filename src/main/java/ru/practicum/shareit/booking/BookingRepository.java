package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findById(long id);

    List<Booking> findAllByBookerIdOrderByStartDesc(long id);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long id);

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, br.id) from Booking as b" +
            " join b.item as i" +
            " join b.booker as br" +
            " where i.id = ?1" +
            " and b.status = 'APPROVED'" +
            " and b.start < current_timestamp" +
            " order by b.end desc")
    List<BookingForItemDto> findPastBookingsOfItem(long id);

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, br.id) from Booking as b" +
            " join b.item as i" +
            " join b.booker as br" +
            " where i.id = ?1" +
            " and b.status = 'APPROVED'" +
            " and b.start > current_timestamp" +
            " order by b.end")
    List<BookingForItemDto> findFutureBookingsOfItem(long id);

    @Query("select count(b) from Booking as b" +
            " join b.booker as br" +
            " join b.item as i" +
            " where br.id = ?1" +
            " and i.id = ?2" +
            " and b.status = 'APPROVED'" +
            " and b.end < current_timestamp")
    int findCountBookingsOfUser(long userId, long itemId);
}
