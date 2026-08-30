package ru.practicum.main.comments.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.main.comments.dto.CommentDto;
import ru.practicum.main.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    public CommentDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto);

    public CommentDto updateComment(Long commentId, Long userId, NewCommentDto newCommentDto);

    public List<CommentDto> getCommentsByEvent(Long eventId);

    public List<CommentDto> getCommentsByUser(Long eventId);

    public ResponseEntity<Void> deleteComment(Long commentId, Long userId);

}
