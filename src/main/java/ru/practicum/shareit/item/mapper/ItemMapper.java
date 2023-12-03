package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ItemMapper {
    Item itemForRequestDtoToItem(ItemCreateUpdateDto itemCreateUpdateDto);

    ItemResponseDto itemToItemForResponseDto(Item item);

    ItemExtendedResponseDto itemToExtendedItemForResponseDto(Item item);

    Comment commentForRequestDtoToComment(CommentCreateDto commentCreateDto);

    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "author.name", target = "authorName")
    CommentResponseDto commentToCommentForResponseDto(Comment comment, User author);
}
