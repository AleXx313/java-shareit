package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.*;

@Data
@Builder
public class UserRequestDto {

    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotEmpty(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String email;
}