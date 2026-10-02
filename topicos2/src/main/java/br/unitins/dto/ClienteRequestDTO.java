package br.unitins.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
    @NotBlank @Size(max = 120) String nome,
    @NotBlank @Pattern(regexp = "\\d{11}") String cpf,
    @NotBlank @Email @Size(max = 160) String email,
    @NotBlank @Pattern(regexp = "\\d{10,11}") String telefone,
    @NotBlank @Pattern(regexp = "\\d{8}") String cep,
    @NotBlank @Size(max = 160) String logradouro,
    @NotBlank @Size(max = 20) String numero,
    @Size(max = 100) String complemento,
    @NotBlank @Size(max = 100) String bairro,
    @NotBlank @Size(max = 100) String municipio,
    @NotBlank @Size(max = 100) String estadoNome,
    @NotBlank @Pattern(regexp = "[A-Z]{2}") String estadoSigla
) {}