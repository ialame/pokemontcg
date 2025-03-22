package com.pca.pokimages.service;

import com.github.f4b6a3.ulid.Ulid;
import com.pca.pokimages.entity.Card;
import com.pca.pokimages.entity.CardSet;
import com.pca.pokimages.entity.Serie;
import com.pca.pokimages.repository.CardRepository;
import com.pca.pokimages.repository.CardSetRepository;
import com.pca.pokimages.repository.SerieRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {
    private static final Logger log = LoggerFactory.getLogger(CardService.class);

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private CardSetRepository cardSetRepository;

    @Autowired
    private CardRepository cardRepository;

    @PostConstruct
    public void initData() {
        log.info("Début de l'initialisation des données.");

        // Ajout d'une série
        Serie testSerie = new Serie("Test Series");
        serieRepository.save(testSerie);
        log.info("Série insérée - name: 'Test Series', id: '{}'", testSerie.getId());

        // Ajout d'un set
        CardSet testSet = new CardSet();
        testSet.setName("Test Set");
        testSet.setReleaseDate("2023-01-01");
        testSet.setSerie(testSerie);
        cardSetRepository.save(testSet);
        log.info("Set inséré - name: 'Test Set', id: '{}'", testSet.getId());

        // Ajout d'une carte
        Card testCard = new Card();
        testCard.setName("Test Card");
        testCard.setImagePath("/images/test.png");
        testCard.setCardSet(testSet);
        cardRepository.save(testCard);
        log.info("Carte insérée - name: 'Test Card', id: '{}'", testCard.getId());
    }

    public List<Serie> getSeries() {
        return serieRepository.findAll();
    }

    public List<CardSet> getSetsBySeries(String seriesName) {
        Serie serie = serieRepository.findByName(seriesName)
                .orElseThrow(() -> new RuntimeException("Serie not found: " + seriesName));
        return cardSetRepository.findBySerie(serie);
    }

    public List<Card> getCardsBySet(Ulid setId) {
        CardSet set = cardSetRepository.findById(setId)
                .orElseThrow(() -> new RuntimeException("Set not found: " + setId));
        return cardRepository.findByCardSet(set);
    }

    public Card getCardById(Ulid cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found: " + cardId));
    }

    public List<Card> getAllCards() {
        log.info("Appel de getAllCards");
        List<Card> cards = cardRepository.findAll();
        log.info("Nombre de cartes trouvées : {}", cards.size());
        return cards;
    }
}