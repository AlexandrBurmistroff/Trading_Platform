package ru.skypro.homework.service;

import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;

import java.util.Optional;

public interface CommentService {

    Optional<Comments> getComments(Integer id);
    Optional<Comment> addComment(Integer id, CreateOrUpdateComment createOrUpdateComment, String email);
    boolean deleteComment(Integer id, Integer commentId);
    Optional<Comment> updateComment(Integer adId, Integer commentId, CreateOrUpdateComment createOrUpdateComment);
}
