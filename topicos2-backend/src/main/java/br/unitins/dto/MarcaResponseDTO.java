package br.unitins.dto;

import br.unitins.model.Marca;

public record MarcaResponseDTO(Long id, String nome) {

    public static MarcaResponseDTO valueOf(Marca marca) {
        return new MarcaResponseDTO(marca.getId(), marca.getNome());
    }
}
