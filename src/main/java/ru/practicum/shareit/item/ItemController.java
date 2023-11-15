package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.item.dto.CommentForRequestDto;
import ru.practicum.shareit.item.dto.CommentForResponseDto;
import ru.practicum.shareit.item.dto.ExtendedItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.user.Marker;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ExtendedItemForResponseDto find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return itemService.findItem(userId, id);
    }

    @GetMapping
    public List<ExtendedItemForResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemForResponseDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam String text) {
        return itemService.searchItem(userId, text);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ItemForResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Valid @RequestBody ItemForRequestDto itemDto) {
        log.debug("Received POST-request /items with header X-Sharer-User-Id={} and body: {}", userId, itemDto);

        return itemService.createNewItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public ItemForResponseDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long id,
                                     @RequestBody ItemForRequestDto itemDto) {
        log.debug("Received PATCH-request /items/{} with header X-Sharer-User-Id={} and body: {}", id, userId, itemDto);

        return itemService.updateItem(userId, id, itemDto);
    }

    @PostMapping("/{id}/comment")
    public CommentForResponseDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long id,
                                               @Valid @RequestBody CommentForRequestDto commentDto) {
        log.debug("Received POST-request /items/{}/comment with header X-Sharer-User-Id={} and body: {}", id, userId,
                commentDto);

        return itemService.createNewComment(userId, id, commentDto);
    }
}
