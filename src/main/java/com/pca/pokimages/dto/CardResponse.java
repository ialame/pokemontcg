package com.pca.pokimages.dto;

import lombok.Data;

@Data
public class CardResponse {
    private String id;
    private String name;
    private ImagesResponse images;
}