package com.example.motocatalogapi.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;

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

    @NotBlank(message = "O nome da moto é obrigatório")

    @Size(
            max = 120,
            message = "O nome deve ter no máximo 120 caracteres"
    )

    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "A marca é obrigatória")

    @Size(
            max = 60,
            message = "A marca deve ter no máximo 60 caracteres"
    )

    @Column(nullable = false, length = 60)
    private String marca;

    @NotBlank(message = "O modelo é obrigatório")

    @Size(
            max = 100,
            message = "O modelo deve ter no máximo 100 caracteres"
    )

    @Column(nullable = false, length = 100)
    private String modelo;

    @NotNull(message = "O ano é obrigatório")

    @Min(
            value = 1900,
            message = "O ano não pode ser menor que 1900"
    )

    @Max(
            value = 2100,
            message = "O ano não pode ser maior que 2100"
    )

    @Column(nullable = false)
    private Integer ano;

    @NotNull(message = "A cilindrada é obrigatória")

    @Positive(message = "A cilindrada deve ser maior que zero")

    @Column(nullable = false)
    private Integer cilindrada;

    @NotNull(message = "A quilometragem é obrigatória")

    @PositiveOrZero(message = "A quilometragem não pode ser negativa")

    @Column(nullable = false)
    private Integer quilometragem;

    @NotBlank(message = "A cor é obrigatória")

    @Column(nullable = false, length = 50)
    private String cor;

    @Size(
            max = 1000,
            message = "A descrição deve ter no máximo 1000 caracteres"
    )

    @Column(length = 1000)
    private String descricao;

    private String imagemUrl;

    @NotBlank(message = "O telefone do vendedor é obrigatório")

    @Size(
            min = 8,
            max = 20,
            message = "O telefone deve ter entre 8 e 20 caracteres"
    )

    @Column(nullable = false, length = 20)
    private String telefoneVendedor;

    @NotNull(message = "A categoria é obrigatória")

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    private CategoriaMoto categoria;

    @Enumerated(EnumType.STRING)
    private StatusMoto status;
}