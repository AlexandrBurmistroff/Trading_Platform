package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;


public interface ImageService {

    void uploadUserImage(MultipartFile file);

    byte[] uploadAdImage(Integer adPk, MultipartFile file);

    byte[] getImage(Integer id);

}
