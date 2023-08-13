package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private BookingRequestDto makeRequestDto() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setStart(LocalDateTime.now().plusHours(1));
        requestDto.setEnd(LocalDateTime.now().plusHours(3));
        requestDto.setItemId(1L);
        return requestDto;
    }

    @SneakyThrows
    @Test
    void save_whenDtoIsValid_thenStatusIsOk() {
        UserDtoShort booker = UserDtoShort.builder().id(1L).name("Booker").build();
        ItemDtoShort item = ItemDtoShort.builder().id(1L).name("hammer").build();
        BookingRequestDto requestDto = makeRequestDto();
        BookingDto response = BookingDto.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(3))
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.save(1L, requestDto)).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void save_whenDtoIsNotValid_thenStatusIsBadRequest() {
        BookingRequestDto requestDto = makeRequestDto();
        requestDto.setStart(LocalDateTime.now().minusHours(2));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateStatus_whenParamSet_thenStatusIsOk() {
        mockMvc.perform(patch("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void updateStatus_whenNoParamSet_thenStatusIsInternal() {
        mockMvc.perform(patch("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void findById_thenStatusIsOk() {
        mockMvc.perform(get("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void findByBooker_whenNoParams_thenStatusIsOkAndParamInvokedIsDefault() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(bookingService, times(1))
                .findByBooker(1L, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void findByBooker_whenParamsSet_thenStatusIsOkAndParamInvokedIsAsSet() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());
        verify(bookingService, times(1))
                .findByBooker(1L, BookingState.CURRENT, 1, 1);
    }

    @SneakyThrows
    @Test
    void findByBooker_whenStateIsUnknown_thenStatusIsBadRequest() {
        String errorMessage = "Unknown state: abrakadabra";

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "abrakadabra")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result.contains("Unknown state: abrakadabra")).isTrue();
    }

    @SneakyThrows
    @Test
    void findByOwner_whenParamsSet_thenStatusIsOkAndParamInvokedIsAsSet() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "CURRENT")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());
        verify(bookingService, times(1))
                .findByOwner(1L, BookingState.CURRENT, 1, 1);

    }

    @SneakyThrows
    @Test
    void findByOwner_whenNoParams_thenStatusIsOkAndParamInvokedIsDefault() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(bookingService, times(1))
                .findByOwner(1L, BookingState.ALL, 0, 10);

    }

    @SneakyThrows
    @Test
    void findByOwner_whenStateIsUnknown_thenStatusIsBadRequest() {
        String errorMessage = "Unknown state: abrakadabra";

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "abrakadabra")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result.contains("Unknown state: abrakadabra")).isTrue();
    }
}