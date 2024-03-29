package ru.practicum.shareit.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(String name, String description);

    List<Item> findAllByOwnerId(Long ownerId);
}
