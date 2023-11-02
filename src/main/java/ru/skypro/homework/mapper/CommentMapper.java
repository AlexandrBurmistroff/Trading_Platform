package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    default Comment commentEntityToComment(CommentEntity commentEntity) {
        return Comment.builder()
                .author(commentEntity.getUserEntity().getId())
                .authorImage("/image/" + commentEntity.getUserEntity().getId())
                .authorFirstName(commentEntity.getUserEntity().getFirstName())
                .createdAt(String.valueOf(commentEntity.getCreatedAt()))
                .pk(commentEntity.getPk())
                .text(commentEntity.getText())
                .adId(commentEntity.getAdEntity().getPk())
                .build();
    };

    List<Comment> commentEntityListToCommentList(List<CommentEntity> commentEntityList);

    CommentEntity createOrUpdateCommentToCommentEntity(CreateOrUpdateComment createOrUpdateComment);

}
