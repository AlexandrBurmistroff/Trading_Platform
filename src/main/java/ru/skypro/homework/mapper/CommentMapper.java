package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

/**
 * Класс конвертирует модель CommentEntity в Comment Dto и обратно.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    default Comment commentEntityToComment(CommentEntity commentEntity) {
        Comment comment = new Comment();
        comment.setAuthor(commentEntity.getUserEntity().getId());
        if (commentEntity.getUserEntity().getImageEntity() != null) {
            comment.setAuthorImage("/image/" + commentEntity.getUserEntity().getImageEntity().getId());
        }
        comment.setAuthorFirstName(commentEntity.getUserEntity().getFirstName());
        comment.setCreatedAt(String.valueOf(commentEntity.getCreatedAt()));
        comment.setPk(commentEntity.getPk());
        comment.setText(commentEntity.getText());
        comment.setAdId(commentEntity.getAdEntity().getPk());
        return comment;
    };

    List<Comment> commentEntityListToCommentList(List<CommentEntity> commentEntityList);

    CommentEntity createOrUpdateCommentToCommentEntity(CreateOrUpdateComment createOrUpdateComment);

}
