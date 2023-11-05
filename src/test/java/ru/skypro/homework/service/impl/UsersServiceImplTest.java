package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsersServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UsersServiceImpl usersService;

    private final UserEntity userEntity = new UserEntity();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("user1@gmail.com");

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
        userRepository.save(userEntity);
    }

    @Test
    void setPassword() {
    }

    @Test
    @WithMockUser(username = "user1@gmail.com", password = "password")
    void getUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("user1@gmail.com");
        user.setFirstName("Alex");
        user.setLastName("B");
        user.setPhone("+79999999999");
        user.setRole(Role.ADMIN);
        user.setImage("image");

        when(userRepository.findByUsername("user1@gmail.com")).thenReturn(userEntity);
        when(usersService.getUser()).thenReturn(user);
        when(userMapper.userEntityToUser(userEntity)).thenReturn(user);

        assertNotNull(usersService.getUser());
        assertEquals(usersService.getUser(), user);
        //assertThrows(EntityNotFoundException.class);

//        verify(userMapper.userEntityToUser((any(UserEntity.class))), timeout(1));
    }

    @Test
    void updateUser() {
    }

    @Test
    void updateUserImage() {
    }
}