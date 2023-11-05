package ru.skypro.homework.service.impl;

import liquibase.pro.packaged.F;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.UserAuthentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

//    @Test
//    void uploadAdImage() {
//        Integer adPk = 1;
//        byte[] image = {1,0,1};
//        MultipartFile multipartFile = new MockMultipartFile("file", image);
//
//        when(out.uploadAdImage(adPk, multipartFile)).thenReturn(image);
//
//    }

    @Test
    void incorrectUploadAdImage() {
        assertThrows(RuntimeException.class, () -> {
            doThrow().when(imageService).uploadAdImage(anyInt(), null);
        });
    }

//    @Test
//    void getImage() throws IOException {
//        ImageService imageService = mock(ImageService.class);
//        ImageEntity image = ImageEntity.builder()
//                .filePath("/Users/grigoriirarog/Pictures/image/ru.skypro.homework.entity.AdEntity@1b73aea7.jpg")
//                .fileSize(18006)
//                .mediaType("image/jpeg")
//                .build();
//        byte[] imageBytes = {1, 0, 1};
//        Optional<ImageEntity> optionalImage = Optional.of(image);
//
//        when(imageRepository.findById(anyInt())).thenReturn(optionalImage);
//        Optional<ImageEntity> optionalImage1 = imageRepository.findById(anyInt());
//        ImageEntity image1 = optionalImage1.get();
//
//        Path imagePath = Path.of(image1.getFilePath());
//        byte[] bytes = Files.readAllBytes(imagePath);
//
//        when(imageService.getImage(anyInt())).thenReturn(bytes);
//        byte[] imageBytes1 = imageService.getImage(anyInt());
//        assertEquals(imageBytes, imageBytes1);
//    }
}