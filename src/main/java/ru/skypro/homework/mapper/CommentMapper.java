package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "userEntity.id", target = "author")
    @Mapping(source = "userEntity.firstName", target = "authorFirstName")
    @Mapping(source = "userEntity.imageEntity.filePath", target = "authorImage")
    @Mapping(source = "adEntity.pk", target = "adId")
    Comment commentEntityToComment(CommentEntity commentEntity);

    List<Comment> commentEntityListToCommentList(List<CommentEntity> commentEntityList);

    CommentEntity createOrUpdateCommentToCommentEntity(CreateOrUpdateComment createOrUpdateComment);


}
