package com.pca.pokimages.repository;

import com.pca.pokimages.entity.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SetRepository extends JpaRepository<Set, String> {
    List<Set> findBySerieName(String serieName);
}