package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private User makeUser(long id){
        return User.builder().id(id).name("user" + id).email("user" + id + "@yandex.ru").build();
    }
    private UserDto makeDto(long id){
        return UserDto.builder().id(id).name("user" + id).email("user" + id + "@yandex.ru").build();
    }

    @Test
    void save_whenUserValid_thenSaveUser() {
        User user = makeUser(1L);
        UserDto userDto = makeDto(1L);
        when(userRepository.save(user)).thenReturn(user);

        UserDto savedUser = userService.save(userDto);

        assertThat(savedUser).isEqualTo(userDto);
        verify(userRepository, atLeastOnce()).save(user);
    }

    @Test
    void update_whenUserValid_thenUpdate() {
        User user = makeUser(1L);
        UserDto newUserDto = makeDto(1L);
        newUserDto.setEmail("yuoyouiou@mail.ru");
        long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto actualUserDto = userService.update(id, newUserDto);
        verify(userRepository).save(any(User.class));
        assertThat(actualUserDto.getEmail()).isEqualTo("yuoyouiou@mail.ru");
    }
    @Test
    void update_whenUserNotExist_thenThrowModelNotFoundException(){
        UserDto newUserDto = makeDto(1L);
        long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());


        assertThatThrownBy(()-> userService.update(id, newUserDto)).hasMessage(
                String.format("Пользователь с id - %d не найден!", id));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any());
    }


    @Test
    void getById_whenUserExist_thenReturnUserDto() {
        long id = 1L;
        User user = makeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(id);

        verify(userRepository, times(1)).findById(id);
        assertThat(userDto.getName()).isEqualTo("user1");

    }
    @Test
    void getById_whenUserNotExist_thenThrowModelNotFoundException() {
        long id = 1L;
        User user = makeUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id)).isExactlyInstanceOf(ModelNotFoundException.class);
        verify(userRepository, times(1)).findById(id);

    }


    @Test
    void getAllUsers_whenUsersExists_thenGetListOfUsersDto() {
        User user = makeUser(1L);
        User user2 = makeUser(2L);
        User user3 = makeUser(3L);
        when(userRepository.findAll()).thenReturn(List.of(user, user2, user3));

        List<UserDto> users = userService.getAllUsers();

        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0).getName()).isEqualTo("user1");
        assertThat(users.get(1).getName()).isEqualTo("user2");
        assertThat(users.get(2).getName()).isEqualTo("user3");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_whenUsersNotExists_thenGetEmptyList() {

        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> users = userService.getAllUsers();

        assertThat(users.size()).isEqualTo(0);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteById_whenDeleted_thenDeleted() { // Просто о том, как я понимаю нейминг тестов.
        userService.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);

    }
}