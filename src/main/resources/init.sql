ALTER TABLE pokemontcg.card_set
    ADD CONSTRAINT fk_cardset_serie
        FOREIGN KEY (serie_id) REFERENCES pokemontcg.serie(id);

ALTER TABLE pokemontcg.card
    ADD CONSTRAINT fk_card_cardset
        FOREIGN KEY (cardset_id) REFERENCES pokemontcg.card_set(id);