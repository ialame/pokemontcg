package com.pca.pokimages.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pca.pokimages.hibernate.AbstractUlidEntity;
import com.pca.pokimages.hibernate.UlidType;
import com.github.f4b6a3.ulid.Ulid;
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

    @Column(name = "external_id")
    private String externalId; // ID de l’API Pokémon TCG


    @Column(nullable = false)
    private String name;

    private String releaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id", columnDefinition = "BINARY(16)", foreignKey = @ForeignKey(name = "fk_cardset_serie"))
    @JsonBackReference // Évite les références circulaires
    @Type(UlidType.class)
    private Serie serie;

    @OneToMany(mappedBy = "cardSet", fetch = FetchType.LAZY) // Corrigé de @ManyToMany à @OneToMany
    private List<Card> cards = new ArrayList<>();

    @Version // Ajout du champ de version
    private Integer version = 0; // Initialiser la valeur à 0 ou 1

//    public CardSet() {
//        this.version = 0; // Initialiser la valeur à 0 ou 1
//    }
}