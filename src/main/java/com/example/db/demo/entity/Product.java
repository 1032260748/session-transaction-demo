package com.example.db.demo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;


@Setter
@Getter
@Builder
@Entity(name = "product")
public class Product {

    private String name;

    @Id
    private String code;

    private Integer price;

    private String description;

    @Tolerate
    public Product() {

    }
}
