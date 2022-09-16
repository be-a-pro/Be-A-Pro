package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Position {

    @Id
    @GeneratedValue
    @Column(name = "position_id")
    private Long id;

}
