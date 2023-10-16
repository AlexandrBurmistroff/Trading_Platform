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
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
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
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;




    /**
     * @param id id комменатрия
     * Поиск комментария в базе данных в формате CommentEntity
     * @throws NullPointerException если комментарий не найден
     * Конвертация CommentEntity в Comment
     * @return Optional<Comment>
     */
    @Override
    public Optional<Comments> getComments(Integer id) {
        Optional<AdEntity> adEntity = adRepository.findById(id);

        if (adEntity.isEmpty()) {
            throw new NullPointerException();
        } else {
            Collection<CommentEntity> commentEntities = adEntity.get().getCommentEntities();
            List<Comment> commentList = commentMapper.commentEntityListToCommentList(List.copyOf(commentEntities)); //c маппером не очень уверен
            Comments comments = new Comments();
            comments.setCount(commentList.size());
            comments.setResults(commentList);
            return Optional.of(comments);
        }
    }


    /**
     * @param id id объявления
     * Поиск пользователя в базе данных по email
     * Создание объекта Comment, конвертация в CommentEntity и сохранение в базу данных.
     * @throws NullPointerException если пользователь не найден
     * @return Optional<Comment>
     */
    @Override
    public Optional<Comment> addComment(Integer id, CreateOrUpdateComment createOrUpdateComment, String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
        UserEntity userEntity = userEntityOptional.orElseThrow(NullPointerException::new);
        Optional<AdEntity> adEntityOptional = adRepository.findById(id);
        AdEntity adEntity = adEntityOptional.orElseThrow(NullPointerException::new);
        CommentEntity commentEntity = commentMapper.createOrUpdateCommentToCommentEntity(createOrUpdateComment);
        commentEntity.setAdEntity(adEntity);
        commentEntity.setUserEntity(userEntity);
        commentEntity.setCreatedAt(LocalDateTime.now().getLong(ChronoField.EPOCH_DAY));
        commentRepository.save(commentEntity);
        Comment comment = commentMapper.commentEntityToComment(commentEntity);
        comment.setAuthor(userEntity.getId());
        comment.setAuthorFirstName(userEntity.getFirstName());
//todo добавить время и достать аватарку автора
        comment.setAuthorImage("authorImage");
        comment.setAdId(adEntity.getPk());

        return Optional.of(comment);
    }


    /**
     * @param id id объявления
     * @param commentId id комментария
     * Поиск объявления в базе данных по email
     * удаление комментария по id из базы данных
     * @throws NullPointerException если объявление не найдено
     */
    @Override
    public boolean deleteComment(Integer id, Integer commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }

    /**
     * @param adId id объявления
     * @param commentId id комментария
     * Поиск комментария в базе данных по email
     * Поиск объекта CommentEntity и изменение текста комментария, сохранение в базу данных
     * конвертация в CommentEntity и сохранение в базу данных.
     * @throws NullPointerException если комментарий не найден
     * @return Optional<Comment>
     */
    @Override
    public Optional<Comment> updateComment(Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment) {

        Optional<AdEntity> adEntityOptional = adRepository.findById(adId);

        if (  adEntityOptional.isEmpty()) {
            throw new NullPointerException();
        }

        Optional<CommentEntity> commentEntityOptional = adEntityOptional.orElseThrow(NullPointerException::new)
                .getCommentEntities().stream()
                .filter(Objects::nonNull)
                .filter(commentEntity1 -> commentEntity1.getPk().equals(commentId))
                .findFirst();

        if (commentEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        CommentEntity commentEntity = commentEntityOptional.get();

        CommentEntity commentEntityMapper = commentMapper.createOrUpdateCommentToCommentEntity(createOrUpdateComment);
        commentEntity.setText(commentEntityMapper.getText());
        //todo обновить время
        commentRepository.save(commentEntity);
        Comment comment = commentMapper.commentEntityToComment(commentEntity);
        comment.setAdId(adId);
        comment.setAuthor(commentEntity.getUserEntity().getId());
        //todo добавить аватарку
        comment.setAuthorImage("image");
        comment.setAuthorFirstName(commentEntity.getUserEntity().getFirstName());
        //todo добавить время
        comment.setCreatedAt(2L);

        return Optional.of(comment);
    }
}