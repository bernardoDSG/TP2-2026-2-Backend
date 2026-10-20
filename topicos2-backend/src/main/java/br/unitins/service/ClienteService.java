package br.unitins.service;

import java.util.List;

import br.unitins.dto.ClienteRequestDTO;
import br.unitins.model.Cliente;

public interface ClienteService {
    List<Cliente> findAll(Integer page, Integer pageSize);
    Cliente findById(Long id);
    Cliente create(ClienteRequestDTO dto);
    Cliente update(Long id, ClienteRequestDTO dto);
    void delete(Long id);
}