package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.InvalidBookingException;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UserHaveNotAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void save_whenDtoReturned_thenStatusIsOk() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item to save")
                .available(true)
                .build();
        when(itemService.save(1L, dto)).thenReturn(dto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void save_whenModelNotFoundExceptionThrown_thenStatusIsNotFound() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item to save")
                .available(true)
                .build();
        when(itemService.save(1L, dto)).thenThrow(ModelNotFoundException.class);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_whenReturnedDto_thenStatusIsOK() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item to save")
                .available(true)
                .build();
        when(itemService.update(1L, 1L, dto)).thenReturn(dto);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenThrownModelNotFound_thenStatusIsNotFound() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item to save")
                .available(true)
                .build();
        when(itemService.update(1L, 1L, dto)).thenThrow(ModelNotFoundException.class);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_whenThrownUserHaveNotAccess_thenStatusIsNotFound() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item to save")
                .available(true)
                .build();
        when(itemService.update(1L, 1L, dto)).thenThrow(UserHaveNotAccessException.class);

        mockMvc.perform(patch("/items/{id}", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_thenReturnItemDtoResponseAndStatusOk() throws Exception {
        ItemDtoResponse response = ItemDtoResponse.builder()
                .name("item")
                .description("item to save")
                .available(true)
                .build();

        when(itemService.getById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getListByUserId_thenReturnListOfDtoResponseAndStatusIsOk() throws Exception {
        ItemDtoResponse response = ItemDtoResponse.builder()
                .name("item")
                .description("item to save")
                .available(true)
                .build();
        ItemDtoResponse response2 = ItemDtoResponse.builder()
                .name("item2")
                .description("item2 to save")
                .available(false)
                .build();
        List<ItemDtoResponse> responseList = List.of(response, response2);

        when(itemService.getByUserId(1L, 0, 10)).thenReturn(responseList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void search_whenQueryIsBlank_thenReturnEmptyList() throws Exception {

        when(itemService.search("", 0, 10)).thenReturn(Collections.emptyList());

        String result = mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo(objectMapper.writeValueAsString(Collections.emptyList()));
    }

    @Test
    void search_whenQueryMatched_thenReturnListOfIteDtos() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item to save")
                .available(true)
                .build();

        when(itemService.search("item", 1, 5)).thenReturn(List.of(dto));

        String result = mockMvc.perform(get("/items/search")
                        .param("text", "item")
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(result).isEqualTo(objectMapper.writeValueAsString(List.of(dto)));
    }

    @Test
    void saveComment_whenModelNotFoundExceptionThrown_thenStatusIsNotFound() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("description");
        when(itemService.saveComment(1L, 1L, request))
                .thenThrow(ModelNotFoundException.class);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void saveComment_whenInvalidBookingThrown_thenStatusIsBadRequest() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("description");
        when(itemService.saveComment(1L, 1L, request))
                .thenThrow(InvalidBookingException.class);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveComment_whenReturnedCommentDto_thenStatusIsOk() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setText("description");
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .authorName("user")
                .text("description")
                .build();
        when(itemService.saveComment(1L, 1L, request))
                .thenReturn(dto);

        mockMvc.perform(post("/items/{id}/comment", 1L)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}