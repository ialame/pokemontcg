package com.pca.pokimages.controller;

import com.github.f4b6a3.ulid.Ulid;
import com.pca.pokimages.entity.Card;
import com.pca.pokimages.entity.CardSet;
import com.pca.pokimages.entity.Serie;
import com.pca.pokimages.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private static final Logger log = LoggerFactory.getLogger(CardController.class);
    @Autowired
    private CardService cardService;

    @GetMapping
    public List<Card> getAllCards() {
        log.info("Requête GET /api/cards reçue");
        List<Card> cards = cardService.getAllCards();
        log.info("Réponse envoyée avec {} cartes", cards.size());
        return cards;
    }

    @GetMapping("/set/{setId}")
    public List<Card> getCardsBySet(@PathVariable String setId) {
        Ulid ulid = Ulid.from(setId);
        return cardService.getCardsBySet(ulid);
    }

    @GetMapping("/{id}")
    public Card getCardById(@PathVariable String id) {
        Ulid ulid = Ulid.from(id);
        return cardService.getCardById(ulid);
    }
}