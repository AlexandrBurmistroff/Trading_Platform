package ru.skypro.homework.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "userEntity.id", target = "author")
    @Mapping(source = "userEntity.firstName", target = "authorFirstName")
    Comment commentEntityToComment(CommentEntity commentEntity);

    @InheritInverseConfiguration
    CommentEntity commentToCommentEntity(Comment comment);

    CommentEntity createOrUpdateCommentToCommentEntity(CreateOrUpdateComment createOrUpdateComment);

    List<Comment> commentEntityListToCommentList(List<CommentEntity> commentEntityList);

}
