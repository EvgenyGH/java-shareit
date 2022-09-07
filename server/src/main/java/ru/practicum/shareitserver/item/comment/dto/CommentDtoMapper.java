package ru.practicum.shareitserver.item.comment.dto;

import ru.practicum.shareitserver.item.Item;
import ru.practicum.shareitserver.item.comment.Comment;
import ru.practicum.shareitserver.user.User;

import java.time.LocalDateTime;

public class CommentDtoMapper {
    public static CommentDto commentToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem().getName(),
                comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment dtoToComment(CommentDto commentDto, Item item, User author) {
        return new Comment(commentDto.getId(), commentDto.getText(),
                item, author, LocalDateTime.now());
    }
}
