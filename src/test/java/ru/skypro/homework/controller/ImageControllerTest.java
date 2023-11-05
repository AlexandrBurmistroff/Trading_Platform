package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.service.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private ImageService imageService;
    @MockBean
    private AdsService adsService;
    @MockBean
    private AuthService authService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private UsersService usersService;
    @InjectMocks
    private ImageController imageController;

    @Test
    void getImage() throws Exception {
        byte[] imageBytes = new byte[] {(byte) 129, (byte) 130, (byte) 131};

        when(imageService.getImage(2)).thenReturn(imageBytes);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/image/2")
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .accept(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(imageBytes));
        verify(imageService).getImage(2);
    }

}