package com.pca.pokimages.controller;

import com.github.f4b6a3.ulid.Ulid;
import com.pca.pokimages.dto.CardDTO;
import com.pca.pokimages.dto.CardSetDTO;
import com.pca.pokimages.dto.SerieDTO;
import com.pca.pokimages.entity.CardSet;
import com.pca.pokimages.entity.Serie;
import com.pca.pokimages.service.CardService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8082")
public class CardController {
    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private CardService cardService;

    @GetMapping("/series")
    public List<SerieDTO> getAllSeries() {
        logger.info("Appel de getAllSeries");
        List<Serie> series = cardService.getSeries();
        List<SerieDTO> seriesDTOs = series.stream()
                .map(serie -> {
                    SerieDTO dto = new SerieDTO();
                    dto.setId(serie.getId().toString());
                    dto.setName(serie.getName());
                    return dto;
                })
                .collect(Collectors.toList());
        logger.info("Renvoi de {} séries", seriesDTOs.size());
        return seriesDTOs;
    }

    @GetMapping("/series/{id}/sets")
    public List<CardSetDTO> getSetsBySerieId(@PathVariable String id) {
        logger.info("Appel de getSetsBySerieId pour l'ID : {}", id);
        //UUID serieId = UUID.fromString(id); //INUTILE
        Ulid serieId = Ulid.from(id); //Utiliser Ulid.valueOf directement
        Serie serie = cardService.getSerieById(serieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Série non trouvée"));
        List<CardSetDTO> setsDTOs = serie.getSets().stream()
                .map(set -> {
                    CardSetDTO dto = new CardSetDTO();
                    dto.setId(set.getId().toString());
                    dto.setName(set.getName());
                    dto.setReleaseDate(set.getReleaseDate());
                    return dto;
                })
                .collect(Collectors.toList());
        logger.info("Renvoi de {} sets pour la série {}", setsDTOs.size(), serie.getName());
        return setsDTOs;
    }

    @GetMapping("/sets/{id}/cards")
    public List<CardDTO> getCardsBySetId(@PathVariable String id) {
        logger.info("Appel de getCardsBySetId pour l'ID : {}", id);
        UUID setId = UUID.fromString(id);
        CardSet cardSet = cardService.getCardSetById(Ulid.from(setId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Set non trouvé"));
        List<CardDTO> cardsDTOs = cardSet.getCards().stream()
                .map(card -> {
                    CardDTO dto = new CardDTO();
                    dto.setId(card.getId().toString());
                    dto.setName(card.getName());
                    dto.setImagePath(card.getImagePath());
                    return dto;
                })
                .collect(Collectors.toList());
        logger.info("Renvoi de {} cartes pour le set {}", cardsDTOs.size(), cardSet.getName());
        return cardsDTOs;
    }

    @GetMapping("/test")
    public String test() {
        logger.info("Appel de /test");
        return "Backend is alive!";
    }
}