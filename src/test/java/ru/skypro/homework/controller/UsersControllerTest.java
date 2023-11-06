package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.service.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private AdsService adsService;

    @MockBean
    private AuthService authService;

    @MockBean
    private CommentService commentsService;

    @MockBean
    ImageService imageService;

    @InjectMocks
    private UsersController usersController;

    @InjectMocks
    private AdsController adsController;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserEntity userEntity = new UserEntity();
    private NewPassword newPassword;
    private JSONObject jsonObjectPassword;

    @BeforeEach
    void setUp() throws JSONException {
        userEntity.setId(1);
        userEntity.setUsername("user1@gmail.com");
        userEntity.setPassword("password");
        userEntity.setFirstName("Alex");
        userEntity.setLastName("B");
        userEntity.setPhone("+79999999999");
        userEntity.setRole(Role.ADMIN);
        userEntity.setImageEntity(ImageEntity.builder()
                .id(1)
                .filePath("/image")
                .fileSize(1L)
                .mediaType("123")
                .build());

        jsonObjectPassword = new JSONObject();
        jsonObjectPassword.put("password", "password");
    }

    @Test
    void setPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObjectPassword.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUser() throws Exception {
        User user = new User(1, "user1@gmail.com", "Alex", "B",
                "+79999999999", Role.ADMIN, "image");


        when(usersService.getUser()).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser() throws Exception {
        User user = new User(1, "user1@gmail.com", "Alex", "B",
                "+79999999999", Role.ADMIN, "image");

        UpdateUser updateUser = new UpdateUser("Max", "P", "+79999999998");

        when(usersService.updateUser(updateUser)).thenReturn(updateUser);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserImage() throws Exception {
        MockPart image = new MockPart("image", "file", "image".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/users/me/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.addPart(image);
                            return request;
                        }))
                .andExpect(status().isOk());
    }
}