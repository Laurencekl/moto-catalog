package com.example.motocatalogapi.exception;

public class MotoNaoEncontradaException extends RuntimeException{

    public MotoNaoEncontradaException(Long id) {
        super("Moto nao encontrada com o id: " + id);
    }
}
