package ru.skypro.homework.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.UsersService;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class CommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentsService;

    @MockBean
    private AdsService adsService;

    @MockBean
    private AuthService authService;

    @MockBean
    private UsersService usersService;

    @InjectMocks
    private CommentsController commentsController;

    private Comment comment;

    private JSONObject createOrUpdateCommentObject;

    private CreateOrUpdateComment createOrUpdateComment;

    @BeforeEach
    void init() throws JSONException {
        comment = new Comment(
                1, "imagePath", "user@gmail.com",
                "date", 1, "text", 1);
        createOrUpdateCommentObject = new JSONObject();
        createOrUpdateCommentObject.put("text", "text");
        createOrUpdateComment = new CreateOrUpdateComment();
        createOrUpdateComment.setText("text");
    }


    @Test
    void getComments() throws Exception {

        Comments comments = new Comments();
        comments.setCount(1);
        comments.setResults(List.of(comment));

        when(commentsService.getComments(1)).thenReturn(comments);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        when(commentsService.getComments(2))
                .thenThrow(new EntityNotFoundException("Объявление не найдено"));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/2/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void addComment() throws Exception {

        when(commentsService.addComment(1, createOrUpdateComment)).thenReturn(comment);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/ads/1/comments")
                        .content(createOrUpdateCommentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("text"));

        when(commentsService.addComment(2, createOrUpdateComment))
                .thenThrow(new EntityNotFoundException("Объявление не найдено"));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/ads/2/comments")
                        .content(createOrUpdateCommentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment() throws Exception {

        when(commentsService.updateComment(1, 1, createOrUpdateComment)).thenReturn(comment);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/1/comments/1")
                        .content(createOrUpdateCommentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("text"));

        when(commentsService.updateComment(1, 2, createOrUpdateComment))
                .thenThrow(new EntityNotFoundException("Комментарий не найден"));
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/1/comments/2")
                        .content(createOrUpdateCommentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void deleteComment() throws Exception {
//        when(commentsService.deleteComment(1, 1)).thenReturn(true);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete("/ads/1/comments/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        when(commentsService.deleteComment(1, 2)).thenThrow(new EntityNotFoundException("Комментарий не найден"));
//        mockMvc.perform(MockMvcRequestBuilders
//                        .delete("/ads/1/comments/2")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
}