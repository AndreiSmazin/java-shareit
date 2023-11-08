package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.user.Marker;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(@Qualifier("itemServiceDbImpl") ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @GetMapping("/{id}")
    public ItemForResponseDto find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return itemMapper.itemToItemForResponseDto(itemService.findItem(userId, id));
    }

    @GetMapping
    public List<ItemForResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {

        return itemService.findAllItems(userId).stream()
                .map(itemMapper::itemToItemForResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemForResponseDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam String text) {
        return itemService.searchItem(userId, text).stream()
                .map(itemMapper::itemToItemForResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ItemForResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Valid @RequestBody ItemForRequestDto itemDto) {
        log.debug("Received POST-request /items with header X-Sharer-User-Id={} and body: {}", userId, itemDto);

        return itemMapper.itemToItemForResponseDto(itemService.createNewItem(userId, itemDto));
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public ItemForResponseDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long id,
                                     @RequestBody ItemForRequestDto itemDto) {
        log.debug("Received PATCH-request /items/{} with header X-Sharer-User-Id={} and body: {}", id, userId, itemDto);

        return itemMapper.itemToItemForResponseDto(itemService.updateItem(userId, id, itemDto));
    }
}
