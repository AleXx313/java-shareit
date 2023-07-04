package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static Item dtoToItem(ItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.isAvailable())
                .build();
    }

    public static ItemDto itemToDto (Item item){
        return ItemDto.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public static List<ItemDto> listToDtoList(List<Item> items){
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
    }
}
