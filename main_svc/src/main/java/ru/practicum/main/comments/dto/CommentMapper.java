package ru.practicum.main.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.main.comments.model.Comment;

@UtilityClass
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {

        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setEventId(comment.getEvent().getId());
        commentDto.setAuthorId(comment.getAuthor().getId());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

}
