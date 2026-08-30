package ru.practicum.main.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.comments.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
