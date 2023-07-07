package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    private void idCounter() {
        ++id;
    }

    @Override
    public User save(User user) {
        user.setId(id);
        users.put(user.getId(), user);
        idCounter();
        return getById(user.getId());
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);

        return getById(user.getId());
    }

    @Override
    public User getById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(long id) {
        users.remove(id);
    }
}
