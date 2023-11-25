package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ExtendedItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ExtendedItemRequestForResponseDto> findByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ExtendedItemRequestForResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(defaultValue = "0") @Min(0) int from,
                                                           @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return itemRequestService.findAllItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ExtendedItemRequestForResponseDto find(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long id) {
        return itemRequestService.findItemRequestById(userId, id);
    }

    @PostMapping
    public ItemRequestForResponseDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Received POST-request /requests with header X-Sharer-User-Id={} and body: {}", userId,
                itemRequestDto);

        return itemRequestService.createNewItemRequest(userId, itemRequestDto);
    }
}
