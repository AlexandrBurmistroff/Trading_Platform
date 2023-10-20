package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.UsersService;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    /**
     * Метод для обновления пароля.
     *
     * @param newPassword принимает новый пароль от пользователя.
     * @return статус 200, если новый пароль не совпадает с текущим паролем и сохранился в БД.
     */
    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody NewPassword newPassword) { // TODO: 14.10.2023 требуется дороботка
        usersService.setPassword(newPassword);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Метод для получения всей информации об авторизованном пользователе.
     *
     * @return всю информацию о пользователе из БД.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getUser() {
        return new ResponseEntity<>(usersService.getUser(), HttpStatus.OK);
    }

    /**
     * Метод для обновления информации о пользователе.
     *
     * @param updateUser принимает новые данные пользователя.
     * @return обновлённые данные пользователя.
     */
    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser) {
        return new ResponseEntity<>(usersService.updateUser(updateUser), HttpStatus.OK);
    }

    /**
     * Метод для обновления аватарки пользователя.
     *
     * @param file принимает файл аватарки пользователя.
     * @return статус 200, если аватарка успешно обновлена.
     */
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(@RequestParam("image") MultipartFile file) {
        usersService.updateUserImage(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
