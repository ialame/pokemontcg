package com.pca.pokimages.hibernate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.f4b6a3.ulid.Ulid;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@MappedSuperclass
public abstract class AbstractUlidEntity {

    @Id
    @GeneratedValue(generator = "ulid-generator")
    @JsonSerialize(using = ToStringSerializer.class)
    @GenericGenerator(name = "ulid-generator", type = UlidGenerator.class)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    @Type(UlidType.class)
    private Ulid id;

    public Ulid getId() {
        return id;
    }

    public void setId(Ulid id) {
        this.id = id;
    }
}