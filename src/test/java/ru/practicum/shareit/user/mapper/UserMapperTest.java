package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User makeUser(long id){
        return User.builder().id(id).name("user" + id).email("user" + id + "@yandex.ru").build();
    }
    private UserDto makeDto(long id){
        return UserDto.builder().id(id).name("user" + id).email("user" + id + "@yandex.ru").build();
    }


    @Test
    void dtoToUser_whenInvoked_thenReturnUserWithProperValues() {
        UserDto userDto = makeDto(1L);

        User user = UserMapper.dtoToUser(userDto);

        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getId()).isEqualTo(userDto.getId());
    }

    @Test
    void userToDto_whenInvoked_thenReturnUserDtoWithProperValues() {
        User user = makeUser(1L);

        UserDto userDto = UserMapper.userToDto(user);

        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getId()).isEqualTo(user.getId());
    }

    @Test
    void userToShort_whenInvoked_thenReturnUserDtoWithoutEmail() {
        User user = makeUser(1L);

        UserDtoShort userDtoShort = UserMapper.userToShort(user);

        assertThat(userDtoShort).hasOnlyFields("id", "name");
        assertThat(userDtoShort.getName()).isEqualTo(user.getName());
        assertThat(userDtoShort.getId()).isEqualTo(user.getId());
    }

    @Test
    void listToDtoList() {
        List<User> users = List.of(makeUser(1L), makeUser(2L), makeUser(3L));

        List<UserDto> dtos = UserMapper.listToDtoList(users);

        assertThat(dtos.size()).isEqualTo(3);
        assertThat(dtos.get(0).getName()).isEqualTo("user1");

    }
}