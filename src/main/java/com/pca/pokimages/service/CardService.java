package com.pca.pokimages.service;

import com.github.f4b6a3.ulid.Ulid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.pca.pokimages.entity.CardSet;
import com.pca.pokimages.entity.Serie;
import com.pca.pokimages.repository.CardSetRepository;
import com.pca.pokimages.repository.SerieRepository;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import com.github.f4b6a3.ulid.Ulid.*;

@Service
@RequiredArgsConstructor
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String POKEMON_TCG_API = "https://api.pokemontcg.io/v2";

    @Value("${pokemon.tcg.api.key}")
    private String apiKey;

    private final SerieRepository serieRepository;
    private final CardSetRepository cardSetRepository;

    @PostConstruct
    @Transactional
    public void init() {
        logger.info("Lancement de l'initialisation des données.");
        initData();
    }

    private void initData() {
        try {
            logger.info("Début de l'initialisation des données depuis l'API Pokémon TCG");

            // Vérifier si la base est déjà remplie
            if (serieRepository.count() > 0) {
                logger.info("La base contient déjà des données ({} séries), initialisation ignorée", serieRepository.count());
                return;
            }

            // Configurer les headers avec la clé API
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Api-Key", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Récupérer les sets
            String setsUrl = POKEMON_TCG_API + "/sets";
            var setsResponse = restTemplate.exchange(setsUrl, HttpMethod.GET, entity, SetsResponse.class);
            if (setsResponse.getBody() == null || setsResponse.getBody().data == null) {
                logger.error("Aucun set récupéré de l'API");
                return;
            }

            // Grouper les sets par série et créer les entités en mémoire
            Map<String, List<SetData>> setsBySeries = setsResponse.getBody().data.stream()
                    .collect(Collectors.groupingBy(set -> set.series));

            // Utiliser une Collection pour enregistrer une liste de series
            List<Serie> seriesToSave = new ArrayList<>();

            //Parcourir les series dans une boucle
            for (Map.Entry<String, List<SetData>> entry : setsBySeries.entrySet()) {
                String seriesName = entry.getKey();
                List<SetData> setsData = entry.getValue();

                // Vérifier si la serie a déjà été enregistrée
                Optional<Serie> existingSerie = serieRepository.findByName(seriesName);

                Serie serie;
                // Si la serie existe alors mettre à jour
                if (existingSerie.isPresent()) {
                    serie = existingSerie.get();
                    logger.info("Mise à jour de la serie existante: {}", seriesName);
                } else {
                    // Si non, créer une nouvelle serie
                    serie = new Serie();
                    serie.setId(Ulid.fast());
                    serie.setName(seriesName);
                    logger.info("Création d'une nouvelle serie: {}", seriesName);
                }

                // Boucler sur les sets et les enregistrer un par un pour minimiser le problème de concurrence
                for (SetData setData : setsData) {
                    CardSet set = new CardSet();
                    set.setId(Ulid.fast());
                    set.setExternalId(setData.id);
                    set.setName(setData.name);
                    set.setReleaseDate(setData.releaseDate);
                    set.setSerie(serie); // Associer la série au set
                    try {
                        cardSetRepository.save(set);
                    } catch (ObjectOptimisticLockingFailureException ex) {
                        logger.error("Verrouillage optimiste lors de l'enregistrement du CardSet. ID: {}", set.getId());
                        logger.error("Rechargement et nouvelle tentative...");
                        CardSet reloadedSet = cardSetRepository.findById(set.getId()).orElse(null);
                        if (reloadedSet != null) {
                            set.setExternalId(setData.id);
                            set.setName(setData.name);
                            set.setReleaseDate(setData.releaseDate);
                            set.setSerie(serie);
                            cardSetRepository.save(set);
                        } else {
                            logger.error("Impossible de recharger CardSet avec ID: {}", set.getId());
                        }
                    }
                }

                // Enregistrer après la mise à jour pour que l'enregistrement par lot puisse se produire
                serieRepository.save(serie);
            }

            logger.info("Initialisation des données terminée avec succès. Total series : {}, sets : {}",
                    serieRepository.count(), cardSetRepository.count());
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation des données", e);
            throw e; // Relancer pour rollback de la transaction si erreur
        }
    }

    public List<Serie> getSeries() {
        return serieRepository.findAll();
    }

    public Optional<Serie> getSerieById(Ulid id) {
        return serieRepository.findById(id);
    }

    public Optional<CardSet> getCardSetById(Ulid id) {
        return cardSetRepository.findById(id);
    }

    // Classes internes pour désérialiser les réponses de l'API
    private static class SetsResponse {
        public List<SetData> data;
    }

    private static class SetData {
        public String id;
        public String name;
        public String series;
        public String releaseDate;
    }

    private boolean trySaveCardSet(CardSet set, SetData setData, Serie serie, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                set.setExternalId(setData.id);
                set.setName(setData.name);
                set.setReleaseDate(setData.releaseDate);
                set.setSerie(serie);
                cardSetRepository.save(set);
                return true; // Succès
            } catch (ObjectOptimisticLockingFailureException ex) {
                attempts++;
                logger.warn("Tentative {}/{} - Échec du verrouillage optimiste lors de l'enregistrement du CardSet avec ID: {}. Nouvelle tentative...", attempts, maxRetries, set.getId());
                try {
                    Thread.sleep(100); // Courte temporisation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restaurer l'état d'interruption
                    logger.warn("Nouvelle tentative interrompue.");
                    return false; // Arrêter de réessayer
                }

                CardSet reloadedSet = cardSetRepository.findById(set.getId()).orElse(null);
                if (reloadedSet == null) {
                    logger.error("Impossible de recharger CardSet avec ID: {}. Abandon.", set.getId());
                    return false; // Impossible de recharger, arrêter de réessayer
                }

                // Important: Mettre à jour l'objet set avec les valeurs reloadedSet
                set.setExternalId(reloadedSet.getExternalId());
                set.setName(reloadedSet.getName());
                set.setReleaseDate(reloadedSet.getReleaseDate());
                set.setSerie(reloadedSet.getSerie());


                logger.info("Les valeurs ont été rechargées avec succès et je vais tenter de réessayer");
            }

        }

        logger.error("Nombre maximal de nouvelles tentatives atteint pour CardSet avec ID: {}. Abandon.", set.getId());
        return false; // Nombre maximal de nouvelles tentatives atteint sans succès
    }

}