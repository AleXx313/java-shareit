package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TestEntityManager em;

    private User user;

    @BeforeEach
    void createUser() {
        user = new User();
        user.setName("user");
        user.setEmail("user@email.ru");
        em.persist(user);
    }

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    private Item makeItem(int num) {
        return Item.builder()
                .owner(user)
                .name("Item" + num)
                .description("Description" + num)
                .available(true)
                .build();
    }

    @Test
    void save_whenItemIsValid_thenReturnUserWithGeneratedId() {
        Item item = makeItem(1);

        Item savedItem = itemRepository.save(item);

        assertThat(savedItem.getId()).isNotNull();
    }

    @Test
    void save_whenUserExists_thenInsteadUpdateUser() {
        Item item = makeItem(1);

        Item savedItem = itemRepository.save(item);

        Item updatedItem = Item.builder()
                .id(savedItem.getId())
                .name("Кувалда")
                .description("Как молоток но побольше")
                .available(false)
                .build();

        savedItem = itemRepository.save(updatedItem);

        assertThat(savedItem.getName()).isEqualTo("Кувалда");
        assertThat(savedItem.getDescription()).isEqualTo("Как молоток но побольше");
        assertThat(savedItem.getAvailable()).isFalse();
    }

    @Test
    void findAllByOwnerIdOrderById_whenOwnerHasItems_thenReturnListOfItemsSortedById() {
        Item item = makeItem(1);
        Item item2 = makeItem(2);
        Item item3 = makeItem(3);
        itemRepository.save(item2);
        itemRepository.save(item);
        itemRepository.save(item3);

        List<Item> items = itemRepository.findAllByOwnerIdOrderById(PageRequest.of(0, 10), user.getId());

        assertThat(items.size()).isEqualTo(3);
        assertThat(items.get(0).getName()).isEqualTo("Item2");
        assertThat(items.get(1).getName()).isEqualTo("Item1");
        assertThat(items.get(2).getName()).isEqualTo("Item3");
    }

    @Test
    void findAllByOwnerIdOrderById_whenOwnerHasNoItems_thenReturnEmptyList() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(PageRequest.of(0, 10), user.getId());

        assertThat(items.isEmpty()).isTrue();
    }

    @Test
    void search_whenItemWithNameOrDescriptionContainingQueryExists_thenReturnItem() {
        Item item = makeItem(1);
        Item item2 = makeItem(2);
        itemRepository.save(item);
        itemRepository.save(item2);

        List<Item> firstQueryList = itemRepository.search(PageRequest.of(0, 10), "rIpTi");
        List<Item> secondQueryList = itemRepository.search(PageRequest.of(0, 10), "iOn2");
        List<Item> thirdQueryList = itemRepository.search(PageRequest.of(0, 10), "definitely not match");

        assertThat(firstQueryList.size()).isEqualTo(2);
        assertThat(secondQueryList.size()).isEqualTo(1);
        assertThat(thirdQueryList.isEmpty()).isTrue();
    }
}