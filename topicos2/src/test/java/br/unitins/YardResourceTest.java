package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class YardResourceTest {

    @Test
    void managesSectorsAndParkingSlotsWithCarAssignments() {
        Number sectorId = given()
                .contentType(ContentType.JSON)
                .body(Map.of("nome", "Setor de teste", "linhas", 2, "colunas", 2))
                .when().post("/setores")
                .then()
                .statusCode(200)
                .extract().path("id");

        Map<String, Object> sampleCar = given()
                .queryParam("pageSize", 100)
                .when().get("/carros")
                .then().statusCode(200)
                .extract().path("[0]");
        Number carId = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "nome", "Carro de teste",
                        "StatusUsoId", 1,
                        "corId", ((Number) sampleCar.get("corId")).longValue(),
                        "marcaId", ((Number) sampleCar.get("marcaId")).longValue(),
                        "categoriaId", ((Number) sampleCar.get("categoriaId")).longValue()))
                .when().post("/carros")
                .then().statusCode(200)
                .extract().path("id");

        Number slotId = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoVaga", "T-01",
                        "linha", 2,
                        "coluna", 2,
                        "tipo", "COBERTA",
                        "setorId", sectorId.longValue(),
                        "carroId", carId.longValue()))
                .when().post("/vagas")
                .then()
                .statusCode(200)
                .body("carroId", is(carId.intValue()))
                .body("setorNome", is("Setor de teste"))
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "codigoVaga", "T-01-ATUALIZADA",
                        "linha", 2,
                        "coluna", 2,
                        "tipo", "NORMAL",
                        "setorId", sectorId.longValue(),
                        "carroId", carId.longValue()))
                .when().put("/vagas/" + slotId)
                .then()
                .statusCode(200)
                .body("codigoVaga", is("T-01-ATUALIZADA"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("nome", "Setor de teste", "linhas", 1, "colunas", 1))
                .when().put("/setores/" + sectorId)
                .then().statusCode(409);

        given()
                .when().delete("/setores/" + sectorId)
                .then().statusCode(409);

        given()
                .when().delete("/vagas/" + slotId)
                .then().statusCode(204);

        given()
                .when().delete("/setores/" + sectorId)
                .then().statusCode(204);

        given()
                .when().delete("/carros/" + carId)
                .then().statusCode(204);
    }
}
