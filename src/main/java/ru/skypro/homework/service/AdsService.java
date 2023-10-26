package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.config.CanDeleteOrUpdate;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

public interface AdsService {

    Ads getAllAds();

    Ad addAd(CreateOrUpdateAd properties, MultipartFile image);

    ExtendedAd getAds(Integer adPk);

    @CanDeleteOrUpdate
    void removeAd(Integer adPk);

    @CanDeleteOrUpdate
    Ad updateAds(Integer adPk, CreateOrUpdateAd createOrUpdateAd);

    Ads getAdsMe();

    @CanDeleteOrUpdate
    byte[] updateImage(Integer adPk, MultipartFile file);

}
