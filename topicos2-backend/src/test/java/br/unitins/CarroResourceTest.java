package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
class CarroResourceTest {

    @Inject
    InMemoryObjectStorageService objectStorageService;

    @Test
    void rejectsInvalidCatalogDataAndPreservesExistingCar() {
        var payload = new java.util.HashMap<String, Object>();
        payload.put("nome", "Modelo de regressão");
        payload.put("StatusUsoId", 1);
        payload.put("corId", 1);
        payload.put("marcaId", 1);
        payload.put("categoriaId", 1);
        String id = given().contentType(ContentType.JSON).body(payload).post("/carros")
                .then().statusCode(200).extract().path("id").toString();
        try {
            payload.put("nome", "Cadastro inválido de regressão");
            for (String campo : List.of("StatusUsoId", "corId")) {
                for (Integer invalido : java.util.Arrays.asList(-1, null)) {
                    payload.put(campo, invalido);
                    given().contentType(ContentType.JSON).body(payload).post("/carros").then().statusCode(400);
                    given().contentType(ContentType.JSON).body(payload).put("/carros/" + id).then().statusCode(400);
                }
                payload.put(campo, 1);
            }
            given().get("/carros/" + id).then().statusCode(200)
                    .body("nomeCarro", is("Modelo de regressão")).body("statusUsoId", is(1)).body("corId", is(1));
            given().pathParam("nome", payload.get("nome")).get("/carros/nome/{nome}")
                    .then().statusCode(200).body("size()", is(0));
        } finally {
            given().delete("/carros/" + id).then().statusCode(204);
        }
        given().get("/carros/" + id).then().statusCode(404);
        given().contentType(ContentType.JSON).body(payload).put("/carros/" + id).then().statusCode(404);
    }

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
    void returnsCarFieldsAndRelationshipIdsForEditing() throws Exception {
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
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(2)) {
            var start = new java.util.concurrent.CountDownLatch(1);
            java.util.concurrent.Callable<Integer> upload = () -> {
                start.await();
                return given().multiPart("imagem", "carro.png", pngHeader, "image/png")
                        .post("/carros/" + carId + "/imagem").statusCode();
            };
            var first = executor.submit(upload);
            var second = executor.submit(upload);
            start.countDown();
            org.junit.jupiter.api.Assertions.assertEquals(204, first.get());
            org.junit.jupiter.api.Assertions.assertEquals(204, second.get());
        }
        assertTrue(objectStorageService.containsObject("carros/" + carId + "/imagem"));

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
            .when().get("/carros/" + carId + "/imagem/download")
            .then()
            .statusCode(200)
            .contentType("image/png")
            .header("Content-Disposition", is("attachment; filename=\"carro-" + carId + ".png\""));

        given()
            .multiPart("imagem", "invalida.png", new byte[] { 1, 2, 3 }, "image/png")
            .when().post("/carros/" + carId + "/imagem")
            .then()
            .statusCode(400);

        given()
            .when().delete("/carros/" + carId + "/imagem")
            .then()
            .statusCode(204);
        assertFalse(objectStorageService.containsObject("carros/" + carId + "/imagem"));

        given()
            .when().get("/carros/" + carId + "/imagem")
            .then()
            .statusCode(404);

        given()
            .multiPart("imagem", "carro.png", pngHeader, "image/png")
            .when().post("/carros/" + carId + "/imagem")
            .then()
            .statusCode(204);
        given()
            .when().delete("/carros/" + carId)
            .then()
            .statusCode(204);
        assertFalse(objectStorageService.containsObject("carros/" + carId + "/imagem"));
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
