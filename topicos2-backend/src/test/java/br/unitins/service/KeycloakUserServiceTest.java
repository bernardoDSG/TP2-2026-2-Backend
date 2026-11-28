package br.unitins.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.unitins.dto.CadastroUsuarioRequestDTO;

class KeycloakUserServiceTest {

    private final KeycloakUserService service = new KeycloakUserService();

    @Test
    void mapsFullNameToRequiredKeycloakFirstAndLastNames() {
        var user = service.toUserRepresentation(new CadastroUsuarioRequestDTO(
                "  Ana   Maria Silva  ",
                " ANA@EXEMPLO.COM ",
                "senha-muito-segura-123"));

        assertEquals("ana@exemplo.com", user.getUsername());
        assertEquals("ana@exemplo.com", user.getEmail());
        assertEquals("Ana Maria", user.getFirstName());
        assertEquals("Silva", user.getLastName());
        assertTrue(user.getRequiredActions().isEmpty());
        assertEquals("senha-muito-segura-123", user.getCredentials().get(0).getValue());
        assertFalse(user.getCredentials().get(0).isTemporary());
    }

    @Test
    void usesSingleNameForBothRequiredNameFields() {
        var user = service.toUserRepresentation(new CadastroUsuarioRequestDTO(
                "  Cliente ",
                "cliente@exemplo.com",
                "senha-muito-segura-123"));

        assertEquals("Cliente", user.getFirstName());
        assertEquals("Cliente", user.getLastName());
    }
}
