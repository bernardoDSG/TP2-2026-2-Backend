package br.unitins.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
    @NotBlank @Pattern(regexp = "\\d{8}") String cep,
    @NotBlank @Size(max = 160) String logradouro,
    @NotBlank @Size(max = 20) String numero,
    @Size(max = 100) String complemento,
    @NotBlank @Size(max = 100) String bairro,
    @NotNull Long municipioId
) {}