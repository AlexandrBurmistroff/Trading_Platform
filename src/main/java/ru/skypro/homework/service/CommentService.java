package ru.skypro.homework.service;

import ru.skypro.homework.config.CanDeleteOrUpdate;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

public interface CommentService {

    Comments getComments(Integer id);
    Comment addComment(Integer id, CreateOrUpdateComment createOrUpdateComment);

    @CanDeleteOrUpdate
    void deleteComment(Integer id, Integer commentId);

    @CanDeleteOrUpdate
    Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment);
}
