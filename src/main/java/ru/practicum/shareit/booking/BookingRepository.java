package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findById(Long id);

    Booking save(Booking booking);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id);

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, br.id) from Booking as b" +
            " join b.item as i" +
            " join b.booker as br" +
            " where i.id = ?1" +
            " and b.end < current_timestamp" +
            " order by b.end desc")
    List<BookingForItemDto> findPastBookings(Long id);

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, br.id) from Booking as b" +
            " join b.item as i" +
            " join b.booker as br" +
            " where i.id = ?1" +
            " and b.start > current_timestamp" +
            " order by b.end")
    List<BookingForItemDto> findFutureBookings(Long id);
}
