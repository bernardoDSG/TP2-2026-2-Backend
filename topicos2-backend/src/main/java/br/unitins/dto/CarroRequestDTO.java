package br.unitins.dto;

public record CarroRequestDTO(
    String nome,
    Long StatusUsoId,
    Long corId,
    Long marcaId,
    Long categoriaId
) {
    
}
