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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;




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
            List<Comment> commentList = CommentMapper.INSTANCE.commentEntityListToCommentList(List.copyOf(commentEntities)); //c маппером не очень уверен
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
        Comment comment = Comment.builder()
                .author(userEntity.getId())
                .authorFirstName(userEntity.getFirstName())
                .authorImage(userEntity.getFilePath())
                .createdAt(1L) //todo добавить время
                .text(createOrUpdateComment.getText())
                .adId(id)
                .build();

        CommentEntity commentEntity = CommentMapper.INSTANCE.commentToCommentEntity(comment);
        commentRepository.save(commentEntity);
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
    public void deleteComment(Integer id, Integer commentId) {
        if (adRepository.findById(id).isEmpty()) {
            throw new NullPointerException();
        }
        commentRepository.deleteById(commentId); //todo не очень уверен

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

        Optional<CommentEntity> commentEntityOptional = commentRepository.findById(commentId);

        if (commentEntityOptional.isEmpty()) {
            throw new NullPointerException();
        }

        CommentEntity commentEntity = commentEntityOptional.get();
        commentEntity.setText(createOrUpdateComment.getText());
        commentRepository.save(commentEntity);

        Comment comment = CommentMapper.INSTANCE.commentEntityToComment(commentEntity);

        return Optional.of(comment);
    }
}