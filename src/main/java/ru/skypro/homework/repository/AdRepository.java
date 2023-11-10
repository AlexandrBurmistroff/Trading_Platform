package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.AdEntity;

/**
 * Интерфейс для обращения к БД объявлений
 */
public interface AdRepository extends JpaRepository<AdEntity, Integer> {
}
