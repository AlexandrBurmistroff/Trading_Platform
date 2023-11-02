package ru.skypro.homework.entity;

import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String filePath;
    private long fileSize;
    private String mediaType;
//    @Lob
//    @Column(columnDefinition = "oid")
//    private byte[] data;

}
