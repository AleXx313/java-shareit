package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    @Test
    void itemToShort_returnShortDtoOfItem() {
        Item item = Item.builder().id(1L)
                .name("item")
                .description("item to do smt")
                .owner(new User())
                .request(new ItemRequest())
                .available(true)
                .build();

        ItemDtoShort shortItem = ItemMapper.itemToShort(item);

        assertThat(shortItem).hasOnlyFields("id", "name");
        assertThat(shortItem.getName()).isEqualTo("item");
        assertThat(shortItem.getId()).isEqualTo(1L);
    }
}