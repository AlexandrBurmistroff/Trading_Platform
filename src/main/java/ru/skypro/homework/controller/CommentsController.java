package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.service.CommentService;

import java.util.Optional;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
public class CommentsController {


    private final CommentService commentService;


    /**
     * Получение комментариев объявления.
     *
     * @param id - идентификатор объявления.
     * @return ResponseEntity.
     * Метод отправляет запрос на сервис в поисках объявления, если такое объявление
     * существует, то возвращает список комментариев к нему.
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer id) {
        Optional<Comments> comments = commentService.getComments(id);
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Comments());
    }

    /**
     * Добавление комментария к объявлению.
     * @param id - идентификатор объявления.
     * @param createOrUpdateComment - текст для нового комментария.
     * @return ResponseEntity.
     * Метод отправляет запрос на сервис добавить новый комментарий к объявлению.
     */

    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @RequestBody CreateOrUpdateComment createOrUpdateComment,
                                              Authentication authentication) {
        String email = authentication.getName();
        Optional<Comment> commentOptional = commentService.addComment(id, createOrUpdateComment, email);
        if (commentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(commentOptional.get());
    }

    /**
     * Удаление комментария.
     * @param adId      - идентификатор объявления.
     * @param commentId - идентификатор комментария.
     * @return ResponseEntity.
     * Метод отправляет запрос на сервис удалить конкретный комментарий к объявлению.
     */

    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId,
                                              @PathVariable Integer commentId) {
        commentService.deleteComment(adId, commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Обновление комментария.
     *
     * @param adId      - идентификатор объявления.
     * @param commentId - идентификатор комментария.
     * @param createOrUpdateComment - текст измененного комментария.
     * @return ResponseEntity.
     * Метод отправляет запрос на сервис изменить конкретный комментарий к объявлению.
     */

    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        Optional<Comment> comment = commentService.updateComment(adId, commentId, createOrUpdateComment);
        if (comment.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(comment.get());
    }
}