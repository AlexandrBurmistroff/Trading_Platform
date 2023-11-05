package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.UserAuthentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private UserAuthentication userAuthentication;

    @InjectMocks
    ImageServiceImpl imageService;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void correctUploadUserImage() {
        ImageService imageService = mock(ImageService.class);
        byte[] image = {1, 0, 1};
        MultipartFile multipartFile = new MockMultipartFile("file", image);

        ArgumentCaptor<MultipartFile> captor = ArgumentCaptor.forClass(MultipartFile.class);
        doNothing().when(imageService).uploadUserImage(captor.capture());
        imageService.uploadUserImage(multipartFile);
        assertEquals(multipartFile, captor.getValue());
    }

    @Test
    void incorrectUploadUserImage() {
        assertThrows(RuntimeException.class, () -> {
            doThrow().when(imageService).uploadUserImage(null);
        });
    }

    @Test
    void incorrectUploadAdImage() {
        assertThrows(RuntimeException.class, () -> {
            doThrow().when(imageService).uploadAdImage(anyInt(), null);
        });
    }
}