package br.unitins.dto;

import br.unitins.model.Setor;

public record SetorResponseDTO(Long id, String nome, Integer linhas, Integer colunas) {

    public static SetorResponseDTO valueOf(Setor setor) {
        return new SetorResponseDTO(setor.getId(), setor.getNome(), setor.getLinhas(), setor.getColunas());
    }
}
