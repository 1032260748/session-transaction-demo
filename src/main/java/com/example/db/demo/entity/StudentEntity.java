package com.example.db.demo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;


@Entity(name = "student")
@Setter
@Getter
@Builder
public class StudentEntity {

    @Id
    private Long id;

    private String name;

    private String no;

    @Tolerate
    public StudentEntity() {
        //do nothing
    }

}
