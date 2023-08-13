package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto makeDto(long id) {
        return UserDto.builder().id(id).name("user" + id).email("user" + id + "@yandex.ru").build();
    }
    @SneakyThrows
    @Test
    void save_whenUserDtoIsValid_thenSave() {
        UserDto userDto = makeDto(1L);
        when(userService.save(any())).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(userDto));
    }

    @SneakyThrows
    @Test
    void save_whenUserDtoIsNotValid_thenStatusIsBadRequest() {
        long userId = 1L;
        UserDto userDto = makeDto(userId);
        userDto.setName("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).save(any());
    }

    @SneakyThrows
    @Test
    void update_whenUserDtoIsValid_thenStatusIsOk() {
        Long userId = 1L;
        UserDto userDto = makeDto(userId);
        when(userService.update(userId, userDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(userDto));
        verify(userService, times(1)).update(userId, userDto);

    }
    @SneakyThrows
    @Test
    void getById_whenSavedUserExist_thenGetDtoWithStatusOk() {
        long userId = 1L;
        UserDto userDto = makeDto(userId);
        when(userService.getById(userId)).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", userId))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

        assertThat(result).isEqualTo(objectMapper.writeValueAsString(userDto));
        verify(userService, times(1)).getById(userId);
    }

    @SneakyThrows
    @Test
    void getById_whenSavedUserNotExist_thenGeExceptionWithStatusNotFound() {
        long userId = 1L;
        when(userService.getById(userId)).thenThrow(new ModelNotFoundException("Пользователь не найден!"));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getById(userId);
    }

    @SneakyThrows
    @Test
    void getAll_whenSavedUserExists_thenGetListOfUsersWithStatusIsOk() {
        List<UserDto> users = List.of(makeDto(1L), makeDto(2L), makeDto(3L));

        when(userService.getAllUsers()).thenReturn(users);

         String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
         List<UserDto> returnedUsers = objectMapper.readValue(result, new TypeReference<>() { });

         assertThat(returnedUsers).isEqualTo(users);
         verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void deleteById() {
        long id = 1L;
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(id);

    }
}