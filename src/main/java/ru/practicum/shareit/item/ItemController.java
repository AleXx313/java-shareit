package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;
    @PostMapping
    public ItemDto save (@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                         @Valid ItemDto dto){
        return service.save(userId, dto);
    }

    @PatchMapping
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                          @Valid ItemDto dto){
        //Если userId или id в dto = 0 item не существует
        //Если userId не равен userId в dto с id полученному по запросу, то в доступе к операции отказано
        return service.update(dto);
    }

    @GetMapping(path = "/{id}")
    public ItemDto getById(@PathVariable(value = "id") Long id){
        return service.getById(id);
    }

}
