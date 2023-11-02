package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.service.ImageService;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_PNG_VALUE,
                                                MediaType.IMAGE_JPEG_VALUE,
                                                MediaType.IMAGE_GIF_VALUE})
    public byte[] getImage(@PathVariable Integer id) {
        return imageService.getImage(id);
    }

}
