package com.pca.pokimages.repository;

import com.pca.pokimages.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Serie findByName(String name);
}