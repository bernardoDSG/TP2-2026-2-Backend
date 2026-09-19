package br.unitins.service;

import java.util.List;

import br.unitins.model.StatusUso;

public interface StatusUsoService {

    List<StatusUso> findAll();
    StatusUso findById(Long id);
}