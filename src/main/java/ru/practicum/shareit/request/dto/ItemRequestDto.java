package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {

    private long id;
    @NotBlank
    private String description;
    private String created;
    private List<ItemDtoForRequests> items;

}
