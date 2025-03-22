package com.pca.pokimages.entity;

import com.pca.pokimages.hibernate.AbstractUlidEntity;
import com.pca.pokimages.hibernate.UlidType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card_set")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = true)
public class CardSet extends AbstractUlidEntity {
    @Column(nullable = false)
    private String name;

    private String releaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id", columnDefinition = "BINARY(16)", foreignKey = @ForeignKey(name = "fk_cardset_serie"))
    @Type(UlidType.class)
    private Serie serie;

    @OneToMany(mappedBy = "cardSet", fetch = FetchType.LAZY) // Corrigé de @ManyToMany à @OneToMany
    private List<Card> cards = new ArrayList<>();
}