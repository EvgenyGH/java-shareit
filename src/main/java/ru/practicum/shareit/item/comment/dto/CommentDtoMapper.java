package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentDtoMapper {
    public static CommentDto CommentToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem().getName()
                , comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment DtoToComment(CommentDto commentDto, Item item, User author) {
        return new Comment(commentDto.getId(), commentDto.getText()
                , item, author, LocalDateTime.now());
    }
}
