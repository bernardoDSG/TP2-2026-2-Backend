package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class CategoriaResourceTest {

    @Test
    void managesCategoriaAndItsCarRelationship() {
        Number categoriaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Categoria temporaria"))
            .when().post("/categorias")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number replacementCategoriaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Categoria substituta"))
            .when().post("/categorias")
            .then()
            .statusCode(200)
            .extract().path("id");

        given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Categoria temporaria atualizada"))
            .when().put("/categorias/" + categoriaId)
            .then()
            .statusCode(204);

        given()
            .when().get("/categorias/nome/atualizada")
            .then()
            .statusCode(200)
            .body("[0].nome", is("Categoria temporaria atualizada"));

        Number marcaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Marca para teste de categoria"))
            .when().post("/marcas")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number corId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Cor para teste de categoria", "tonalidadeId", 1))
            .when().post("/cores")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number carroId = given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                    "nome", "Carro da categoria",
                    "StatusUsoId", 1,
                    "corId", corId.longValue(),
                    "marcaId", marcaId.longValue(),
                    "categoriaId", categoriaId.longValue()))
            .when().post("/carros")
            .then()
            .statusCode(200)
            .body("categoriaId", is(categoriaId.intValue()))
            .body("categoriaNome", is("Categoria temporaria atualizada"))
            .extract().path("id");

        given()
            .when().get("/carros/categoria/" + categoriaId)
            .then()
            .statusCode(200)
            .body("[0].categoriaNome", is("Categoria temporaria atualizada"));

        given()
            .when().delete("/categorias/" + categoriaId)
            .then()
            .statusCode(409);

        given()
            .queryParam("replacementCategoriaId", replacementCategoriaId)
            .when().delete("/categorias/" + categoriaId)
            .then()
            .statusCode(204);

        given()
            .when().get("/carros/" + carroId)
            .then()
            .statusCode(200)
            .body("categoriaId", is(replacementCategoriaId.intValue()))
            .body("categoriaNome", is("Categoria substituta"));

        given().when().delete("/carros/" + carroId).then().statusCode(204);
        given().when().delete("/cores/" + corId).then().statusCode(204);
        given().when().delete("/marcas/" + marcaId).then().statusCode(204);
        given().when().delete("/categorias/" + replacementCategoriaId).then().statusCode(204);
    }
}
