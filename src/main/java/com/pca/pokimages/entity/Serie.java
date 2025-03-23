package com.pca.pokimages.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pca.pokimages.hibernate.AbstractUlidEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Table(name = "serie")
@EqualsAndHashCode(callSuper = false)
public class Serie extends AbstractUlidEntity {

    @Column(name = "external_id")
    private String externalId; // ID de l’API Pokémon TCG

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Évite les références circulaires
    private List<CardSet> sets = new ArrayList<>();

    @Version
    private Long version = 0L; // Initialisation par défaut

    // getters et setters

    public Serie() {
        // Initialisation par défaut
        this.version = 0L;
    }
}