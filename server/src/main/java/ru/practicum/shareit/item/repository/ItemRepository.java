package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(long id);

    Page<Item> findAllByOwnerIdOrderById(long ownerId, Pageable pageable);

    @Query("select i from Item as i" +
            " where i.available = true " +
            " and (lower(i.name) like lower(concat('%', ?1, '%'))" +
            " or lower(i.description) like lower(concat('%', ?1, '%')))")
    Page<Item> findItemsByNameOrDescription(String text, Pageable pageable);

    List<Item> findAllByRequestId(long requestId);
}
