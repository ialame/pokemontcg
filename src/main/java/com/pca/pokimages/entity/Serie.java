package com.pca.pokimages.entity;

import com.pca.pokimages.hibernate.AbstractUlidEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "serie")
public class Serie extends AbstractUlidEntity {
    @Setter
    @Getter
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "serie", fetch = FetchType.LAZY)
    private List<CardSet> sets;

    public Serie(String name) {
        this.name = name;
    }
    public Serie() {
    }

}