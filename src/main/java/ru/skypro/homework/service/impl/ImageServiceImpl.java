package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.UserAuthentication;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    @Value("${path.to.image.folder}")
    private String imageDir;

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;
    private final UserAuthentication userAuthentication;

    /**
     * Метод для сохраниения фото в БД
     * @param file файл фото
     * @throws IOException
     */
    @Override
    public void uploadUserImage(MultipartFile file) throws IOException {
        UserEntity user = userAuthentication.getCurrentUser();

        Path filePath = Path.of(imageDir, user + "." + getExtensions(file.getOriginalFilename()));
        imageStream(filePath, file);

        ImageEntity image = userRepository.findById(user.getId())
                .map(UserEntity::getImageEntity)
                .orElse(new ImageEntity());
        image.setFilePath(filePath.toString());
        image.setFileSize(file.getSize());
        image.setMediaType(file.getContentType());
        imageRepository.save(image);

        if (user.getImageEntity() == null) {
            user.setImageEntity(image);
            userRepository.save(user);
        }
    }

    @Override
    public byte[] uploadAdImage(Integer adPk, MultipartFile file) throws IOException {
        AdEntity ad = adRepository.getReferenceById(adPk);

        Path filePath = Path.of(imageDir, ad + "." + getExtensions(file.getOriginalFilename()));
        imageStream(filePath, file);

        ImageEntity image = adRepository.findById(adPk)
                .map(AdEntity::getImageEntity)
                .orElse(new ImageEntity());
        image.setFilePath(filePath.toString());
        image.setFileSize(file.getSize());
        image.setMediaType(file.getContentType());
        imageRepository.save(image);

        if (ad.getImageEntity() == null) {
            ad.setImageEntity(image);
            adRepository.save(ad);
        }
        return getImage(image.getId());
    }

    @Override
    public byte[] getImage(Integer id) {
        Optional<ImageEntity> foundedImage = imageRepository.findById(id);
        if (foundedImage.isEmpty()) {
            throw new EntityNotFoundException("Image not found");
        } else {
            ImageEntity image = foundedImage.get();
            Path imagePath = Path.of(image.getFilePath());
            try {
                return Files.readAllBytes(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void imageStream(Path filePath, MultipartFile file) throws IOException{
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (
                InputStream is = file.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }
    }

    /**
     * Метод для переименовывания файла
     * @param fileName текущее название файла
     * @return переименованное название файла
     */
    private String getExtensions(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
