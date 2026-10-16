package br.unitins.service;

import java.util.List;

import br.unitins.dto.SetorRequestDTO;
import br.unitins.model.Setor;

public interface SetorService {
    List<Setor> findAll();
    Setor findById(Long id);
    Setor create(SetorRequestDTO dto);
    Setor update(Long id, SetorRequestDTO dto);
    void delete(Long id);
}
