package br.unitins.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.representations.AccessTokenResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class KeycloakSessionService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    public AccessTokenResponse login(String email, String password) {
        Map<String, String> form = clientCredentials();
        form.put("grant_type", "password");
        form.put("username", email.trim().toLowerCase());
        form.put("password", password);
        return tokenRequest("/protocol/openid-connect/token", form);
    }

    public AccessTokenResponse refresh(String refreshToken) {
        Map<String, String> form = clientCredentials();
        form.put("grant_type", "refresh_token");
        form.put("refresh_token", refreshToken);
        return tokenRequest("/protocol/openid-connect/token", form);
    }

    public void logout(String refreshToken) {
        Map<String, String> form = clientCredentials();
        form.put("refresh_token", refreshToken);
        try {
            HttpResponse<String> response = postForm("/protocol/openid-connect/logout", form);
            if (response.statusCode() == 400 || response.statusCode() == 401) {
                return;
            }
            if (response.statusCode() != 204) {
                throw unavailable("O Keycloak não conseguiu encerrar a sessão.");
            }
        } catch (ServiceUnavailableException exception) {
            throw exception;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw unavailable("A comunicação com o Keycloak foi interrompida.", exception);
        } catch (IOException exception) {
            throw unavailable("Não foi possível conectar ao Keycloak.", exception);
        }
    }

    private AccessTokenResponse tokenRequest(String path, Map<String, String> form) {
        try {
            HttpResponse<String> response = postForm(path, form);
            if (response.statusCode() == 400 || response.statusCode() == 401) {
                throw new NotAuthorizedException("Credenciais inválidas.");
            }
            if (response.statusCode() != 200) {
                throw unavailable("O Keycloak não conseguiu autenticar a conta.");
            }
            return objectMapper.readValue(response.body(), AccessTokenResponse.class);
        } catch (NotAuthorizedException | ServiceUnavailableException exception) {
            throw exception;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw unavailable("A comunicação com o Keycloak foi interrompida.", exception);
        } catch (IOException exception) {
            throw unavailable("Não foi possível conectar ao Keycloak.", exception);
        }
    }

    private Map<String, String> clientCredentials() {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("client_id", clientId);
        form.put("client_secret", clientSecret);
        return form;
    }

    private HttpResponse<String> postForm(String path, Map<String, String> form)
            throws IOException, InterruptedException {
        String encodedForm = form.entrySet().stream()
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint(path)))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(encodedForm))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String endpoint(String path) {
        String baseUrl = authServerUrl.endsWith("/")
                ? authServerUrl.substring(0, authServerUrl.length() - 1)
                : authServerUrl;
        return baseUrl + path;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private ServiceUnavailableException unavailable(String message) {
        return new ServiceUnavailableException(message);
    }

    private ServiceUnavailableException unavailable(String message, Throwable cause) {
        Response response = Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).build();
        return new ServiceUnavailableException(response, cause);
    }
}
