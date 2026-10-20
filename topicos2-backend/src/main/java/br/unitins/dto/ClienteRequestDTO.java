package br.unitins.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
    @NotBlank @Size(max = 120) String nome,
    @NotBlank @Pattern(regexp = "\\d{11}") String cpf,
    @NotBlank @Email @Size(max = 160) String email,
    @NotBlank @Pattern(regexp = "\\d{10,11}") String telefone,
    @NotEmpty @Valid List<EnderecoRequestDTO> enderecos
) {}