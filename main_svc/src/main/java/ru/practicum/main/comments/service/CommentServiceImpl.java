package ru.practicum.main.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.comments.dto.CommentDto;
import ru.practicum.main.comments.dto.CommentMapper;
import ru.practicum.main.comments.dto.NewCommentDto;
import ru.practicum.main.comments.model.Comment;
import ru.practicum.main.comments.repository.CommentRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ForbiddenException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CommentDto createComment(Long eventId, Long userId, NewCommentDto newCommentDto) {

        log.info("Запрос на добавления нового коммента для события: {} от пользователя {}", eventId, userId);

        if (eventId == null) {
            throw new BadRequestException("ID мероприятия не может быть null");
        }
        if (userId == null) {
            throw new BadRequestException("ID пользователя не может быть null");
        }
        if (newCommentDto == null || newCommentDto.getText() == null || newCommentDto.getText().isBlank()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }


        Event saveCommentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено мероприятие с ID:" + eventId));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEvent(saveCommentEvent);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment saveComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(saveComment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long commentId, Long userId, NewCommentDto newCommentDto) {
        log.info("Запрос на обновления  коммента для комментария: {} от пользователя {}", commentId, userId);

        if (commentId == null) {
            throw new BadRequestException("ID комментария не может быть null");
        }
        if (userId == null) {
            throw new BadRequestException("ID пользователя не может быть null");
        }
        if (newCommentDto == null || newCommentDto.getText() == null || newCommentDto.getText().isBlank()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с ID:" + commentId));

        if (comment.getText().equals(newCommentDto.getText())) {
            throw new BadRequestException("Вы не внесли изменений в текст комментария");
        }

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Только автор может редактировать сообщение");
        }

        comment.setText(newCommentDto.getText());

        Comment updateCom = commentRepository.save(comment);

        return CommentMapper.toCommentDto(updateCom);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId) {
        log.info("Запрос на получения комментарий для мероприятия: {}", eventId);

        if (eventId == null) {
            throw new BadRequestException("ID мероприятия не может быть null");
        }

        Event getEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Не найдено мероприятие с ID:" + eventId));

        List<Comment> commentEvent = getEvent.getComments();

        return commentEvent.stream()
                .sorted(Comparator.comparing(Comment::getCreated))
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByUser(Long userId) {
        log.info("Запрос на получения комментарий от пользователя: {}", userId);

        if (userId == null) {
            throw new BadRequestException("ID мероприятия не может быть null");
        }

        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        List<Comment> commentEvent = getUser.getComments();

        return commentEvent.stream()
                .sorted(Comparator.comparing(Comment::getCreated))
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteComment(Long commentId, Long userId) {
        log.info("Запрос на удаления комментарий от пользователя: {}", userId);

        if (userId == null) {
            throw new BadRequestException("ID пользователя не может быть null");
        }

        if (commentId == null) {
            throw new BadRequestException("ID комментария не может быть null");
        }

        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с ID:" + userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Не найден комментарий с ID:" + commentId));

        if (!comment.getAuthor().getId().equals(commentId)) {
            throw new ForbiddenException("Только автор комментария может удалить");
        }

        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }

}
