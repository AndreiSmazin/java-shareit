package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Optional<ItemRequest> findById(long id);

    @Query("select ir from ItemRequest as ir" +
            " join ir.requester as r" +
            " where not (r.id = ?1)" +
            " order by ir.created desc")
    Page<ItemRequest> findAllFromOtherUsers(long userId, Pageable pageable);

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long userId);
}
