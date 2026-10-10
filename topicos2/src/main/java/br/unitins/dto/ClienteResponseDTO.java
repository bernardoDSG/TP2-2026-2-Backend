package br.unitins.dto;

import java.util.List;

import br.unitins.model.Cliente;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        List<EnderecoResponseDTO> enderecos) {

    public static ClienteResponseDTO valueOf(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getEnderecos().stream().map(EnderecoResponseDTO::valueOf).toList());
    }
}
