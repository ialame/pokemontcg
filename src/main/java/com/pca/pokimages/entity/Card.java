package com.pca.pokimages.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "card")
@Data
public class Card {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id")
    @JsonBackReference
    private Set set;

    public String getImagePath() {
        return imagePath != null ? "http://localhost:8081/api/images/" + set.getSerie().getName() + "/" + set.getName() + "/" + name + ".png" : null;
    }
}