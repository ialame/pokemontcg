package com.pca.pokimages.repository;

import com.github.f4b6a3.ulid.Ulid;
import com.pca.pokimages.entity.Card;
import com.pca.pokimages.entity.CardSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Ulid> {
    List<Card> findByCardSet(CardSet cardset);
}