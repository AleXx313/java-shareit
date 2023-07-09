package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto save(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                        @RequestBody @Valid ItemDto dto) {
        return service.save(userId, dto);
    }

    @PatchMapping(path = "/{id}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @PathVariable Long id,
                          @RequestBody ItemDto dto) {
        return service.update(userId, id, dto);
    }

    @GetMapping(path = "/{id}")
    public ItemDto getById(@PathVariable(value = "id") Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ItemDto> getListByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return service.getByUserId(userId);
    }

    @GetMapping(path = "/search")
    public List<ItemDto> search(@RequestParam(value = "text") String text) {
        return service.search(text);
    }

}
