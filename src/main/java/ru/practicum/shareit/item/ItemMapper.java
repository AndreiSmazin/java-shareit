package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemForRequestCreateDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

public class ItemMapper {

    public static Item toItem(ItemForRequestCreateDto itemDto) {
        return Item.builder()
                .id(0)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(null)
                .request(null)
                .build();
    }

    public static ItemForResponseDto toItemForResponseDto(Item item) {
        return ItemForResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }
}
