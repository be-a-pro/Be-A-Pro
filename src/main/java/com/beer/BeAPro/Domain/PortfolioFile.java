package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class PortfolioFile {

    @Id @GeneratedValue
    @Column(name = "portfolio_file_id")
    private Long id;

    private String originalName;

    private String modifiedName;

    private int size; // byte

    private String filepath;

}
