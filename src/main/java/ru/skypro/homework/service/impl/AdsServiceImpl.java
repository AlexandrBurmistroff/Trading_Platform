package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.EntityForbiddenException;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.UserAuthentication;


import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final AdsMapper adsMapper;
    private final UserAuthentication userAuthentication;

    /**
     * Метод получает все объявления из БД и конвертирует их в Ads Dto.
     *
     * @return возвращает ResponsEntity status с Ads Dto.
     */
    @Override
    public Ads getAllAds() {
        List<AdEntity> adEntityList = adRepository.findAll();
        List<Ad> adList = adsMapper.adEntityListToAdList(adEntityList);

        return Ads.builder()
                .count(adList.size())
                .results(adList)
                .build();
    }

    /**
     * Метод добавляет новое объявление в БД.
     *
     * @param properties CreateOrUpdateAd DTO. Включает title, price и description объявления.
     * @param image      принимает изображение объявления.
     * @return возвращает ResponsEntity status с Ad Dto.
     */
    @Override
    public Ad addAd(CreateOrUpdateAd properties, MultipartFile image) {
        AdEntity newAdEntity = adsMapper.createOrUpdateAdToAdEntity(properties);
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();

        if (currentUserEntity == null) {
            throw new EntityNotFoundException();
        } else {
            newAdEntity.setUserEntity(currentUserEntity);
            AdEntity savedAdEntity = adRepository.save(newAdEntity);

            try {
                imageService.uploadAdImage(savedAdEntity.getPk(), image);
            } catch (IOException e) {
                log.error("Image not uploaded");
            }
            return adsMapper.adEntityToAd(savedAdEntity);
        }
    }

    /**
     * Метод получает из БД информацию об объявлении по id объявления.
     *
     * @param adPk идентификатор объявления в БД.
     * @return возвращает ResponsEntity status с ExtendedAd Dto.
     */
    @Override
    public ExtendedAd getAds(Integer adPk) {
        Optional<AdEntity> checkForExistAd = adRepository.findById(adPk);

        if (checkForExistAd.isEmpty()) {
            log.error("Ad not founded");
            throw new EntityNotFoundException();
        } else {
            AdEntity foundedAdEntity = checkForExistAd.get();
            return adsMapper.adEntityToExtendedAd(foundedAdEntity);
        }
    }


    /**
     * Метод удаляет объявление из БД по id объявления.
     *
     * @param adPk идентификатор объявления в БД.
     * @return возвращает ResponsEntity status.
     */
    @Override
    public void removeAd(Integer adPk) {
        Optional<AdEntity> checkForExistAd = adRepository.findById(adPk);
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();
        if (checkForExistAd.isEmpty() && currentUserEntity == null) {
            log.error("Ad not founded");
            throw new EntityNotFoundException();
        } else {
            if (currentUserEntity.getId().equals(checkForExistAd.get().getUserEntity().getId()) ||
            currentUserEntity.getRole().equals(Role.ADMIN)) {
                adRepository.deleteById(adPk);
            } else {
                throw new EntityForbiddenException();
            }
        }
    }

    /**
     * Метод обновляет информацию об объявлении в БД по id объявления.
     *
     * @param adPk             идентификатор объявления в БД.
     * @param createOrUpdateAd DTO. Включает title, price и description объявления.
     * @return возвращает ResponsEntity status с Ad Dto.
     */
    @Override
    public Ad updateAds(Integer adPk, CreateOrUpdateAd createOrUpdateAd) {
        Optional<AdEntity> checkForExistAd = adRepository.findById(adPk);
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();

        if (checkForExistAd.isEmpty() && currentUserEntity == null) {
            log.error("Ad not founded");
            throw new EntityNotFoundException();
        } else {
            AdEntity foundedAdEntity = checkForExistAd.get();
            if (currentUserEntity.getId().equals(checkForExistAd.get().getUserEntity().getId()) ||
                    currentUserEntity.getRole().equals(Role.ADMIN)) {
                AdEntity updatedAdEntity = adsMapper.createOrUpdateAdToAdEntity(createOrUpdateAd);
                updatedAdEntity.setPk(foundedAdEntity.getPk());
                updatedAdEntity.setUserEntity(foundedAdEntity.getUserEntity());
                updatedAdEntity.setImageEntity(foundedAdEntity.getImageEntity());
                adRepository.save(updatedAdEntity);
                return adsMapper.adEntityToAd(updatedAdEntity);
            } else {
                throw new EntityForbiddenException();
            }
        }
    }

    /**
     * Метод возвращает все объявления авторизованного пользователя.
     *
     * @return возвращает ResponsEntity status с Ads Dto.
     */
    @Override
    @Transactional
    public Ads getAdsMe() {
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();

        Collection<AdEntity> adEntityList = userRepository.findById(currentUserEntity.getId())
                .map(UserEntity::getAdEntities)
                .orElse(null);
        List<Ad> adList = adsMapper.adEntityListToAdList((List<AdEntity>) adEntityList);
        Integer sizeList = adList.size();

        Ads ads = new Ads();
        ads.setCount(sizeList);
        ads.setResults(adList);
        return ads;
    }

    /**
     * Метод обновляет в БД картинку объявления по id объявления.
     *
     * @param adPk идентификатор объявления в БД.
     * @param file изображение.
     * @return возвращает ResponsEntity status с byte[] (бинарным кодом изображения).
     */
    @Override
    public byte[] updateImage(Integer adPk, MultipartFile file) {
        Optional<AdEntity> checkForExistAd = adRepository.findById(adPk);
        UserEntity currentUserEntity = userAuthentication.getCurrentUser();
        if (checkForExistAd.isEmpty() && currentUserEntity == null) {
            log.error("Ad not founded");
            throw new EntityNotFoundException();
        } else {
            if (currentUserEntity.getId().equals(checkForExistAd.get().getUserEntity().getId()) ||
                    currentUserEntity.getRole().equals(Role.ADMIN)) {
                byte[] image = new byte[0];
                try {
                    image = imageService.uploadAdImage(adPk, file);
                } catch (IOException e) {
                    log.error("Image not uploaded");
                }
                return image;
            } else {
                throw new EntityForbiddenException();
            }
        }
    }

}
