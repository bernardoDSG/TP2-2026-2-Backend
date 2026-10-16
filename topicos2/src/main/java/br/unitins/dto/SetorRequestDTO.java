package br.unitins.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SetorRequestDTO(
        @NotBlank @Size(max = 80) String nome,
        @NotNull @Min(1) @Max(50) Integer linhas,
        @NotNull @Min(1) @Max(50) Integer colunas) {}
