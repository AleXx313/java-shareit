package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequest {

    @NotBlank
    private String text;
}
