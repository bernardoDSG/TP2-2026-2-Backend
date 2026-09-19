package br.unitins.service;

import java.util.Arrays;
import java.util.List;

import br.unitins.model.StatusUso;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StatusUsoServiceImpl implements StatusUsoService {

    @Override
    public List<StatusUso> findAll() {
        return Arrays.asList(StatusUso.values());
    }

    @Override
    public StatusUso findById(Long id) {
        return StatusUso.valueOf(id);
    }
}