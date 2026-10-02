package br.unitins.service;

import java.util.List;

import br.unitins.dto.EstadoRequestDTO;
import br.unitins.model.Estado;

public interface EstadoService {
    List<Estado> findAll();
    Estado findById(Long id);
    Estado create(EstadoRequestDTO dto);
    Estado update(Long id, EstadoRequestDTO dto);
    void delete(Long id);
}