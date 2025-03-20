package com.pca.pokimages.service;

import com.pca.pokimages.dto.CardResponse;
import com.pca.pokimages.dto.ImagesResponse;
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
import org.springframework.stereotype.Service;
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

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String POKEMON_TCG_API = "https://api.pokemontcg.io/v2";
    private static final String IMAGE_DIR = "/app/images/";

    private final SerieRepository serieRepository;
    private final SetRepository setRepository;
    private final CardRepository cardRepository;

    @PostConstruct
    public void initData() throws IOException {
        if (serieRepository.count() > 0) return;

        PokemonTcgSetsResponse setsResponse = restTemplate.getForObject(POKEMON_TCG_API + "/sets", PokemonTcgSetsResponse.class);
        if (setsResponse == null || setsResponse.getData() == null) return;

        for (SetResponse setResponse : setsResponse.getData()) {
            Serie serie = serieRepository.findByName(setResponse.getSeries());
            if (serie == null) {
                serie = new Serie();
                serie.setName(setResponse.getSeries());
                serie = serieRepository.save(serie);
            }

            Set set = setRepository.findById(setResponse.getId()).orElse(new Set());
            set.setId(setResponse.getId());
            set.setName(setResponse.getName());
            set.setSerie(serie);
            set.setReleaseDate(setResponse.getReleaseDate());
            setRepository.save(set);

            PokemonTcgCardsResponse cardsResponse = restTemplate.getForObject(
                    POKEMON_TCG_API + "/cards?q=set.id:" + setResponse.getId(), PokemonTcgCardsResponse.class);
            if (cardsResponse == null || cardsResponse.getData() == null) continue;

            for (CardResponse cardResponse : cardsResponse.getData()) {
                Card card = cardRepository.findById(cardResponse.getId()).orElse(new Card());
                card.setId(cardResponse.getId());
                card.setName(cardResponse.getName());
                card.setSet(set);

                String imageUrl = cardResponse.getImages().getLarge();
                String imagePath = IMAGE_DIR + serie.getName() + "/" + set.getName() + "/" + cardResponse.getName() + ".png";
                downloadImage(imageUrl, imagePath);
                card.setImagePath(imagePath);

                cardRepository.save(card);
            }
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