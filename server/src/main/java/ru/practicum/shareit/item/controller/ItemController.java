package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateUpdateDto;
import ru.practicum.shareit.item.dto.ItemExtendedResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemExtendedResponseDto find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return itemService.findItem(userId, id);
    }

    @GetMapping
    public List<ItemExtendedResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        return itemService.findAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "20") int size,
                                        @RequestParam String text) {
        return itemService.searchItem(userId, text, from, size);
    }

    @PostMapping
    public ItemResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody ItemCreateUpdateDto itemDto) {
        log.debug("Received POST-request /items with header X-Sharer-User-Id={} and body: {}", userId, itemDto);

        return itemService.createNewItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemResponseDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long id,
                                  @RequestBody ItemCreateUpdateDto itemDto) {
        log.debug("Received PATCH-request /items/{} with header X-Sharer-User-Id={} and body: {}", id, userId, itemDto);

        return itemService.updateItem(userId, id, itemDto);
    }

    @PostMapping("/{id}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable long id,
                                            @RequestBody CommentCreateDto commentDto) {
        log.debug("Received POST-request /items/{}/comment with header X-Sharer-User-Id={} and body: {}", id, userId,
                commentDto);

        return itemService.createNewComment(userId, id, commentDto);
    }
}
