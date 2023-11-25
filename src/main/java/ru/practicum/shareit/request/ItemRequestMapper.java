package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ExtendedItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ItemRequestMapper {
    ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestForResponseDto itemRequestToItemRequestForResponseDto(ItemRequest itemRequest);

    ExtendedItemRequestForResponseDto itemRequestToExtendedItemRequestForResponseDto(ItemRequest itemRequest);
}
