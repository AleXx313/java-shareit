package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {

    private Long id;
    private Long userId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean available;
}
