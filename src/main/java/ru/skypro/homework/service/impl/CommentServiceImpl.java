package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.util.UserAuthentication;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
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
            Comments comments = new Comments();
            comments.setCount(commentList.size());
            comments.setResults(commentList);
            return comments;
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
        UserEntity currentUserEntity = userAuthentication.getCurrentUserName();
        Optional<UserEntity> userEntityOptional = Optional.of(currentUserEntity);
        UserEntity userEntity = userEntityOptional.orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Optional<AdEntity> adEntityOptional = adRepository.findById(id);
        AdEntity adEntity = adEntityOptional.orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"));

        CommentEntity commentEntity = commentMapper.createOrUpdateCommentToCommentEntity(createOrUpdateComment);
        commentEntity.setAdEntity(adEntity);
        commentEntity.setUserEntity(userEntity);
        commentEntity.setCreatedAt(LocalDateTime.now().getLong(ChronoField.EPOCH_DAY));
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
    public boolean deleteComment(Integer id, Integer commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        throw new EntityNotFoundException("Комментарий не найден");
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

        Optional<AdEntity> adEntityOptional = adRepository.findById(adId);

        Optional<CommentEntity> commentEntityOptional = adEntityOptional.orElseThrow(() -> new EntityNotFoundException("Объявление не найдено"))
                .getCommentEntities().stream()
                .filter(Objects::nonNull)
                .filter(commentEntity1 -> commentEntity1.getPk().equals(commentId))
                .findFirst();

        if (commentEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("Комментарий не найден");
        }
        CommentEntity commentEntity = commentEntityOptional.get();
        CommentEntity commentEntityMapper = commentMapper.createOrUpdateCommentToCommentEntity(createOrUpdateComment);
        commentEntity.setText(commentEntityMapper.getText());
        commentEntity.setCreatedAt(LocalDateTime.now().getLong(ChronoField.EPOCH_DAY));
        commentRepository.save(commentEntity);

        return commentMapper.commentEntityToComment(commentEntity);
    }
}