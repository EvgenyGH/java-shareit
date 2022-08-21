package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    private CommentDto commentDto;

    @Autowired
    private JacksonTester<CommentDto> jsonDto;

    @BeforeEach
    void initialize(){
        User user = new User(10L, "name 10", "email@10.ru");
        ItemRequest itemRequest = new ItemRequest(5L, "description 5"
                , user, LocalDateTime.now().minusHours(1));
        Item item = new Item(15L, "name 15", "description 15", true
                , user, itemRequest);
        Comment comment = new Comment(11L, "text 11", item, user, LocalDateTime.now());
        commentDto = CommentDtoMapper.CommentToDto(comment);
    }

    @Test
    void setCommentDtoTest() throws IOException {
        JsonContent<CommentDto> json = jsonDto.write(commentDto);

        assertThat(json).extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId().intValue());
        assertThat(json).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().toString());
        assertThat(json).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(json).extractingJsonPathStringValue("$.itemName")
                .isEqualTo(commentDto.getItemName());
        assertThat(json).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }
}
