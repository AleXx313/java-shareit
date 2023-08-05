package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto requestToDto(ItemRequest itemRequest){
//        String regDate = DateTimeFormatter
//                .ofPattern("yyyy.MM.dd hh:mm:ss")
//                .withZone(ZoneOffset.UTC)
//                .format(itemRequest.getCreated());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated().toString())
                .build();
    }

    public static List<ItemRequestDto> listToDtosList(List<ItemRequest> requests){
        return requests.stream().map(ItemRequestMapper::requestToDto).collect(Collectors.toList());
    }
}
