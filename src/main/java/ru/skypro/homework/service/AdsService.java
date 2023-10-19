package ru.skypro.homework.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

public interface AdsService {

    Ads getAllAds();

    Ad addAd(CreateOrUpdateAd properties, MultipartFile image);

    ExtendedAd getAds(Integer adPk);

    void removeAd(Integer adPk);

    Ad updateAds(Integer adPk, CreateOrUpdateAd createOrUpdateAd);

    Ads getAdsMe();

    byte[] updateImage(Integer adPk, MultipartFile file);

}
