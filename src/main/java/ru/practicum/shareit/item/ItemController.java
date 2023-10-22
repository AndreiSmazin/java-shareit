package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemForRequestCreateDto;
import ru.practicum.shareit.item.dto.ItemForRequestUpdateDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemForResponseDto find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return ItemMapper.toItemForResponseDto(itemService.findItem(userId, id));
    }

    @GetMapping
    public List<ItemForResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {

        return itemService.findAllItems(userId).stream()
                .map(ItemMapper::toItemForResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemForResponseDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam String text) {
        return itemService.searchItem(userId, text).stream()
                .map(ItemMapper::toItemForResponseDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemForResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Valid @RequestBody ItemForRequestCreateDto itemDto) {
        log.info("Received POST-request /items with header X-Sharer-User-Id={} and body: {}", userId, itemDto);

        return ItemMapper.toItemForResponseDto(itemService.createNewItem(userId, itemDto));
    }

    @PatchMapping("/{id}")
    public ItemForResponseDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long id,
                                     @RequestBody ItemForRequestUpdateDto itemDto) {
        log.info("Received PATCH-request /items/{} with header X-Sharer-User-Id={} and body: {}", id, userId, itemDto);

        return ItemMapper.toItemForResponseDto(itemService.updateItem(userId, id, itemDto));
    }
}
