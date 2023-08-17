package ru.practicum.shareit.item.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment requestToComment(Item item, User user, String text) {
        return Comment.builder()
                .item(item)
                .author(user)
                .text(text)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> listToDtoList(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::commentToDto).collect(Collectors.toList());
    }
}
