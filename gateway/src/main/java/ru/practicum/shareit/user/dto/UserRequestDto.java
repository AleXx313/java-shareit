package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class UserRequestDto {

    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = Marker.OnUpdate.class)
    private String name;
    @Email
    @NotNull(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = Marker.OnUpdate.class)
    private String email;
}