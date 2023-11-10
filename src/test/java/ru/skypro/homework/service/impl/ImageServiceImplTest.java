package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.util.UserAuthentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ImageServiceImplTest {

    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AdRepository adRepository;
    @MockBean
    private UserAuthentication userAuthentication;

    @Autowired
    private ImageServiceImpl imageService;

    private CommentEntity commentEntity1;
    private CommentEntity commentEntity2;
    private LocalDateTime time;
    private List<CommentEntity> commentEntityList;
    private UserEntity userEntity;
    private AdEntity adEntity;
    private List<AdEntity> adEntityList;

    @BeforeEach
    void init() {

        ImageEntity image = mock(ImageEntity.class);

        time = LocalDateTime.now();

        userEntity = new UserEntity(1, "testUser", "password", "firstName",
                "lastName", "999", Role.USER, adEntityList, commentEntityList, image);

        commentEntity1 = new CommentEntity(1, "comment1", time, userEntity, adEntity);
        commentEntity2 = new CommentEntity(2, "comment2", time, userEntity, adEntity);

        commentEntityList = new ArrayList<>();
        commentEntityList.add(commentEntity1);
        commentEntityList.add(commentEntity2);

        adEntity = new AdEntity(1, 100, "someAd",
                "someDescriptionToAdd", userEntity, image, commentEntityList);

        adEntityList = new ArrayList<>();
        adEntityList.add(adEntity);
    }

    @Test
    void correctUploadUserImage() {
        ImageEntity image = mock(ImageEntity.class);
        byte[] imageBytes = {1,0,1};
        MultipartFile multipartFile = mock(MockMultipartFile.class);
        multipartFile = new MockMultipartFile("file", imageBytes);
        Optional<ImageEntity> optionalImage = Optional.of(image);

        when(userAuthentication.getCurrentUser()).thenReturn(userEntity);
        when(imageRepository.findById(any())).thenReturn(optionalImage);
        when(imageRepository.save(any(ImageEntity.class))).thenReturn(image);

        Optional<ImageEntity> optionalImage1 = imageRepository.findById(anyInt());
        assertEquals(optionalImage, optionalImage1);

        imageService.uploadUserImage(multipartFile);

        verify(userAuthentication).getCurrentUser();
        verify(imageRepository).findById(anyInt());
        verify(imageRepository).save(any(ImageEntity.class));
    }

    @Test
    void incorrectUploadUserImage() {
        assertThrows(RuntimeException.class, () -> {
            doThrow().when(imageService).uploadUserImage(null);
        });
    }

    @Test
    void uploadAdImage() throws IOException {
        ImageEntity image = ImageEntity.builder()
                .filePath("./data/image_test.webp")
                .fileSize(42698)
                .mediaType("image/webp")
                .build();
        AdEntity ad = new AdEntity(1, 100, "someAd",
                "someDescriptionToAdd", userEntity, image, commentEntityList);

        Path imagePath = Path.of(image.getFilePath());
        byte[] imageBytes = Files.readAllBytes(imagePath);
        Optional<ImageEntity> optionalImage = Optional.of(image);

        MultipartFile multipartFile = mock(MockMultipartFile.class);
        multipartFile = new MockMultipartFile("file", imageBytes);

        when(adRepository.getReferenceById(anyInt())).thenReturn(ad);
        when(imageRepository.findById(any())).thenReturn(optionalImage);
        when(imageRepository.save(any(ImageEntity.class))).thenReturn(image);

        Optional<ImageEntity> optionalImage1 = imageRepository.findById(anyInt());
        assertEquals(optionalImage, optionalImage1);

        byte[] foundedImageBytes = imageService.uploadAdImage(ad.getPk(), multipartFile);

        verify(adRepository).getReferenceById(anyInt());
        verify(imageRepository).findById(anyInt());
        verify(imageRepository).save(any(ImageEntity.class));
        assertArrayEquals(imageBytes, foundedImageBytes);
    }

    @Test
    void incorrectUploadAdImage() {
        assertThrows(RuntimeException.class, () -> {
            doThrow().when(imageService).uploadAdImage(anyInt(), null);
        });
    }

    @Test
    void correctGetImage() throws IOException {
        ImageEntity image = ImageEntity.builder()
                .filePath("./data/image_test.webp")
                .fileSize(42698)
                .mediaType("image/webp")
                .build();
        Path imagePath = Path.of(image.getFilePath());
        byte[] imageBytes = Files.readAllBytes(imagePath);
        Optional<ImageEntity> optionalImage = Optional.of(image);

        when(imageRepository.findById(any())).thenReturn(optionalImage);

        byte[] foundedImageBytes = imageService.getImage(1);

        verify(imageRepository).findById(any());
        assertArrayEquals(imageBytes, foundedImageBytes);
    }

    @Test
    void incorrectGetImage() {
        assertThrows(RuntimeException.class, () -> {
            doThrow().when(imageService).getImage(null);
        });
    }
}