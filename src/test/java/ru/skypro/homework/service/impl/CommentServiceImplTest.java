package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.EntityForbiddenException;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.util.UserAuthentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;


class CommentServiceImplTest {


    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserAuthentication userAuthentication;

    @Mock
    private ImageEntity imageEntity;

    private CommentEntity commentEntity1;
    private CommentEntity commentEntity2;
    private Comment comment1;
    private Comment comment2;
    private LocalDateTime time;
    private List<CommentEntity> commentEntityList;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UserEntity userEntity;
    private AdEntity adEntity;

    @BeforeEach
    void init() {

        MockitoAnnotations.initMocks(this);

        time = LocalDateTime.now();

        comment1 = Comment.builder()
                .adId(1)
                .author(1)
                .authorFirstName("testUser")
                .pk(1)
                .text("comment1")
                .authorImage("filePath")
                .createdAt(time.toString())
                .build();

        comment2 = Comment.builder()
                .adId(1)
                .author(1)
                .authorFirstName("testUser")
                .pk(1)
                .text("comment2")
                .authorImage("filePath")
                .createdAt(time.toString())
                .build();



        userEntity = new UserEntity(1, "testUser", "password", "firstName",
                "lastName", "999", Role.USER, null, commentEntityList, imageEntity);

        commentEntity1 = new CommentEntity(1, "comment1", time, userEntity, adEntity);
        commentEntity2 = new CommentEntity(2, "comment2", time, userEntity, adEntity);

        commentEntityList = new ArrayList<>();
        commentEntityList.add(commentEntity1);
        commentEntityList.add(commentEntity2);

        adEntity = new AdEntity(1, 100, "someAd",
                "someDescriptionToAdd", userEntity, imageEntity, commentEntityList);
    }

    @Test
    void addUpdateComment() {
        CreateOrUpdateComment createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("comment3");

        Comment comment3 = Comment.builder()
                .adId(1)
                .author(1)
                .authorFirstName("testUser")
                .pk(3)
                .text("comment3")
                .authorImage("filePath")
                .createdAt(time.toString())
                .build();

        when(userAuthentication.getCurrentUser()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> commentService.addComment(1, createOrUpdateComment));
        assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(1,1, createOrUpdateComment));

        when(userAuthentication.getCurrentUser()).thenReturn(userEntity);
        when(adRepository.findById(anyInt())).thenReturn(Optional.of(adEntity));

        CommentEntity commentEntity3 = new CommentEntity(3, "comment3", time, userEntity, adEntity);
        when(commentMapper.createOrUpdateCommentToCommentEntity(createOrUpdateComment)).thenReturn(commentEntity3);
        when(commentRepository.save(Mockito.any(CommentEntity.class))).thenReturn(null);
        when(commentMapper.commentEntityToComment(Mockito.any(CommentEntity.class))).thenReturn(comment3);
        assertThat(commentService.addComment(1,createOrUpdateComment)).isEqualTo(comment3);
        assertThat(commentService.updateComment(1,1,createOrUpdateComment)).isEqualTo(comment3);

        adEntity.setCommentEntities(null);
        assertThrows(NullPointerException.class, () -> commentService.updateComment(1,1, createOrUpdateComment));

    }

    @Test
    void getComments() {
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment1);
        commentList.add(comment2);

        when(adRepository.findById(anyInt())).thenReturn(Optional.of(adEntity));
        when(commentMapper.commentEntityListToCommentList(List.copyOf(commentEntityList))).thenReturn(commentList);
        assertThat(commentService.getComments(1).getCount()).isEqualTo(2);

        when(adRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> commentService.getComments(1));
    }

    @Test
    void deleteComment() {

        when(userAuthentication.getCurrentUser()).thenReturn(userEntity);
        when(commentRepository.existsById(anyInt())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(1,1));


        commentEntityList = new ArrayList<>();
        imageEntity = new ImageEntity(1,"path",1, MediaType.IMAGE_JPEG_VALUE);
        when(adRepository.findById(anyInt())).thenReturn(Optional.of(adEntity));
        userEntity = new UserEntity(2, "testUser", "password", "firstName",
                "lastName", "999", Role.USER, null, commentEntityList, imageEntity);
        when(userAuthentication.getCurrentUser()).thenReturn(userEntity);
        assertThrows(EntityForbiddenException.class, () -> commentService.deleteComment(1,1));
    }
}