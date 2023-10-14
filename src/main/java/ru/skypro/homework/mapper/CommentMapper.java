package ru.skypro.homework.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.entity.CommentEntity;

import java.util.List;

@Mapper
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "userEntity.id", target = "author")
    @Mapping(source = "userEntity.filePath", target = "authorImage")
    @Mapping(source = "userEntity.firstName", target = "authorFirstName")
    Comment commentEntityToComment(CommentEntity commentEntity);

    @InheritInverseConfiguration
    CommentEntity commentToCommentEntity(Comment comment);

    List<Comment> commentEntityListToCommentList(List<CommentEntity> commentEntityList);

}
