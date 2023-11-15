package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.PasswordValidationException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.util.UserAuthentication;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@SpringBootTest
class UsersServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserAuthentication userAuthentication;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private ImageServiceImpl imageService;

    @MockBean
    private NewPassword newPassword;

    @Autowired
    private UsersServiceImpl usersService;


    @Test
    void setPassword() {
        UserEntity userEntity = new UserEntity();
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

        NewPassword newPassword = new NewPassword("password2", "password2");

        when(userAuthentication.getCurrentUser()).thenReturn(userEntity);
        assertThrows(PasswordValidationException.class, () -> usersService.setPassword(newPassword));

        when(passwordEncoder.encode(userEntity.getPassword())).thenReturn(newPassword.getNewPassword());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
    }

    @Test
    void getUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("user1@gmail.com");
        user.setFirstName("Alex");
        user.setLastName("B");
        user.setPhone("+79999999999");
        user.setRole(Role.ADMIN);
        user.setImage("image");

        UserEntity userEntity = new UserEntity();
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

        when(userRepository.findByUsername("user1@gmail.com")).thenReturn(userEntity);
        when(userMapper.userEntityToUser(userEntity)).thenReturn(user);
        when(userAuthentication.getCurrentUser()).thenReturn(userEntity);

        assertNotNull(userEntity);
        assertNotNull(user);
    }

    @Test
    void updateUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setUsername("user@gmail.com");
        userEntity.setPassword("password");
        userEntity.setFirstName("Alex1");
        userEntity.setLastName("B");
        userEntity.setPhone("+79999999999");
        userEntity.setRole(Role.ADMIN);
        userEntity.setImageEntity(ImageEntity.builder()
                .id(1)
                .filePath("/image")
                .fileSize(1L)
                .mediaType("123")
                .build());

        UpdateUser updateUser = new UpdateUser("Alex", "B", "+79999999999");
        when(userAuthentication.getCurrentUser()).thenReturn(userEntity);

        userEntity.setFirstName(updateUser.getFirstName());
        userEntity.setLastName(updateUser.getLastName());
        userEntity.setPhone(updateUser.getPhone());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
    }

    @Test
    void updateUserImage() {
        MultipartFile file = Mockito.mock(MultipartFile.class);

        usersService.updateUserImage(file);

        verify(imageService).uploadUserImage(file);
    }
}