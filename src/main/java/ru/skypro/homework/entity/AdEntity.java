package ru.skypro.homework.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@EqualsAndHashCode(exclude = "pk")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    private Integer price;
    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_entity_id")
    private UserEntity userEntity;

    @OneToOne(cascade = CascadeType.ALL)
    private ImageEntity imageEntity;

    @OneToMany(mappedBy = "adEntity", cascade = CascadeType.ALL)
    private Collection<CommentEntity> commentEntities;
}
