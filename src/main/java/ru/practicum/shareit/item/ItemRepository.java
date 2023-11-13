package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(long id);

    List<Item> findAllByOwnerIdOrderById(long ownerId);

    Item save(Item item);

    @Query("select i from Item as i" +
            " where i.available = true " +
            " and (lower(i.name) like lower(concat('%', ?1, '%'))" +
            " or lower(i.description) like lower(concat('%', ?1, '%')))")
    List<Item> findItemsByNameOrDescription(String text);
}
