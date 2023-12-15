package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ItemRequestMapper {
    ItemRequest itemRequestDtoToItemRequest(ItemRequestCreateDto itemRequestCreateDto);

    ItemRequestResponseDto itemRequestToItemRequestForResponseDto(ItemRequest itemRequest);

    ItemRequestExtendedResponseDto itemRequestToExtendedItemRequestForResponseDto(ItemRequest itemRequest);
}
