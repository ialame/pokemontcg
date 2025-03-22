package com.pca.pokimages.repository;

import com.github.f4b6a3.ulid.Ulid;
import com.pca.pokimages.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Ulid> {
    Optional<Serie> findByName(String name);
}