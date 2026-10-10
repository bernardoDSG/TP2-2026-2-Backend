package br.unitins.dto;

import br.unitins.model.Endereco;

public record EnderecoResponseDTO(
        Long id,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        Long municipioId,
        String municipioNome,
        String estadoSigla) {

    public static EnderecoResponseDTO valueOf(Endereco endereco) {
        return new EnderecoResponseDTO(
                endereco.getId(),
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getMunicipio().getId(),
                endereco.getMunicipio().getNome(),
                endereco.getMunicipio().getEstado().getSigla());
    }
}
