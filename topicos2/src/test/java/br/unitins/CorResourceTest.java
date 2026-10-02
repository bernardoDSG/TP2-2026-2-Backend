package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class CorResourceTest {

    @Test
    void deletesColorAfterReassigningLinkedCars() {
        Number colorId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Cor temporaria para exclusao", "tonalidadeId", 1))
            .when().post("/cores")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number replacementColorId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Cor substituta de teste", "tonalidadeId", 2))
            .when().post("/cores")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number carId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Carro temporario para exclusao", "StatusUsoId", 1, "corId", colorId.longValue()))
            .when().post("/carros")
            .then()
            .statusCode(200)
            .extract().path("id");

        given()
            .when().delete("/cores/" + colorId)
            .then()
            .statusCode(409);

        given()
            .queryParam("replacementColorId", replacementColorId)
            .when().delete("/cores/" + colorId)
            .then()
            .statusCode(204);

        given()
            .when().get("/carros/" + carId)
            .then()
            .statusCode(200)
            .body("cor.nome", is("Cor substituta de teste"));

        given()
            .when().delete("/carros/" + carId)
            .then()
            .statusCode(204);

        given()
            .when().delete("/cores/" + replacementColorId)
            .then()
            .statusCode(204);
    }
}