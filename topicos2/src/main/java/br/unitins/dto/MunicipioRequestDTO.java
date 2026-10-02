package br.unitins.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MunicipioRequestDTO(
    @NotBlank @Size(max = 100) String nome,
    @NotNull Long estadoId
) {}