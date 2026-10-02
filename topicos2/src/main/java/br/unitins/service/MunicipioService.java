package br.unitins.service;

import java.util.List;

import br.unitins.dto.MunicipioRequestDTO;
import br.unitins.model.Municipio;

public interface MunicipioService {
    List<Municipio> findAll();
    Municipio findById(Long id);
    Municipio create(MunicipioRequestDTO dto);
    Municipio update(Long id, MunicipioRequestDTO dto);
    void delete(Long id);
}