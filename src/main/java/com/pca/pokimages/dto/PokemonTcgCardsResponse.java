package com.pca.pokimages.dto;

import lombok.Data;

import java.util.List;

@Data
public class PokemonTcgCardsResponse {
    private List<CardResponse> data;
}