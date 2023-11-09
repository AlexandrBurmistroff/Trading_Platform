package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.exception.PasswordValidationException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UsersService;
import ru.skypro.homework.util.UserAuthentication;


/**
 * Сервис для обработки запроса от UsersController, обращений в БД
 * и возврата ответа в UsersController
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final UserMapper userMapper;
    private final UserAuthentication userAuthentication;
    private final PasswordEncoder encoder;

    /**
     * Метод, который сравнивает значения текущего пароля с новым
     * @param newPassword - новый пароль
     */
    @Override
    public void setPassword(NewPassword newPassword) {
        UserEntity user = userAuthentication.getCurrentUser();

        if (!encoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            throw new PasswordValidationException("Password validation failed");
        }
        String encodedNewPassword = encoder.encode(newPassword.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    /**
     * Метод, который ищет в БД пользователя по логину
     * @return данные пользователя
     */
    @Override
    public User getUser() {
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();
        if (currentUserEntity == null) {
            throw new EntityNotFoundException();
        }
        return userMapper.userEntityToUser(currentUserEntity);
    }

    /**
     * Метод, который обновляет значения пользователя
     * @param updateUser содержит новые значения пользователя
     * @return обновлённые данные пользователя
     */
    @Override
    public UpdateUser updateUser(UpdateUser updateUser) {
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();

        currentUserEntity.setFirstName(updateUser.getFirstName());
        currentUserEntity.setLastName(updateUser.getLastName());
        currentUserEntity.setPhone(updateUser.getPhone());
        userRepository.save(currentUserEntity);
        return updateUser;
    }

    /**
     * Метод, который меняет аватарку пользователя, и сохраняет в БД
     * @param file - значение нового файла
     */
    @Override
    public void updateUserImage(MultipartFile file) {
        imageService.uploadUserImage(file);
    }
}
