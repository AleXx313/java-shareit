package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void requestToDto() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("need")
                .requester(new User(1L, "user", "user@mail.ru"))
                .created(LocalDateTime.now())
                .build();
        LocalDateTime created = request.getCreated();
        String date = created.toString();

        ItemRequestDto dto = ItemRequestMapper.requestToDto(request);
        assertThat(dto).hasOnlyFields("id", "description", "created", "items");
        assertThat(dto.getCreated()).isEqualTo(date);
    }

    @Test
    void listToDtosList() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("need")
                .requester(new User(1L, "user", "user@mail.ru"))
                .created(LocalDateTime.now())
                .build();
        ItemRequest request2 = ItemRequest.builder()
                .id(2L)
                .description("need")
                .requester(new User(1L, "user", "user@mail.ru"))
                .created(LocalDateTime.now())
                .build();
        ItemRequest request3 = ItemRequest.builder()
                .id(3L)
                .description("need")
                .requester(new User(1L, "user", "user@mail.ru"))
                .created(LocalDateTime.now())
                .build();
        List<ItemRequest> requestList = List.of(request, request2, request3);

        List<ItemRequestDto> dtos = ItemRequestMapper.listToDtosList(requestList);

        assertThat(dtos.size()).isEqualTo(3);
    }
}