package com.pca.pokimages.client;

// src/main/java/com/example/pokimages/client/PokeApiClient.java

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pokeapi", url = "https://pokeapi.co/api/v2")
public interface PokeApiClient {
    @GetMapping("/pokemon/{id}")
    String getPokemonById(@PathVariable("id") String id);
}