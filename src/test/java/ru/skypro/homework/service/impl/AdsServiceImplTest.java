package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.exception.EntityNotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.util.UserAuthentication;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class AdsServiceImplTest {

    @Mock
    private AdRepository adRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdsMapper adsMapper;
    @Mock
    private ImageService imageService;
    @Mock
    private UserAuthentication userAuthentication;
    @Mock
    private ImageEntity imageEntity;

    private CreateOrUpdateAd createOrUpdateAd;

    private AdEntity adEntity;
    private AdEntity adEntity1;
    private AdEntity adEntity2;
    private UserEntity userEntity;
    private LocalDateTime time;
    private CommentEntity commentEntity;
    private Collection<CommentEntity> commentEntities;
    private List<CommentEntity> commentEntityList;

    @InjectMocks
    private AdsServiceImpl adsService;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);

        time = LocalDateTime.now();

        adEntity1 = new AdEntity(1, 10, "title1", "description1", userEntity, imageEntity, commentEntities);
        adEntity2 = new AdEntity(2, 20, "title2", "description2", userEntity, imageEntity, commentEntities);

        List<AdEntity> adEntityList = new ArrayList<>();
        adEntityList.add(adEntity1);
        adEntityList.add(adEntity2);


        userEntity = new UserEntity(1, "testUser", "password", "firstName",
                "lastName", "999", Role.USER, null, commentEntityList, imageEntity);

        commentEntity = new CommentEntity(1, "comment1", time, userEntity, adEntity);
    }

    @Test
    void getAllAds() {

        List<AdEntity> adEntityList = new ArrayList<>();
        adEntityList.add(adEntity1);
        adEntityList.add(adEntity2);

        when(adRepository.findAll()).thenReturn(adEntityList);

        Ads ads = new Ads();
        ads.setCount(2);

        List<Ad> adList = new ArrayList<>();
        Ad ad1 = new Ad();
        ad1.setPk(1);
        ad1.setTitle("Ad 1");
        adList.add(ad1);

        Ad ad2 = new Ad();
        ad2.setPk(2);
        ad2.setTitle("Ad 2");
        adList.add(ad2);

        ads.setResults(adList);

        when(adRepository.findAll()).thenReturn(adEntityList);

        when(adsMapper.adEntityListToAdList(adEntityList)).thenReturn(adList);

        assertThat(adsService.getAllAds()).isEqualTo(ads);

    }

    @Test
    void addAd() throws IOException {

        CreateOrUpdateAd createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle("Test Ad");
        createOrUpdateAd.setPrice(100);
        createOrUpdateAd.setDescription("Test description");

        MultipartFile image = new MockMultipartFile("test.jpg", new byte[10]);

        AdEntity adEntity = new AdEntity();
        adEntity.setPk(1);
        adEntity.setTitle("Test Ad");
        adEntity.setPrice(100);
        adEntity.setDescription("Test description");

        when(adsMapper.createOrUpdateAdToAdEntity(any(CreateOrUpdateAd.class))).thenReturn(adEntity);

        UserEntity currentUserEntity = new UserEntity();
        currentUserEntity.setId(1);

        when(userAuthentication.getCurrentUser()).thenReturn(currentUserEntity);

        AdEntity savedAdEntity = new AdEntity();
        savedAdEntity.setPk(1);
        savedAdEntity.setTitle("Test Ad");
        savedAdEntity.setPrice(100);
        savedAdEntity.setDescription("Test description");
        when(adRepository.save(any(AdEntity.class))).thenReturn(savedAdEntity);

        when(imageService.uploadAdImage(anyInt(), any(MultipartFile.class))).thenReturn(new byte[10]);

        Ad ad = new Ad(1,"image", 1, 100, "Test Ad");

        when(adsMapper.adEntityToAd(savedAdEntity)).thenReturn(ad);
        assertThat(adsService.addAd(createOrUpdateAd,image)).isEqualTo(ad);

        when(userAuthentication.getCurrentUser()).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> {
            adsService.addAd(createOrUpdateAd,image);
        });


    }


    @Test
    void removeAd() {
        Integer adPk = 1;

        when(adRepository.findById(adPk)).thenReturn(Optional.empty());

        when(userAuthentication.getCurrentUser()).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            adsService.removeAd(adPk);
        });

        AdEntity foundAdEntity = new AdEntity();
        foundAdEntity.setPk(adPk);
        UserEntity currentUserEntity = new UserEntity();
        currentUserEntity.setId(1);
        when(adRepository.findById(adPk)).thenReturn(Optional.of(foundAdEntity));
        when(userAuthentication.getCurrentUser()).thenReturn(currentUserEntity);

        assertThrows(NullPointerException.class, () -> {
            adsService.removeAd(adPk);
        });
    }

    @Test
    void updateAds() {
        AdEntity adEntity = new AdEntity();
        adEntity.setPk(1);
        adEntity.setTitle("Test Ad");
        adEntity.setPrice(100);
        adEntity.setDescription("Test description");
        adEntity.setUserEntity(userEntity);

        when(adRepository.findById(1)).thenReturn(Optional.of(adEntity));

        UserEntity currentUserEntity = new UserEntity();
        currentUserEntity.setId(1);

        when(userAuthentication.getCurrentUser()).thenReturn(currentUserEntity);

        when(adsMapper.createOrUpdateAdToAdEntity(any(CreateOrUpdateAd.class))).thenReturn(adEntity);

        AdEntity savedAdEntity = new AdEntity();
        savedAdEntity.setPk(1);
        savedAdEntity.setTitle("Test Ad");
        savedAdEntity.setPrice(100);
        savedAdEntity.setDescription("Test description");
        savedAdEntity.setUserEntity(userEntity);
        when(adRepository.save(any(AdEntity.class))).thenReturn(savedAdEntity);

        Ad ad = new Ad(1,"image", 1, 100, "Test Ad");
        when(adsMapper.adEntityToAd(savedAdEntity)).thenReturn(ad);


        CreateOrUpdateAd createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle("Test Ad");
        createOrUpdateAd.setPrice(100);
        createOrUpdateAd.setDescription("Test description");

        assertThat(adsService.updateAds(1,createOrUpdateAd)).isEqualTo(ad);

        when(userAuthentication.getCurrentUser()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> {
            adsService.updateAds(1,createOrUpdateAd);
        });
    }

    @Test
    void getAdsMe() {

        List<AdEntity> adEntityList = new ArrayList<>();
        adEntityList.add(adEntity1);
        adEntityList.add(adEntity2);

        when(adRepository.findAll()).thenReturn(adEntityList);

        Ads ads = new Ads();
        ads.setCount(2);

        List<Ad> adList = new ArrayList<>();
        Ad ad1 = new Ad();
        ad1.setPk(1);
        ad1.setTitle("Ad 1");
        adList.add(ad1);

        Ad ad2 = new Ad();
        ad2.setPk(2);
        ad2.setTitle("Ad 2");
        adList.add(ad2);

        ads.setResults(adList);

        UserEntity currentUserEntity = new UserEntity();
        currentUserEntity.setId(1);
        currentUserEntity.setAdEntities(adEntityList);
        when(userAuthentication.getCurrentUser()).thenReturn(currentUserEntity);

        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));


        when(adRepository.findAll()).thenReturn(adEntityList);

        when(adsMapper.adEntityListToAdList(adEntityList)).thenReturn(adList);

        assertThat(adsService.getAllAds()).isEqualTo(ads);

    }

    @Test
    void updateImage() throws IOException {
        Integer adPk = 1;
        byte[] imageBytes = new byte[10];

        when(adRepository.findById(any())).thenReturn(Optional.empty());
        when(userAuthentication.getCurrentUser()).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> {
            adsService.updateImage(adPk, null);
        });



        UserEntity currentUserEntity = new UserEntity();
        currentUserEntity.setId(1);
        AdEntity foundAdEntity = new AdEntity();
        foundAdEntity.setPk(adPk);
        foundAdEntity.setUserEntity(userEntity);
        when(adRepository.findById(any())).thenReturn(Optional.of(foundAdEntity));

        when(userAuthentication.getCurrentUser()).thenReturn(currentUserEntity);

        when(imageService.uploadAdImage(anyInt(), any(MultipartFile.class))).thenReturn(imageBytes);

        MultipartFile image = new MockMultipartFile("test.jpg", new byte[10]);

        AdEntity savedAdEntity = new AdEntity();
        savedAdEntity.setPk(1);
        savedAdEntity.setTitle("Test Ad");
        savedAdEntity.setPrice(100);
        savedAdEntity.setDescription("Test description");

        assertThat(adsService.updateImage(1,image)).isEqualTo(imageBytes);
    }
}