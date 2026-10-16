package br.unitins.service;

import java.util.List;

import br.unitins.dto.VagaRequestDTO;
import br.unitins.model.Vaga;

public interface VagaService {
    List<Vaga> findAll();
    Vaga findById(Long id);
    Vaga create(VagaRequestDTO dto);
    Vaga update(Long id, VagaRequestDTO dto);
    void delete(Long id);
}
