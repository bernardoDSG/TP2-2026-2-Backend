package br.unitins.resource;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import br.unitins.dto.CadastroUsuarioRequestDTO;
import br.unitins.dto.LoginRequestDTO;
import br.unitins.dto.UsuarioAutenticadoResponseDTO;
import br.unitins.service.KeycloakSessionService;
import br.unitins.service.KeycloakUserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.annotation.security.PermitAll;
import org.keycloak.representations.AccessTokenResponse;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    SecurityIdentity identity;

    @Inject
    KeycloakUserService keycloakUserService;

    @Inject
    KeycloakSessionService keycloakSessionService;

    @ConfigProperty(name = "app.frontend-url")
    String frontendUrl;

    @ConfigProperty(name = "app.auth.cookie-secure")
    boolean secureCookies;

    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequestDTO request) {
        return withSessionCookies(Response.noContent(), keycloakSessionService.login(request.email(), request.senha()))
                .build();
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public Response refresh(@CookieParam("refresh_token") String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return clearSessionCookies(Response.status(Response.Status.UNAUTHORIZED)).build();
        }
        try {
            return withSessionCookies(Response.noContent(), keycloakSessionService.refresh(refreshToken)).build();
        } catch (NotAuthorizedException exception) {
            return clearSessionCookies(Response.status(Response.Status.UNAUTHORIZED)).build();
        }
    }

    @GET
    @Path("/me")
    @PermitAll
    public UsuarioAutenticadoResponseDTO currentUser() {
        if (identity.isAnonymous()) {
            throw new NotAuthorizedException("Sessão não autenticada.");
        }

        String username = identity.getPrincipal().getName();
        return new UsuarioAutenticadoResponseDTO(
                identity.getAttribute("sub"),
                username,
                identity.getAttribute("name"),
                identity.getAttribute("email"),
                Set.copyOf(new TreeSet<>(identity.getRoles())));
    }

    @POST
    @Path("/register")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Valid CadastroUsuarioRequestDTO request) {
        keycloakUserService.register(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/logout")
    @PermitAll
    public Response logout(@CookieParam("refresh_token") String refreshToken) {
        try {
            if (refreshToken != null && !refreshToken.isBlank()) {
                keycloakSessionService.logout(refreshToken);
            }
            return clearSessionCookies(Response.seeOther(java.net.URI.create(frontendUrl + "/"))).build();
        } catch (jakarta.ws.rs.ServiceUnavailableException exception) {
            return clearSessionCookies(Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Não foi possível encerrar a sessão no Keycloak.")).build();
        }
    }

    private Response.ResponseBuilder withSessionCookies(Response.ResponseBuilder response,
            AccessTokenResponse tokens) {
        response.header("Set-Cookie", sessionCookie("access_token", tokens.getToken(), tokens.getExpiresIn()));
        response.header("Set-Cookie",
                sessionCookie("refresh_token", tokens.getRefreshToken(), tokens.getRefreshExpiresIn()));
        return response;
    }

    private Response.ResponseBuilder clearSessionCookies(Response.ResponseBuilder response) {
        response.header("Set-Cookie", sessionCookie("access_token", "", 0));
        response.header("Set-Cookie", sessionCookie("refresh_token", "", 0));
        return response;
    }

    private String sessionCookie(String name, String value, long maxAge) {
        return name + "=" + value + "; Max-Age=" + Math.max(0L, maxAge)
                + "; Path=/; HttpOnly; SameSite=Strict" + (secureCookies ? "; Secure" : "");
    }
}
