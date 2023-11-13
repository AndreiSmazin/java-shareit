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
            " and b.status = 'APPROVED'" +
            " and b.start < current_timestamp" +
            " order by b.end desc")
    List<BookingForItemDto> findPastBookingsOfItem(Long id);

    @Query("select new ru.practicum.shareit.booking.dto.BookingForItemDto(b.id, br.id) from Booking as b" +
            " join b.item as i" +
            " join b.booker as br" +
            " where i.id = ?1" +
            " and b.status = 'APPROVED'" +
            " and b.start > current_timestamp" +
            " order by b.end")
    List<BookingForItemDto> findFutureBookingsOfItem(Long id);

    @Query("select count(b) from Booking as b" +
            " join b.booker as br" +
            " where br.id = ?1" +
            " and b.status = 'APPROVED'" +
            " and b.end < current_timestamp")
    int findCountBookingsOfUser(Long id);
}
