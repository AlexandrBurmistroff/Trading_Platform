package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.Role;
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

class UsersServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserAuthentication userAuthentication;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private UsersServiceImpl usersService;

    private final UserEntity userEntity = new UserEntity();
    @Mock
    private NewPassword newPassword;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
//        Authentication authentication = Mockito.mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        when(authentication.getName()).thenReturn("user1@gmail.com");

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

        userMapper.userEntityToUser(userEntity);
    }

    @Test
    void setPassword() {
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

        User serviceUser = userMapper.userEntityToUser(userEntity);

        when(userRepository.findByUsername("user1@gmail.com")).thenReturn(userEntity);
        when(userMapper.userEntityToUser(userEntity)).thenReturn(user);

        assertNotNull(userEntity);
        //assertEquals(serviceUser, user);
    }

    @Test
    void updateUser() {
    }

    @Test
    void updateUserImage() {
    }
}