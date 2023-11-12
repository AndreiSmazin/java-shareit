package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findById(Long id);

    Booking save(Booking booking);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id);
}
