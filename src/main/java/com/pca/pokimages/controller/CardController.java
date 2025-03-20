package com.pca.pokimages.controller;

import com.pca.pokimages.entity.Card;
import com.pca.pokimages.entity.Set;
import com.pca.pokimages.entity.Serie;
import com.pca.pokimages.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/series")
    public List<Serie> getSeries() {
        return cardService.getSeries();
    }

    @GetMapping("/sets")
    public List<Set> getSetsBySeries(@RequestParam String series) {
        return cardService.getSetsBySeries(series);
    }

    @GetMapping("/cards")
    public List<Card> getCardsBySet(@RequestParam String setId) {
        return cardService.getCardsBySet(setId);
    }

    @GetMapping("/cards/{id}")
    public Card getCardById(@PathVariable String id) {
        return cardService.getCardById(id);
    }
}