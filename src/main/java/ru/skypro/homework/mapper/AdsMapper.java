package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;
import ru.skypro.homework.entity.AdEntity;

import java.util.List;

/**
 * Класс конвертирует модель AdEntity в Ad Dto и обратно.
 */
@Mapper(componentModel = "spring")
public interface AdsMapper {

    default Ad adEntityToAd(AdEntity adEntity) {
        Ad ad = new Ad();
        ad.setAuthor(adEntity.getUserEntity().getId());
        ad.setImage("/image/" + adEntity.getImageEntity().getId());
        ad.setPk(adEntity.getPk());
        ad.setPrice(adEntity.getPrice());
        ad.setTitle(adEntity.getTitle());
        return ad;
    };

    List<Ad> adEntityListToAdList(List<AdEntity> adEntityList);

    default ExtendedAd adEntityToExtendedAd(AdEntity adEntity) {
        ExtendedAd extendedAd = new ExtendedAd();
        extendedAd.setPk(adEntity.getPk());
        extendedAd.setAuthorFirstName(adEntity.getUserEntity().getFirstName());
        extendedAd.setAuthorLastName(adEntity.getUserEntity().getLastName());
        extendedAd.setDescription(adEntity.getDescription());
        extendedAd.setEmail(adEntity.getUserEntity().getUsername());
        extendedAd.setImage("/image/" + adEntity.getImageEntity().getId());
        extendedAd.setPhone(adEntity.getUserEntity().getPhone());
        extendedAd.setPrice(adEntity.getPrice());
        extendedAd.setTitle(adEntity.getTitle());
        return extendedAd;
    };

    AdEntity createOrUpdateAdToAdEntity(CreateOrUpdateAd createOrUpdateAd);


}
