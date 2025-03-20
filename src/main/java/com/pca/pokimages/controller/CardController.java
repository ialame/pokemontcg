package com.pca.pokimages.controller;

import com.pca.pokimages.dto.SerieDTO;
import com.pca.pokimages.entity.Card;
import com.pca.pokimages.entity.Set;
import com.pca.pokimages.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/series")
    public List<SerieDTO> getSeries() {
        return cardService.getSeries().stream()
                .map(serie -> new SerieDTO(serie.getId(), serie.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/series/{seriesName}/sets")
    public List<Set> getSetsBySeries(@PathVariable String seriesName) {
        return cardService.getSetsBySeries(seriesName);
    }

    @GetMapping("/sets/{setId}/cards")
    public List<Card> getCardsBySet(@PathVariable String setId) {
        return cardService.getCardsBySet(setId);
    }

    @GetMapping("/cards/{id}")
    public Card getCardById(@PathVariable String id) {
        return cardService.getCardById(id);
    }
    @GetMapping("/images/{series}/{set}/{filename}")
    public ResponseEntity<FileSystemResource> getImage(
            @PathVariable String series,
            @PathVariable String set,
            @PathVariable String filename) {
        File file = new File("/app/images/" + series + "/" + set + "/" + filename);
        if (file.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new FileSystemResource(file));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}