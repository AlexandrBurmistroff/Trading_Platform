package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final UserEntity userEntity = new UserEntity();

    @BeforeEach
    void setUp() {
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
                .data("image".getBytes())
                .build());
        userRepository.save(userEntity);
    }

//    @AfterEach
//    void tearDown() {
//        userRepository.delete(userEntity);
//    }

    @Test
    void setPassword() {
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", password = "password")
    void getUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/me"))

                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", password = "password")
    void updateUser() throws Exception {
        userEntity.setFirstName("Max");
        userEntity.setLastName("P");
        userEntity.setPhone("+79999999998");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEntity)))

                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", password = "password")
    void updateUserImage() throws Exception {
        MockPart image = new MockPart("image", "file", "image".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/me/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.addPart(image);
                            return request;
                        }))
                .andExpect(status().isOk());
    }
}