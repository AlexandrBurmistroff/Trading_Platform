package ru.skypro.homework.dto;

import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class Comment {

    /**
     * Класс ДТО комментария
     * @param author id пользователя, в сущности из userEntity
     * @param authorImage аватар автора. В сущности из userEntity filePath
     * @param authorFirstName в сущности из userEntity firstname
     * @param createdAt время создания комментария аналогично с сущностью
     * @param pk первичный ключ комментария
     * @param text текст комментария из сущности adEntity
     */

    private Integer author; //userEntity
    private String authorImage;
    private String authorFirstName;
    private Long createdAt;//
    private Integer pk;//
    private String text;//
    private Integer adId;
}
