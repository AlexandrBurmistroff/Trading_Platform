package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.config.CustomUserDetailsServiceImpl;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;

/**
 * Сервис для регистрации и аутентификации пользователя
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomUserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    /**
     *                 Метод для аутентификации пользователя (процесс проверки подлинности пользователя,
     *                 чтобы убедиться, что он является тем, за кого себя выдает)
     * @param userName логин пользователя
     * @param password пароль пользователя
     * @return         true, если пользователь ввёл верные данные
     *                 false, если пользователь ввёл неверные данные
     */
    @Override
    public boolean login(String userName, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if (encoder.matches(password, userDetails.getPassword())) {
            return true;
        } else {
            log.error("Invalid user password {} already exist", password);
            return false;
        }
    }

    /**
     *                 Метод для регистрации нового пользователя
     * @param register регистрационные данные нового пользователя
     * @return         true, если новый пользователь ввёл верные данные и сохранился в БД,
     *                 false, если такой пользователь уже был зарегистрирован.
     */
    @Override
    public boolean register(Register register) {
        UserEntity userExistsCheck = userRepository.findByUsername(register.getUsername());
        if (userExistsCheck == null) {
            UserEntity newUserEntity = UserEntity.builder()
                    .username(register.getUsername())
                    .password(encoder.encode(register.getPassword()))
                    .firstName(register.getFirstName())
                    .lastName(register.getLastName())
                    .phone(register.getPhone())
                    .role(register.getRole())
                    .build();
            userRepository.save(newUserEntity);
            System.out.println(newUserEntity);
            return true;
        } else {
            log.error("User with email {} already exist", register.getUsername());
            return  false;
        }
    }

}
