package br.unitins.service;

import java.util.List;

import br.unitins.dto.EnderecoRequestDTO;
import br.unitins.model.Endereco;

public interface EnderecoService {
    List<Endereco> findAll();
    Endereco findById(Long id);
    Endereco create(EnderecoRequestDTO dto);
    Endereco update(Long id, EnderecoRequestDTO dto);
    void delete(Long id);
}
