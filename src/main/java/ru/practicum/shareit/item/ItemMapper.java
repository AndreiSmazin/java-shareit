package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.dto.ItemForResponseWithBookingsDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemForRequestDtoToItem(ItemForRequestDto itemForRequestDto);

    ItemForResponseDto itemToItemForResponseDto(Item item);

    ItemForResponseWithBookingsDto itemToItemForResponseWithBookingsDto(Item item);

    ItemForBookingDto itemToItemForBookingDto(Item item);
}
