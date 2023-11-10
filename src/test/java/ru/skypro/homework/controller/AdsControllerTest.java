package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.AdEntity;
import ru.skypro.homework.entity.CommentEntity;
import ru.skypro.homework.entity.ImageEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AdsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentsService;

    @MockBean
    private AdsService adsService;

    @MockBean
    ImageService imageService;

    @MockBean
    private AuthService authService;

    @MockBean
    private UsersService usersService;

    @InjectMocks
    private AdsController adsController;

    @Mock
    ImageEntity imageEntity;


    private AdEntity adEntity;
    private AdEntity adEntity1;
    private AdEntity adEntity2;
    private UserEntity userEntity;
    private LocalDateTime time;
    private CommentEntity commentEntity;
    private Collection<CommentEntity> commentEntities;
    private List<CommentEntity> commentEntityList;

    private Ad ad1;
    private Ad ad2;

    @BeforeEach
    void init() {
        initMocks(this);

        time = LocalDateTime.now();

        adEntity1 = new AdEntity(1, 10, "title1", "description1", userEntity, imageEntity, commentEntities);
        adEntity2 = new AdEntity(2, 20, "title2", "description2", userEntity, imageEntity, commentEntities);

        List<AdEntity> adEntityList = new ArrayList<>();
        adEntityList.add(adEntity1);
        adEntityList.add(adEntity2);


        userEntity = new UserEntity(1, "testUser", "password", "firstName",
                "lastName", "999", Role.USER, null, commentEntityList, imageEntity);

        commentEntity = new CommentEntity(1, "comment1", time, userEntity, adEntity);

        ad1 = new Ad(1, "path", 1, 10, "title1");
        ad2 = new Ad(1, "path", 2, 20, "title2");


    }

    @Test
    void getAllAds() throws Exception {
        List<Ad> adList = new ArrayList<>();
        adList.add(ad1);
        adList.add(ad2);
        Ads ads = new Ads();
        ads.setCount(2);
        ads.setResults(adList);

        List<AdEntity> adEntityList = new ArrayList<>();
        adEntityList.add(adEntity1);
        adEntityList.add(adEntity2);

        when(adsService.getAllAds()).thenReturn(ads);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAds() throws Exception {

        ExtendedAd extendedAd = new ExtendedAd(1, "test", "test",
                "description", "mail", "image", "999", 10, "test");

        when(adsService.getAds(1)).thenReturn(extendedAd);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value("1"))
                .andExpect(jsonPath("$.authorFirstName").value("test"))
                .andExpect(jsonPath("$.authorLastName").value("test"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.email").value("mail"))
                .andExpect(jsonPath("$.image").value("image"))
                .andExpect(jsonPath("$.phone").value("999"))
                .andExpect(jsonPath("$.price").value("10"))
                .andExpect(jsonPath("$.title").value("test"));
    }

    @Test
    void removeAd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    void updateAds() throws Exception {

        CreateOrUpdateAd createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle("Test Ad");
        createOrUpdateAd.setPrice(100);
        createOrUpdateAd.setDescription("Test description");

        ObjectMapper jsonMapper = new ObjectMapper();

        Ad ad = new Ad(1, "/image/1", 1, 100, "Test Ad");

        when(adsService.updateAds(1, createOrUpdateAd)).thenReturn(ad);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/1")
                        .content(jsonMapper.writeValueAsBytes(createOrUpdateAd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("1"))
                .andExpect(jsonPath("$.image").value("/image/1"))
                .andExpect(jsonPath("$.pk").value("1"))
                .andExpect(jsonPath(("$.price")).value("100"))
                .andExpect(jsonPath(("$.title")).value("Test Ad"));
    }

    @Test
    void getAdsMe() throws Exception {

        List<Ad> adList = new ArrayList<>();
        adList.add(ad1);
        adList.add(ad2);
        Ads ads = new Ads();
        ads.setCount(2);
        ads.setResults(adList);


        when(adsService.getAdsMe()).thenReturn(ads);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value("2"));
    }

    @Test
    void updateImage() throws Exception {
        MockPart image = new MockPart("image", "file", "image".getBytes());
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/ads/2/image")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.addPart(image);
                            return request;
                        }))
                .andExpect(status().isOk());
    }

}