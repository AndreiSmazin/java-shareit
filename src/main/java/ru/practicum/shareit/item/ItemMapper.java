package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemRequestDtoToItem(ItemForRequestDto itemForRequestDto);
    ItemForResponseDto itemToItemForResponseDto(Item item);
}
