package br.unitins.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequestDTO(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 12, max = 72) String senha) {
}
