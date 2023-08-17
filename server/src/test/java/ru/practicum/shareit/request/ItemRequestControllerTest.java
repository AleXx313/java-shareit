package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAllByRequester() throws Exception {

        when(requestService.findAllByRequester(1L)).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

    }

    @Test
    void findAll() throws Exception {
        when(requestService.findAll(0, 10, 1L)).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("need")
                .build();
        when(requestService.findById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

    }

    @Test
    void save() throws Exception {
        ItemRequest request = ItemRequest.builder()
                .description("need")
                .build();
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("need")
                .build();
        when(requestService.save(1L, dto)).thenReturn(dto);
        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }
}