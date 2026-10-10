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
        Number carBrandId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Marca para teste de cor"))
            .when().post("/marcas")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number colorId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Cor temporaria para exclusao", "tonalidadeId", 1))
            .when().post("/cores")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number categoriaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Categoria para teste de cor"))
            .when().post("/categorias")
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
            .body(Map.of("nome", "Carro temporario para exclusao", "StatusUsoId", 1,
                    "corId", colorId.longValue(), "marcaId", carBrandId.longValue(),
                    "categoriaId", categoriaId.longValue()))
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
            .body("nome", is("Marca para teste de cor Carro temporario para exclusao"));

        given()
            .when().delete("/carros/" + carId)
            .then()
            .statusCode(204);

        given()
            .when().delete("/cores/" + replacementColorId)
            .then()
            .statusCode(204);

        given()
            .when().delete("/marcas/" + carBrandId)
            .then()
            .statusCode(204);

        given()
            .when().delete("/categorias/" + categoriaId)
            .then()
            .statusCode(204);
    }
}