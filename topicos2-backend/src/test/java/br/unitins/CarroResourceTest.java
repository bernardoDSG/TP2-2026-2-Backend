package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class CarroResourceTest {

    @Test
    void importedCarsHaveBundledPhotos() {
        for (String model : List.of("Corolla", "Civic", "Gol", "Onix", "HB20", "Argo", "Renegade", "Ka", "Kicks", "Kwid")) {
            Number carId = given()
                .when().get("/carros/nome/" + model)
                .then()
                .statusCode(200)
                .body("[0].imagemUrl", is(notNullValue()))
                .extract().path("[0].id");

            given()
                .when().get("/carros/" + carId + "/imagem")
                .then()
                .statusCode(200)
                .contentType("image/png");
        }
    }

    @Test
    void returnsCarFieldsAndRelationshipIdsForEditing() {
        Number categoriaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Categoria para teste de resposta"))
            .when().post("/categorias")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number marcaId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Marca para teste de resposta"))
            .when().post("/marcas")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number corId = given()
            .contentType(ContentType.JSON)
            .body(Map.of("nome", "Cor para teste de resposta", "tonalidadeId", 2))
            .when().post("/cores")
            .then()
            .statusCode(200)
            .extract().path("id");

        Number carId = given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "nome", "Modelo para teste de resposta",
                "StatusUsoId", 1,
                "corId", corId.longValue(),
                "marcaId", marcaId.longValue(),
                "categoriaId", categoriaId.longValue()))
            .when().post("/carros")
            .then()
            .statusCode(200)
            .body("nomeCarro", is("Modelo para teste de resposta"))
            .body("marcaId", is(marcaId.intValue()))
            .body("categoriaId", is(categoriaId.intValue()))
            .body("corId", is(corId.intValue()))
            .body("statusUsoId", is(1))
            .extract().path("id");

        byte[] pngHeader = new byte[] {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
        };
        given()
            .multiPart("imagem", "carro.png", pngHeader, "image/png")
            .when().post("/carros/" + carId + "/imagem")
            .then()
            .statusCode(204);

        given()
            .when().get("/carros/" + carId)
            .then()
            .statusCode(200)
            .body("nome", is("Marca para teste de resposta Modelo para teste de resposta"))
            .body("nomeCarro", is("Modelo para teste de resposta"))
            .body("statusUso", is("SEMINOVO"))
            .body("statusUsoId", is(1))
            .body("marcaId", is(marcaId.intValue()))
            .body("marcaNome", is("Marca para teste de resposta"))
            .body("categoriaId", is(categoriaId.intValue()))
            .body("categoriaNome", is("Categoria para teste de resposta"))
            .body("corId", is(corId.intValue()))
            .body("corNome", is("Cor para teste de resposta"))
            .body("tonalidade", is("Escuro"))
            .body("imagemUrl", is("/carros/" + carId + "/imagem"));

        given()
            .contentType(ContentType.JSON)
            .body(Map.of(
                "nome", "Modelo atualizado para teste",
                "StatusUsoId", 2,
                "corId", corId.longValue(),
                "marcaId", marcaId.longValue(),
                "categoriaId", categoriaId.longValue()))
            .when().put("/carros/" + carId)
            .then()
            .statusCode(204);

        given()
            .when().get("/carros/" + carId)
            .then()
            .statusCode(200)
            .body("nome", is("Marca para teste de resposta Modelo atualizado para teste"))
            .body("nomeCarro", is("Modelo atualizado para teste"))
            .body("statusUso", is("USADO"))
            .body("statusUsoId", is(2))
            .body("marcaId", is(marcaId.intValue()))
            .body("corId", is(corId.intValue()));

        given()
            .when().get("/carros/" + carId + "/imagem")
            .then()
            .statusCode(200)
            .contentType("image/png")
            .header("Content-Length", is(String.valueOf(pngHeader.length)));

        given()
            .when().delete("/carros/" + carId + "/imagem")
            .then()
            .statusCode(204);

        given()
            .when().delete("/carros/" + carId)
            .then()
            .statusCode(204);
        given()
            .when().delete("/cores/" + corId)
            .then()
            .statusCode(204);
        given()
            .when().delete("/marcas/" + marcaId)
            .then()
            .statusCode(204);
        given()
            .when().delete("/categorias/" + categoriaId)
            .then()
            .statusCode(204);
    }
}
