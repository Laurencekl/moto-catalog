package com.example.motocatalogapi.service;

import com.example.motocatalogapi.model.CategoriaMoto;
import com.example.motocatalogapi.model.Moto;
import com.example.motocatalogapi.model.StatusMoto;
import com.example.motocatalogapi.repository.MotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotoService {

    private final MotoRepository motoRepository;

    public MotoService(MotoRepository motoRepository) {
        this.motoRepository = motoRepository;
    }

    public List<Moto> listarTodas() {
        return motoRepository.findAll();
    }

    public List<Moto> listarDisponiveis(
            CategoriaMoto categoria,
            Integer anoMinimo,
            Integer anoMaximo,
            Integer cilindradaMinima,
            Integer cilindradaMaxima
    ) {
        return motoRepository.filtrarCatalogo(
                StatusMoto.DISPONIVEL,
                categoria,
                anoMinimo,
                anoMaximo,
                cilindradaMinima,
                cilindradaMaxima
        );
    }

    public Moto buscarPorId(Long id) {
        return motoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moto não encontrada"));
    }


    public Moto cadastrar(Moto moto) {
        if (moto.getStatus() == null) {
            moto.setStatus(StatusMoto.DISPONIVEL);
        }

        return motoRepository.save(moto);
    }

    public Moto atualizar(Long id, Moto dadosAtualizados) {
        Moto moto = buscarPorId(id);

        moto.setNome(dadosAtualizados.getNome());
        moto.setMarca(dadosAtualizados.getMarca());
        moto.setModelo(dadosAtualizados.getModelo());
        moto.setAno(dadosAtualizados.getAno());
        moto.setCilindrada(dadosAtualizados.getCilindrada());
        moto.setQuilometragem(dadosAtualizados.getQuilometragem());
        moto.setCor(dadosAtualizados.getCor());
        moto.setDescricao(dadosAtualizados.getDescricao());
        moto.setImagemUrl(dadosAtualizados.getImagemUrl());
        moto.setTelefoneVendedor(dadosAtualizados.getTelefoneVendedor());
        moto.setCategoria(dadosAtualizados.getCategoria());
        moto.setStatus(dadosAtualizados.getStatus());

        return motoRepository.save(moto);
    }

    public void remover(Long id) {
        Moto moto = buscarPorId(id);
        motoRepository.delete(moto);
    }


    public Moto marcarComoVendida(Long id) {
        Moto moto = buscarPorId(id);
        moto.setStatus(StatusMoto.VENDIDA);

        return motoRepository.save(moto);
    }
}