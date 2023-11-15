package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentForRequestDto;
import ru.practicum.shareit.item.dto.CommentForResponseDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.dto.ExtendedItemForResponseDto;
import ru.practicum.shareit.user.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ItemMapper {
    Item itemForRequestDtoToItem(ItemForRequestDto itemForRequestDto);

    ItemForResponseDto itemToItemForResponseDto(Item item);

    ExtendedItemForResponseDto itemToExtendedItemForResponseDto(Item item);

    Comment commentForRequestDtoToComment(CommentForRequestDto commentForRequestDto);

    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "author.name", target = "authorName")
    CommentForResponseDto commentToCommentForResponseDto(Comment comment, User author);
}
