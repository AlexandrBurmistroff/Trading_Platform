package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.EntityForbiddenException;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.util.UserAuthentication;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис для поиска, добавления, обновления, удаления комментария
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final CommentMapper commentMapper;
    private final UserAuthentication userAuthentication;

    /**
     * @param id id комменатрия
     *           Поиск комментария в базе данных в формате CommentEntity
     * @return Optional<Comment>
     * @throws NullPointerException если комментарий не найден
     *                              Конвертация CommentEntity в Comment
     */
    @Override
    public Comments getComments(Integer id) {
        Optional<AdEntity> adEntity = adRepository.findById(id);

        if (adEntity.isEmpty()) {
            throw new EntityNotFoundException("Объявления не найдено");
        } else {
            Collection<CommentEntity> commentEntities = adEntity.get().getCommentEntities();
            List<Comment> commentList = commentMapper.commentEntityListToCommentList(List.copyOf(commentEntities));

            return Comments.builder()
                    .count(commentList.size())
                    .results(commentList)
                    .build();
        }
    }

    /**
     * @param id id объявления
     *           Поиск пользователя в базе данных по email
     *           Создание объекта Comment, конвертация в CommentEntity и сохранение в базу данных.
     * @return Optional<Comment>
     * @throws NullPointerException если пользователь не найден
     */
    @Override
    public Comment addComment(Integer id, CreateOrUpdateComment createOrUpdateComment) {
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();

        if (currentUserEntity.getId() == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        Optional<AdEntity> adEntityOptional = adRepository.findById(id);
        AdEntity adEntity = adEntityOptional.orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));

        CommentEntity commentEntity = commentMapper.createOrUpdateCommentToCommentEntity(createOrUpdateComment);
        commentEntity.setAdEntity(adEntity);
        commentEntity.setUserEntity(currentUserEntity);
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentRepository.save(commentEntity);

        return commentMapper.commentEntityToComment(commentEntity);
    }

    /**
     * @param id        id объявления
     * @param commentId id комментария
     *                  Поиск объявления в базе данных по email
     *                  удаление комментария по id из базы данных
     * @throws NullPointerException если объявление не найдено
     */
    @Override
    public void deleteComment(Integer id, Integer commentId) {
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();
        Optional<CommentEntity> commentEntityOptional = getCommentFromAd(id, commentId);

        if (commentEntityOptional.isEmpty() && currentUserEntity == null) {
            throw new EntityNotFoundException("Комментарий не найден");
        } else {
            if (currentUserEntity.getId().equals(commentEntityOptional.get().getUserEntity().getId()) ||
                    currentUserEntity.getRole().equals(Role.ADMIN)) {
                commentRepository.deleteById(commentId);
            } else {
                throw new EntityForbiddenException();
            }
        }
    }

    /**
     * @param adId      id объявления
     * @param commentId id комментария
     *                  Поиск комментария в базе данных по email
     *                  Поиск объекта CommentEntity и изменение текста комментария, сохранение в базу данных
     *                  конвертация в CommentEntity и сохранение в базу данных.
     * @return Optional<Comment>
     * @throws NullPointerException если комментарий не найден
     */
    @Override
    public Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment) {
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();
        Optional<CommentEntity> commentEntityOptional = getCommentFromAd(adId, commentId);

        if (commentEntityOptional.isEmpty() && currentUserEntity == null) {
            throw new EntityNotFoundException("Комментарий не найден");
        } else {
            if (currentUserEntity.getId().equals(commentEntityOptional.get().getUserEntity().getId()) ||
                    currentUserEntity.getRole().equals(Role.ADMIN)) {
                CommentEntity commentEntity = commentEntityOptional.get();
                CommentEntity commentEntityMapper = commentMapper.createOrUpdateCommentToCommentEntity(createOrUpdateComment);
                commentEntity.setText(commentEntityMapper.getText());
                commentEntity.setCreatedAt(LocalDateTime.now());
                commentRepository.save(commentEntity);
                return commentMapper.commentEntityToComment(commentEntity);
            } else {
                throw new EntityForbiddenException();
            }
        }
    }

    private Optional<CommentEntity> getCommentFromAd(Integer adId, Integer commentId) {
        Optional<AdEntity> adEntityOptional = adRepository.findById(adId);
        return adEntityOptional.orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"))
                .getCommentEntities().stream()
                .filter(Objects::nonNull)
                .filter(commentEntity1 -> commentEntity1.getPk().equals(commentId))
                .findFirst();
    }
}