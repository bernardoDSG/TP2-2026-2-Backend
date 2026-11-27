package br.unitins;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AuthAuthorizationTest {

    @Test
    void customerAdministrationRequiresAuthentication() {
        given()
                .when().get("/clientes")
                .then().statusCode(401);
    }

    @Test
    void sessionEndpointReturnsUnauthorizedWithoutAuthentication() {
        given()
                .when().get("/auth/me")
                .then().statusCode(401);
    }

    @Test
    void loginRejectsInvalidRequestData() {
        given()
                .contentType("application/json")
                .body("{\"email\":\"nao-e-email\",\"senha\":\"\"}")
                .when().post("/auth/login")
                .then().statusCode(400);
    }

    @Test
    @io.quarkus.test.security.TestSecurity(user = "cliente", roles = "CLIENTE")
    void customerRoleCannotManageCustomerRecords() {
        given()
                .when().get("/clientes")
                .then().statusCode(403);
    }

    @Test
    void carCatalogRemainsPublic() {
        given()
                .when().get("/carros")
                .then().statusCode(200);
    }
}
