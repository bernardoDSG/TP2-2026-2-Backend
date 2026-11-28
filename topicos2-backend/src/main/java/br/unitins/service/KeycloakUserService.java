package br.unitins.service;

import java.util.List;
import java.util.Locale;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
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

    private static final Logger LOG = Logger.getLogger(KeycloakUserService.class);

    @Inject
    Keycloak keycloak;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    String realmName;

    public void register(CadastroUsuarioRequestDTO request) {
        RealmResource realm = keycloak.realm(realmName);
        UserRepresentation user = toUserRepresentation(request);

        try (Response response = realm.users().create(user)) {
            if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new WebApplicationException("Este e-mail já possui uma conta.", Response.Status.CONFLICT);
            }
            if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                LOG.warn("Keycloak rejected the user registration data with HTTP 400.");
                throw new WebApplicationException(
                        "O Keycloak rejeitou os dados do cadastro. Confira nome, e-mail e senha.",
                        Response.Status.BAD_REQUEST);
            }
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                LOG.warnf("Keycloak user registration failed with HTTP %d.", response.getStatus());
                throw unavailable("O Keycloak não conseguiu criar a conta.", null);
            }
        } catch (WebApplicationException exception) {
            if (exception.getResponse() != null
                    && exception.getResponse().getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new WebApplicationException("Este e-mail já possui uma conta.", Response.Status.CONFLICT);
            }
            if (exception.getResponse() != null
                    && exception.getResponse().getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                throw exception;
            }
            LOG.warn("Keycloak user registration request failed.", exception);
            throw unavailable("O Keycloak não conseguiu criar a conta.", exception);
        } catch (RuntimeException exception) {
            LOG.warn("Could not communicate with Keycloak while registering a user.", exception);
            throw unavailable("Não foi possível conectar ao Keycloak.", exception);
        }
    }

    UserRepresentation toUserRepresentation(CadastroUsuarioRequestDTO request) {
        String normalizedName = request.nome().trim().replaceAll("\\s+", " ");
        int lastSpace = normalizedName.lastIndexOf(' ');
        String firstName = lastSpace < 0 ? normalizedName : normalizedName.substring(0, lastSpace);
        String lastName = lastSpace < 0 ? normalizedName : normalizedName.substring(lastSpace + 1);
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setRequiredActions(List.of());

        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(request.senha());
        password.setTemporary(false);
        user.setCredentials(List.of(password));
        return user;
    }

    private ServiceUnavailableException unavailable(String message, Throwable cause) {
        Response response = Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).build();
        return new ServiceUnavailableException(response, cause);
    }
}
