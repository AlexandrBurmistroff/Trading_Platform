package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

/**
 * Класс конвертирует модель UserEntity в User Dto и обратно.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    default User userEntityToUser(UserEntity userEntity) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setEmail(userEntity.getUsername());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setPhone(userEntity.getPhone());
        user.setRole(userEntity.getRole());
        if (userEntity.getImageEntity() != null) {
            user.setImage("/image/" + userEntity.getImageEntity().getId());
        }
        return user;
    };

}
