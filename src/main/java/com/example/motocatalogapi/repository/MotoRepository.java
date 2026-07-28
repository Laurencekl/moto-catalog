package com.example.motocatalogapi.repository;

import com.example.motocatalogapi.model.CategoriaMoto;
import com.example.motocatalogapi.model.Moto;
import com.example.motocatalogapi.model.StatusMoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MotoRepository extends JpaRepository<Moto, Long> {

    List<Moto> findByStatus(StatusMoto status);

    @Query("""
            SELECT m FROM Moto m
            WHERE m.status = :status
            AND (:categoria IS NULL OR m.categoria = :categoria)
            AND (:anoMinimo IS NULL OR m.ano >= :anoMinimo)
            AND (:anoMaximo IS NULL OR m.ano <= :anoMaximo)
            AND (:cilindradaMinima IS NULL OR m.cilindrada >= :cilindradaMinima)
            AND (:cilindradaMaxima IS NULL OR m.cilindrada <= :cilindradaMaxima)
            ORDER BY m.id DESC
            """)
    List<Moto> filtrarCatalogo(
            @Param("status") StatusMoto status,
            @Param("categoria") CategoriaMoto categoria,
            @Param("anoMinimo") Integer anoMinimo,
            @Param("anoMaximo") Integer anoMaximo,
            @Param("cilindradaMinima") Integer cilindradaMinima,
            @Param("cilindradaMaxima") Integer cilindradaMaxima
    );
}