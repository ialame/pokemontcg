package com.pca.pokimages.dto;

import lombok.Data;

import java.util.List;

@Data
public class PokemonTcgSetsResponse {
    private List<SetResponse> data;
}