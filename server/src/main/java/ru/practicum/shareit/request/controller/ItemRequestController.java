package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestExtendedResponseDto> findByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestExtendedResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "20") int size) {
        return itemRequestService.findAllItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestExtendedResponseDto find(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long id) {
        return itemRequestService.findItemRequestById(userId, id);
    }

    @PostMapping
    public ItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.debug("Received POST-request /requests with header X-Sharer-User-Id={} and body: {}", userId,
                itemRequestCreateDto);

        return itemRequestService.createNewItemRequest(userId, itemRequestCreateDto);
    }
}
