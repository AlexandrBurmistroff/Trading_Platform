package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.config.CustomUserDetailsServiceImpl;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthServiceImplTest {

    @MockBean
    private CustomUserDetailsServiceImpl userDetailsService;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Test
    void login() {
        String loginUser = "user1@gmail.com";
        String passwordUser = "password";

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(loginUser)).thenReturn(userDetails);
        encoder.matches(passwordUser, userDetails.getPassword());

        verify(encoder).matches(passwordUser, userDetails.getPassword());
    }

    @Test
    void register() {
        Register register = new Register("user1@gmail.com", "password",
                "Alex", "B", "+79999999999", Role.ADMIN);

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

        when(userRepository.findByUsername(register.getUsername())).thenReturn(userEntity);

        userRepository.save(userEntity);
        verify(userRepository).save(any(UserEntity.class));
    }
}