package com.pca.pokimages.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    private String id; // ex. "base1-1"

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private Set set;

    @Column(name = "image_path")
    private String imagePath; // ex. "images/Base/Base Set/Bulbasaur.png"
}