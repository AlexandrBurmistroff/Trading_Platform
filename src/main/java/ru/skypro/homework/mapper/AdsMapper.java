package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
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
        if (adEntity.getImageEntity() != null) {
            ad.setImage("/image/" + adEntity.getImageEntity().getId());
        }
        ad.setPk(adEntity.getPk());
        ad.setPrice(adEntity.getPrice());
        ad.setTitle(adEntity.getTitle());
        return ad;
    };

    List<Ad> adEntityListToAdList(List<AdEntity> adEntityList);

    default ExtendedAd adEntityToExtendedAd(AdEntity adEntity) {
        return ExtendedAd.builder()
                .pk(adEntity.getPk())
                .authorFirstName(adEntity.getUserEntity().getFirstName())
                .authorLastName(adEntity.getUserEntity().getLastName())
                .description(adEntity.getDescription())
                .email(adEntity.getUserEntity().getUsername())
                .image("/image/" + adEntity.getImageEntity().getId())
                .phone(adEntity.getUserEntity().getPhone())
                .price(adEntity.getPrice())
                .title(adEntity.getTitle())
                .build();
    };

    AdEntity createOrUpdateAdToAdEntity(CreateOrUpdateAd createOrUpdateAd);

}
