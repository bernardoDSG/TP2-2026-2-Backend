package br.unitins.dto;

import br.unitins.model.StatusUso;

public record StatusUsoResponseDTO(Long id, String nome) {

    public static StatusUsoResponseDTO valueOf(StatusUso statusUso) {
        return new StatusUsoResponseDTO(statusUso.getId(), statusUso.getNome());
    }
}
