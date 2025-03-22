package com.pca.pokimages.entity;

import com.pca.pokimages.hibernate.AbstractUlidEntity;
import com.pca.pokimages.hibernate.UlidType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "card")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = true)
public class Card extends AbstractUlidEntity {
    @Column(nullable = false)
    private String name;

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cardset_id", columnDefinition = "BINARY(16)", foreignKey = @ForeignKey(name = "fk_card_cardset"))
    @Type(UlidType.class)
    private CardSet cardSet;

    public String getImagePath() {
        return imagePath != null ? "http://localhost:8081/api/images/" + cardSet.getSerie().getName() + "/" + cardSet.getName() + "/" + name + ".png" : null;
    }
}