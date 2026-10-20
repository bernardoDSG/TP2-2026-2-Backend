package br.unitins.service;

import java.util.List;

import br.unitins.dto.CategoriaRequestDTO;
import br.unitins.model.Categoria;

public interface CategoriaService {

    List<Categoria> findAll(Integer page, Integer pageSize);
    List<Categoria> findByNome(String nome, Integer page, Integer pageSize);
    Categoria findById(Long id);
    Categoria create(CategoriaRequestDTO dto);
    void update(Long id, CategoriaRequestDTO dto);
    void delete(Long id, Long replacementCategoriaId);
}
