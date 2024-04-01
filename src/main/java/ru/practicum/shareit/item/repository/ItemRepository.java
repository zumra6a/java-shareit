package ru.practicum.shareit.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item as i " +
            "WHERE i.available = true and " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%') ) or " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%') ))")
    List<Item> searchAvailableItemsByNameAndDescription(String name);

    List<Item> findAllByOwnerId(Long ownerId);
}
