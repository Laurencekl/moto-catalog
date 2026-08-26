package com.example.motocatalogapi.controller;

import com.example.motocatalogapi.model.CategoriaMoto;
import com.example.motocatalogapi.model.Moto;
import com.example.motocatalogapi.service.MotoService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/motos")
@CrossOrigin(origins = "*")
public class MotoController {

    private final MotoService motoService;

    public MotoController(MotoService motoService) {
        this.motoService = motoService;
    }


    @GetMapping
    public List<Moto> listarTodas() {
        return motoService.listarTodas();
    }

    @GetMapping("/disponiveis")
    public List<Moto> listarDisponiveis(
            @RequestParam(required = false) CategoriaMoto categoria,
            @RequestParam(required = false) Integer anoMinimo,
            @RequestParam(required = false) Integer anoMaximo,
            @RequestParam(required = false) Integer cilindradaMinima,
            @RequestParam(required = false) Integer cilindradaMaxima
    ) {
        return motoService.listarDisponiveis(
                categoria,
                anoMinimo,
                anoMaximo,
                cilindradaMinima,
                cilindradaMaxima
        );
    }

    @GetMapping("/{id}")
    public Moto buscarPorId(@PathVariable Long id) {
        return motoService.buscarPorId(id);
    }

    @PostMapping
    public Moto cadastrar(
            @Valid @RequestBody Moto moto
    ) {
        return motoService.cadastrar(moto);
    }

    @PutMapping("/{id}")
    public Moto atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Moto moto
    ) {
        return motoService.atualizar(id, moto);
    }

    @PatchMapping("/{id}/vendida")
    public Moto marcarComoVendida(@PathVariable Long id) {
        return motoService.marcarComoVendida(id);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        motoService.remover(id);
    }
}