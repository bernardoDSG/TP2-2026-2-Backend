package br.unitins.dto;

import br.unitins.model.Cor;

public record CorResponseDTO(
    Long id,
    String nome,
    String tonalidade
) {

    public static CorResponseDTO valueOf(Cor cor) {
        return new CorResponseDTO(cor.getId(), cor.getNome(), cor.getTonalidade().getNOME());
    }
}
