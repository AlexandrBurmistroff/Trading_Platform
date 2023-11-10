package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.CommentEntity;

/**
 * Интерфейс для обращения к БД комментарий
 */
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
}
