package br.unitins.service;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import br.unitins.dto.CadastroUsuarioRequestDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class KeycloakUserService {

    @Inject
    Keycloak keycloak;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    String realmName;

    public void register(CadastroUsuarioRequestDTO request) {
        RealmResource realm = keycloak.realm(realmName);
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.email().trim().toLowerCase());
        user.setEmail(request.email().trim().toLowerCase());
        user.setFirstName(request.nome().trim());
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(request.senha());
        password.setTemporary(false);
        user.setCredentials(List.of(password));

        try (Response response = realm.users().create(user)) {
            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new WebApplicationException("Este e-mail já possui uma conta.", Response.Status.CONFLICT);
            }
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw unavailable("O Keycloak não conseguiu criar a conta.", null);
            }
        } catch (WebApplicationException exception) {
            if (exception.getResponse() != null
                    && exception.getResponse().getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new WebApplicationException("Este e-mail já possui uma conta.", Response.Status.CONFLICT);
            }
            throw unavailable("O Keycloak não conseguiu criar a conta.", exception);
        } catch (RuntimeException exception) {
            throw unavailable("Não foi possível conectar ao Keycloak.", exception);
        }
    }

    private ServiceUnavailableException unavailable(String message, Throwable cause) {
        Response response = Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).build();
        return new ServiceUnavailableException(response, cause);
    }
}
