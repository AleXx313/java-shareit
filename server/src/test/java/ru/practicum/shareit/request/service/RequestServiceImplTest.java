package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void findAllByRequester_whenNoUserFound_thenThrowModelNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.findAllByRequester(userId))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Пользователь с id 1 отсутствует");

    }

    @Test
    void findAllByRequester_whenUserFound_thenReturnListOfItemRequestDto() {
        long userId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        ItemRequest request = new ItemRequest(
                1L,
                "Need hammer",
                LocalDateTime.of(2023, 8, 11, 10, 30, 15),
                user);
        ItemDtoForRequests item = new ItemDtoForRequests(
                1L,
                "Hammer",
                "Knock knock",
                true,
                1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterId(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(item.getId())).thenReturn(List.of(item));

        List<ItemRequestDto> requestDtos = requestService.findAllByRequester(1L);

        assertThat(requestDtos.size()).isEqualTo(1);
        assertThat(requestDtos.get(0).getItems().get(0)).isEqualTo(item);
    }

    @Test
    void findAll_returnListOfItemRequestDtos() {
        long userId = 1L;
        User user = new User(userId, "user", "user@yandex.ru");
        ItemRequest request = new ItemRequest(
                1L,
                "Need hammer",
                LocalDateTime.of(2023, 8, 11, 10, 30, 15),
                user);
        List<ItemRequest> requests = List.of(request);
        ItemDtoForRequests item = new ItemDtoForRequests(
                1L,
                "Hammer",
                "Knock knock",
                true,
                1L);
        when(requestRepository.findAllByRequesterIdNot(PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "created")), 2L)).thenReturn(requests);
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        List<ItemRequestDto> result = requestService.findAll(0, 10, 2L);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getItems().get(0)).isEqualTo(item);
    }

    @Test
    void findById_ifUserNotFound_thenThrowModelNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.findById(1L, userId))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Пользователь с id 1 отсутствует");
    }

    @Test
    void findById_ifRequestNotFound_thenThrowModelNotFoundException() {
        long userId = 1L;
        User user = new User(userId, "user", "user@yandex.ru");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> requestService.findById(1L, userId))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Запрос с id 1 отсутствует");
    }

    @Test
    void findById_ifRequestFound_thenReturnDto() {
        long userId = 1L;
        User user = new User(userId, "user", "user@yandex.ru");
        ItemRequest request = new ItemRequest(
                1L,
                "Need hammer",
                LocalDateTime.of(2023, 8, 11, 10, 30, 15),
                user);
        ItemDtoForRequests item = new ItemDtoForRequests(
                1L,
                "Hammer",
                "Knock knock",
                true,
                1L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(item.getId())).thenReturn(List.of(item));

        ItemRequestDto result = requestService.findById(1L, userId);

        assertThat(result.getItems()).isEqualTo(List.of(item));
    }

    @Test
    void save_ifUserNotFound_thenThrowModelNotFoundException() {
        long userId = 1L;
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("need hammer")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.save(1L, requestDto))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessage("Пользователь с id 1 отсутствует");
    }

    @Test
    void save_ifUserFound_thenSaveRequest() {
        long userId = 1L;
        User user = new User(userId, "user", "user@yandex.ru");
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("need hammer")
                .build();
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("need hammer")
                .created(LocalDateTime.now())
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto result = requestService.save(userId, requestDto);

        assertThat(result).hasOnlyFields("id", "description", "created", "items");
    }
}