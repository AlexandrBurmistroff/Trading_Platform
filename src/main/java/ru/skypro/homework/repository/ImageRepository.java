package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.ImageEntity;

/**
 * Интерфейс для обращения к БД изображений
 */
public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
}
