package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item save(Item item);
    Item update(Item item);
    Item getById(long id);
    List<Item> getByUserId(long userId);
    List<Item> search(String query);
}
