package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true, access = AccessLevel.PUBLIC)
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;

    public Boolean isAvailable() {
        return available;
    }
}
