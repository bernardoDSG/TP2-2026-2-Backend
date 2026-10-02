package br.unitins.service;

import br.unitins.dto.ClienteRequestDTO;
import br.unitins.model.Cliente;

public interface ClienteService {
    Cliente create(ClienteRequestDTO dto);
}