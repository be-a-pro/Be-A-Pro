package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class ProfileImage {

    @Id @GeneratedValue
    @Column(name = "profile_image_id")
    private Long id;

    private String originalName;

    private String modifiedName;

    private int size; // byte

    private String filepath;

}
