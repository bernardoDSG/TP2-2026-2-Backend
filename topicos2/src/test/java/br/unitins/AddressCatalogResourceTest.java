package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class AddressCatalogResourceTest {

    @Test
    void managesStatesAndMunicipalitiesWithStructuredRelations() {
        Number estadoId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Estado de teste", "sigla", "ZZ"))
            .when().post("/estados")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number municipioId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Município de teste", "estadoId", estadoId.longValue()))
            .when().post("/municipios")
            .then()
            .statusCode(200)
            .body("estado.sigla", is("ZZ"))
            .extract().path("id");

        given()
            .when().get("/municipios/" + municipioId)
            .then()
            .statusCode(200)
            .body("estado.nome", is("Estado de teste"));

        given()
            .when().delete("/estados/" + estadoId)
            .then()
            .statusCode(409);

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Município atualizado", "estadoId", estadoId.longValue()))
            .when().put("/municipios/" + municipioId)
            .then()
            .statusCode(200)
            .body("nome", is("Município atualizado"));

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Estado atualizado", "sigla", "ZZ"))
            .when().put("/estados/" + estadoId)
            .then()
            .statusCode(200)
            .body("nome", is("Estado atualizado"));

        given()
            .when().delete("/municipios/" + municipioId)
            .then()
            .statusCode(204);

        given()
            .when().delete("/estados/" + estadoId)
            .then()
            .statusCode(204);
    }
}