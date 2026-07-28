package com.example.motocatalogapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "motos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String marca;
    private String modelo;
    private Integer ano;
    private Integer cilindrada;
    private Integer quilometragem;
    private String cor;

    @Column(length = 1000)
    private String descricao;

    private String imagemUrl;

    private String telefoneVendedor;

    @Enumerated(EnumType.STRING)
    private CategoriaMoto categoria;

    @Enumerated(EnumType.STRING)
    private StatusMoto status;

}