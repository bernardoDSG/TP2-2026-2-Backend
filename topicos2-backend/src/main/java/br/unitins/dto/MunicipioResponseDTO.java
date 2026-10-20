package br.unitins.dto;

import br.unitins.model.Municipio;

public record MunicipioResponseDTO(
        Long id,
        String nome,
        Long estadoId,
        String estadoNome,
        String estadoSigla) {

    public static MunicipioResponseDTO valueOf(Municipio municipio) {
        return new MunicipioResponseDTO(
                municipio.getId(),
                municipio.getNome(),
                municipio.getEstado().getId(),
                municipio.getEstado().getNome(),
                municipio.getEstado().getSigla());
    }
}
