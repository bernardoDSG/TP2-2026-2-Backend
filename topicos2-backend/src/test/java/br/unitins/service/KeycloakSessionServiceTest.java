package br.unitins.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import jakarta.ws.rs.NotAuthorizedException;

class KeycloakSessionServiceTest {

    private HttpServer server;
    private KeycloakSessionService service;
    private final AtomicReference<String> requestBody = new AtomicReference<>();
    private volatile int responseStatus;

    @BeforeEach
    void startStubServer() throws IOException {
        responseStatus = 200;
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/realms/ecommerce/protocol/openid-connect/token", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] response = responseStatus == 200
                    ? """
                            {"access_token":"access-token","expires_in":300,
                             "refresh_expires_in":1800,"refresh_token":"refresh-token",
                             "token_type":"Bearer"}
                            """.getBytes(StandardCharsets.UTF_8)
                    : "{\"error\":\"invalid_grant\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(responseStatus, response.length);
            try (var body = exchange.getResponseBody()) {
                body.write(response);
            }
        });
        server.start();

        service = new KeycloakSessionService();
        service.authServerUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/realms/ecommerce";
        service.clientId = "ecommerce-backend";
        service.clientSecret = "test-client-secret";
        service.objectMapper = new ObjectMapper();
    }

    @AfterEach
    void stopStubServer() {
        server.stop(0);
    }

    @Test
    void loginUsesPasswordGrantAndParsesTokens() {
        var tokens = service.login("USER@example.com", "p&ss=");

        assertEquals("access-token", tokens.getToken());
        assertEquals("refresh-token", tokens.getRefreshToken());
        assertTrue(requestBody.get().contains("grant_type=password"));
        assertTrue(requestBody.get().contains("username=user%40example.com"));
        assertTrue(requestBody.get().contains("password=p%26ss%3D"));
        assertTrue(requestBody.get().contains("client_secret=test-client-secret"));
    }

    @Test
    void invalidCredentialsBecomeUnauthorized() {
        responseStatus = 400;

        assertThrows(NotAuthorizedException.class, () -> service.login("user@example.com", "wrong-password"));
    }
}
