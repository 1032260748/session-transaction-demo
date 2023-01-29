package com.example.db.demo.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;


@Setter
@Getter
@Builder
public class ProductVo {
    private String name;

    private String code;

    private Integer price;

    private String description;

    @Tolerate
    public ProductVo() {
    }

}
