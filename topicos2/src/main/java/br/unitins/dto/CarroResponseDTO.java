package br.unitins.dto;

import br.unitins.model.Carro;

public record CarroResponseDTO(
    Long id,
    String nome,
    String statusUso,
    String nomeCarro,
    Long statusUsoId,
    Long marcaId,
    String marcaNome,
    Long categoriaId,
    String categoriaNome,
    Long corId,
    String corNome,
    String tonalidade,
    String imagemUrl
) {

    public static CarroResponseDTO valueOf(Carro carro) {
        return new CarroResponseDTO(
                carro.getId(),
                carro.getMarca().getNome() + " " + carro.getNome(),
                carro.getStatusUso().name(),
                carro.getNome(),
                carro.getStatusUso().getId(),
                carro.getMarca().getId(),
                carro.getMarca().getNome(),
                carro.getCategoria().getId(),
                carro.getCategoria().getNome(),
                carro.getCor().getId(),
                carro.getCor().getNome(),
                carro.getCor().getTonalidade().getNOME(),
                carro.isPossuiImagem() ? "/carros/" + carro.getId() + "/imagem" : null);
    }
}
