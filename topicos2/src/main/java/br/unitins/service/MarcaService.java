package br.unitins.service;

import java.util.List;

import br.unitins.dto.MarcaRequestDTO;
import br.unitins.model.Marca;

public interface MarcaService {

    List<Marca> findAll(Integer page, Integer pageSize);
    List<Marca> findByNome(String nome, Integer page, Integer pageSize);
    Marca findById(Long id);
    Marca create(MarcaRequestDTO dto);
    void update(Long id, MarcaRequestDTO dto);
    void delete(Long id, Long replacementMarcaId);
}
