package ru.practicum.shareit.item.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {
    private static Long id = 1L;
    private final Map<Long, List<Item>> items = new HashMap<>();

    private final UserRepository userRepository;

    @Autowired
    public InMemoryItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Item findOneById(Long itemId) {
        Optional<Item> optItem = items.values().stream()
                .flatMap(byOwner -> byOwner.stream())
                .filter(i -> Objects.equals(i.getId(), itemId))
                .findFirst();

        if (optItem.isEmpty()) {
            throw new NoSuchElementException(String.format("Item with id %d not found", itemId));
        }

        return optItem.get();
    }

    @Override
    public Item add(Item item) {
        userRepository.findOneById(item.getOwner());

        item.setId(id);
        id++;

        List<Item> ownerItems = items.getOrDefault(item.getOwner(), new ArrayList<>());
        ownerItems.add(item);
        items.put(item.getOwner(), ownerItems);

        return item;
    }

    @Override
    public Item update(Item updatedItem) {
        Item item = findOneByUserAndItemId(updatedItem.getOwner(), updatedItem.getId());

        String name = updatedItem.getName();
        if (!Objects.isNull(name) && !name.isBlank()) {
            item.setName(updatedItem.getName());
        }

        String description = updatedItem.getDescription();
        if (!Objects.isNull(description) && !description.isBlank()) {
            item.setDescription(updatedItem.getDescription());
        }

        if (!Objects.isNull(updatedItem.getAvailable())) {
            item.setAvailable(updatedItem.getAvailable());
        }

        if (!Objects.isNull(updatedItem.getOwner())) {
            item.setOwner(updatedItem.getOwner());
        }

        return item;
    }

    private Item findOneByUserAndItemId(Long userId, Long itemId) {
        List<Item> ownerItems = items.getOrDefault(userId, new ArrayList<>());

        Optional<Item> optItem = ownerItems.stream()
                .filter(i -> Objects.equals(i.getId(), itemId))
                .findFirst();

        if (optItem.isEmpty()) {
            throw new NoSuchElementException(String.format("Item with id %d not found", itemId));
        }

        return optItem.get();
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        if (!items.containsKey(userId)) {
            throw new NoSuchElementException(String.format("Items for user with id %d not found", userId));
        }

        return items.get(userId);
    }

    @Override
    public List<Item> search(String text) {
        String needle = text.trim().toLowerCase();

        if (needle.isEmpty()) {
            return List.of();
        }

        return items.values().stream()
                .flatMap(byOwner -> byOwner.stream())
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(needle)
                        || item.getDescription().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }
}
