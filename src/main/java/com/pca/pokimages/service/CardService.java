package com.pca.pokimages.service;

import com.pca.pokimages.dto.CardResponse;
import com.pca.pokimages.dto.PokemonTcgCardsResponse;
import com.pca.pokimages.dto.PokemonTcgSetsResponse;
import com.pca.pokimages.dto.SetResponse;
import com.pca.pokimages.entity.Card;
import com.pca.pokimages.entity.Serie;
import com.pca.pokimages.entity.Set;
import com.pca.pokimages.repository.CardRepository;
import com.pca.pokimages.repository.SerieRepository;
import com.pca.pokimages.repository.SetRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String POKEMON_TCG_API = "https://api.pokemontcg.io/v2";
    private static final String IMAGE_DIR = "/app/images/";

    private final SerieRepository serieRepository;
    private final SetRepository setRepository;
    private final CardRepository cardRepository;

    @PostConstruct
    public void initData() {
        try {
            if (serieRepository.count() > 0) {
                logger.info("La base contient déjà des séries, pas d'initialisation nécessaire.");
                return;
            }

            logger.info("Début de l'initialisation des données depuis l'API Pokémon TCG.");
            PokemonTcgSetsResponse setsResponse = restTemplate.getForObject(POKEMON_TCG_API + "/sets", PokemonTcgSetsResponse.class);
            if (setsResponse == null || setsResponse.getData() == null) {
                logger.warn("Réponse de l'API /sets vide ou nulle.");
                return;
            }

            for (SetResponse setResponse : setsResponse.getData()) {
                Serie serie = serieRepository.findByName(setResponse.getSeries());
                if (serie == null) {
                    serie = new Serie();
                    serie.setName(setResponse.getSeries());
                    serie = serieRepository.save(serie);
                    logger.info("Série sauvegardée : {}", serie.getName());
                }

                Set set = setRepository.findById(setResponse.getId()).orElse(new Set());
                set.setId(setResponse.getId());
                set.setName(setResponse.getName());
                set.setSerie(serie);
                set.setReleaseDate(setResponse.getReleaseDate());
                setRepository.save(set);
                logger.info("Set sauvegardé : {}", set.getName());

                try {
                    PokemonTcgCardsResponse cardsResponse = restTemplate.getForObject(
                            POKEMON_TCG_API + "/cards?q=set.id:" + setResponse.getId(), PokemonTcgCardsResponse.class);
                    if (cardsResponse == null || cardsResponse.getData() == null) {
                        logger.warn("Réponse de l'API /cards vide pour le set : {}", setResponse.getId());
                        continue;
                    }

                    for (CardResponse cardResponse : cardsResponse.getData()) {
                        Card card = cardRepository.findById(cardResponse.getId()).orElse(new Card());
                        card.setId(cardResponse.getId());
                        card.setName(cardResponse.getName());
                        card.setSet(set);

                        String imageUrl = cardResponse.getImages().getLarge();
                        String imagePath = IMAGE_DIR + serie.getName() + "/" + set.getName() + "/" + cardResponse.getName() + ".png";
                        try {
                            downloadImage(imageUrl, imagePath);
                            card.setImagePath(imagePath);
                        } catch (IOException e) {
                            logger.warn("Échec du téléchargement de l'image pour la carte {} : {}", cardResponse.getName(), e.getMessage());
                            card.setImagePath(null);  // Sauvegarde sans image
                        }

                        cardRepository.save(card);
                        logger.info("Carte sauvegardée : {}", card.getName());
                    }
                } catch (RestClientException e) {
                    logger.warn("Échec de la récupération des cartes pour le set {} : {}", setResponse.getId(), e.getMessage());
                    continue;
                }
            }
            logger.info("Initialisation des données terminée avec succès.");
        } catch (Exception e) {
            logger.error("Erreur critique lors de l'initialisation des données : ", e);
            throw new RuntimeException("Échec de l'initialisation des données", e);
        }
    }

    private void downloadImage(String imageUrl, String destination) throws IOException {
        File file = new File(destination);
        file.getParentFile().mkdirs();
        URL url = new URL(imageUrl);
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(file)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    public List<Serie> getSeries() {
        return serieRepository.findAll();
    }


    public List<Set> getSetsBySeries(String seriesName) {
        return setRepository.findBySerieName(seriesName);
    }
    public List<Card> getCardsBySet(String setId) {
        return cardRepository.findBySetId(setId);
    }

    public Card getCardById(String id) {
        return cardRepository.findById(id).orElseThrow(() -> new RuntimeException("Carte non trouvée"));
    }
}