package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    private void idCounter() {
        ++id;
    }

    @Override
    public Item save(Item item) {
        item.setId(id);
        items.put(id, item);
        idCounter();
        return item;
    }

//    @Override
//    public Item update(Item item) {
//        items.put(item.getId(), item);
//        return item;
//    }

    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getByUserId(long userId) {
        return items.values().stream().filter((a) -> a.getUserId() == userId).collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String query) {
        return items.values().stream()
                .filter((a) -> a.getAvailable() == true
                        && (a.getName().toLowerCase().contains(query)
                        || a.getDescription().toLowerCase().contains(query))).collect(Collectors.toList());
    }
}
