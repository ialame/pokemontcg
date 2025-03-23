package com.pca.pokimages.repository;

import com.github.f4b6a3.ulid.Ulid;
import com.pca.pokimages.entity.CardSet;
import com.pca.pokimages.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardSetRepository extends JpaRepository<CardSet, Ulid> {
    Optional<CardSet> findByExternalId(String externalId);
    List<CardSet> findBySerie(Serie serie);
}
