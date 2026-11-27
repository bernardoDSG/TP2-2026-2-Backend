package br.unitins.dto;

import java.util.Set;

public record UsuarioAutenticadoResponseDTO(
        String subject,
        String username,
        String nome,
        String email,
        Set<String> roles) {
}
