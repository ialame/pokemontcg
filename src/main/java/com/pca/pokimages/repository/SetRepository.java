package com.pca.pokimages.repository;

import com.github.f4b6a3.ulid.Ulid;
import com.pca.pokimages.entity.CardSet;
import com.pca.pokimages.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetRepository extends JpaRepository<CardSet, Ulid> {
    List<CardSet> findBySerie(Serie serie);
}