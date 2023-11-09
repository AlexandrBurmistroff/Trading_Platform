package ru.skypro.homework.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AdsService adsService;

    @MockBean
    private CommentService commentsService;

    @MockBean
    private ImageService imageService;

    @MockBean
    private UsersService usersService;

    @InjectMocks
    private AuthController authController;

    @InjectMocks
    private AdsController adsController;

    @InjectMocks
    private CommentsController commentsController;

    @InjectMocks
    private ImageController imageController;

    @InjectMocks
    private UsersController usersController;

    private JSONObject jsonObject;

    @Test
    void login() throws Exception {
        jsonObject = new JSONObject();
        jsonObject.put("username", "user1@gmail.com");
        jsonObject.put("password", "password");

        when(authService.login("user1@gmail.com", "password")).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void register() throws Exception {
        Register register = new Register("user1@gmail.com", "password",
                "Alex", "B", "+79999999999", Role.ADMIN);

        jsonObject = new JSONObject();
        jsonObject.put("username", "user1@gmail.com");
        jsonObject.put("password", "password");
        jsonObject.put("firstName", "Alex");
        jsonObject.put("lastName", "B");
        jsonObject.put("phone", "+79999999999");
        jsonObject.put("role", "ADMIN");

        when(authService.register(register)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}