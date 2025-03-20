package com.pca.pokimages.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "set")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Set {
    @Id
    private String id; // ex. "base1"

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @Column(name = "release_date")
    private String releaseDate;

    @OneToMany(mappedBy = "set")
    private List<Card> cards;
}
