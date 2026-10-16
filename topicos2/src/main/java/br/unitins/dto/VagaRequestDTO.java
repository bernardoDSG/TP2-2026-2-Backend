package br.unitins.dto;

import br.unitins.model.TipoVaga;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VagaRequestDTO(
        @NotBlank @Size(max = 40) String codigoVaga,
        @NotNull @Min(1) Integer linha,
        @NotNull @Min(1) Integer coluna,
        @NotNull TipoVaga tipo,
        @NotNull Long setorId,
        Long carroId) {}
