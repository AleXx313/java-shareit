package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderById(PageRequest pageRequest, Long userId);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "and (lower(it.description) like lower(concat('%', ?1, '%')) " +
            "or lower(it.name) like lower(concat('%', ?1, '%')))")
    List<Item> search(PageRequest pageRequest, String query);

    List<ItemDtoForRequests> findByRequestId(long id);
}
