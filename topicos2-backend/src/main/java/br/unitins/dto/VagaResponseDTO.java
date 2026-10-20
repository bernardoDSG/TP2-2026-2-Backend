package br.unitins.dto;

import br.unitins.model.Vaga;

public record VagaResponseDTO(
        Long id,
        String codigoVaga,
        Integer linha,
        Integer coluna,
        String tipo,
        Long setorId,
        String setorNome,
        Long carroId,
        String carroNome) {

    public static VagaResponseDTO valueOf(Vaga vaga) {
        return new VagaResponseDTO(
                vaga.getId(),
                vaga.getCodigoVaga(),
                vaga.getLinha(),
                vaga.getColuna(),
                vaga.getTipo().name(),
                vaga.getSetor().getId(),
                vaga.getSetor().getNome(),
                vaga.getCarro() == null ? null : vaga.getCarro().getId(),
                vaga.getCarro() == null ? null : vaga.getCarro().getMarca().getNome() + " " + vaga.getCarro().getNome());
    }
}
