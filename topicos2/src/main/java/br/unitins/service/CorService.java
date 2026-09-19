package br.unitins.service;

import java.util.List;

import br.unitins.dto.CorRequestDTO;
import br.unitins.model.Cor;
import br.unitins.model.Tonalidade;

public interface CorService {

    List<Cor> findAll(Integer page, Integer pageSize);
    List<Cor> findByNome(String nome, Integer page, Integer pageSize);
    List<Cor> findByTonalidade(Tonalidade tonalidade, Integer page, Integer pageSize);
    Cor findById(Long id);
    Cor create(CorRequestDTO dto);
    void update(Long id, CorRequestDTO dto);
    void delete(Long id);
}
