package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 1, max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(min = 1, max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}
