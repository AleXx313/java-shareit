package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private Long id;
    private Long userId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean available;
}
