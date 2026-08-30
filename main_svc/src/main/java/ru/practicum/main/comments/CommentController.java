package ru.practicum.main.comments;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comments.dto.CommentDto;
import ru.practicum.main.comments.dto.NewCommentDto;
import ru.practicum.main.comments.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long eventId,
                                    @RequestBody NewCommentDto newCommentDto, @RequestParam Long userId) {
        return commentService.createComment(eventId, userId, newCommentDto);
    }

    @PatchMapping("/events/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId, @RequestParam Long userId,
                                    @RequestBody NewCommentDto newCommentDto) {

        return commentService.updateComment(commentId, userId, newCommentDto);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getCommentsByEvent(@PathVariable Long eventId) {
        return commentService.getCommentsByEvent(eventId);
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getCommentsByUser(@PathVariable Long userId) {
        return commentService.getCommentsByUser(userId);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        return commentService.deleteComment(commentId, userId);
    }

}
