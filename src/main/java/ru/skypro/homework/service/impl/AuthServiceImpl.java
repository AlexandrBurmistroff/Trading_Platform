package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.util.UserAuthentication;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;

    private final UserAuthentication userAuthentication;

    @Override
    public boolean login(String userName, String password) {
        UserEntity userExistsCheck = userRepository.findByUsername(userName);
        if (userExistsCheck != null && userExistsCheck.getUsername().equals(userName)
                && userExistsCheck.getPassword().equals(password)) {
            UserDetails userDetails = manager.loadUserByUsername(userName);
            return encoder.matches(password, userDetails.getPassword());
        } else {
            return false;
        }
    }

    @Override
    public boolean register(Register register) {
        UserEntity userExistsCheck = userRepository.findByUsername(register.getUsername());
        if (userExistsCheck == null) {
            UserEntity newUserEntity = UserEntity.builder()
                    .username(register.getUsername())
                    .password(register.getPassword())
                    .firstName(register.getFirstName())
                    .lastName(register.getLastName())
                    .phone(register.getPhone())
                    .role(register.getRole())
                    .build();
            userRepository.save(newUserEntity);
        }

        if (manager.userExists(register.getUsername())) {
            return false;
        }
        manager.createUser(
                User.builder()
                        .passwordEncoder(this.encoder::encode)
                        .password(register.getPassword())
                        .username(register.getUsername())
                        .roles(register.getRole().name())
                        .build());
        return true;
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        UserEntity currentUserEntity = userAuthentication.getCurrentUserName();
        UserEntity foundedUser = userRepository.findByUsername(currentUserEntity.getUsername());

        foundedUser.setPassword(newPassword);
        userRepository.save(foundedUser);

        manager.changePassword(oldPassword, newPassword);
    }

}
