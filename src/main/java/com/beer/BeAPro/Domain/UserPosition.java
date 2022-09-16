package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class UserPosition {

    @Id @GeneratedValue
    @Column(name = "user_position_id")
    private Long id;

}
