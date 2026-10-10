package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class MarcaResourceTest {

    @Test
    void managesMarcaAndItsCarRelationship() {
        Number marcaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Marca temporaria"))
            .when().post("/marcas")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number replacementMarcaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Marca substituta"))
            .when().post("/marcas")
            .then()
            .statusCode(200)
            .extract().path("id");

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Marca temporaria atualizada"))
            .when().put("/marcas/" + marcaId)
            .then()
            .statusCode(204);

        given()
            .when().get("/marcas/nome/atualizada")
            .then()
            .statusCode(200)
            .body("[0].nome", is("Marca temporaria atualizada"));

        Number corId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Cor para marca de teste", "tonalidadeId", 1))
            .when().post("/cores")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number categoriaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Categoria para teste de marca"))
            .when().post("/categorias")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number carroId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Carro da marca de teste", "StatusUsoId", 1,
                    "corId", corId.longValue(), "marcaId", marcaId.longValue(),
                    "categoriaId", categoriaId.longValue()))
            .when().post("/carros")
            .then()
            .statusCode(200)
            .extract().path("id");

        given()
            .when().get("/carros/marca/" + marcaId)
            .then()
            .statusCode(200)
            .body("[0].nome", is("Marca temporaria atualizada Carro da marca de teste"));

        given()
            .when().delete("/marcas/" + marcaId)
            .then()
            .statusCode(409);

        given()
            .queryParam("replacementMarcaId", replacementMarcaId)
            .when().delete("/marcas/" + marcaId)
            .then()
            .statusCode(204);

        given()
            .when().get("/carros/" + carroId)
            .then()
            .statusCode(200)
            .body("nome", is("Marca substituta Carro da marca de teste"));

        given().when().delete("/carros/" + carroId).then().statusCode(204);
        given().when().delete("/cores/" + corId).then().statusCode(204);
        given().when().delete("/marcas/" + replacementMarcaId).then().statusCode(204);
        given().when().delete("/categorias/" + categoriaId).then().statusCode(204);
    }
}
