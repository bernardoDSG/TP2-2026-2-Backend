package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class EnderecoResourceTest {

    @Test
    void managesAddressOwnedByCustomer() {
        Number customerId = given()
                .when().get("/clientes")
                .then().statusCode(200)
                .extract().path("[0].id");
        Number municipalityId = given()
                .when().get("/municipios")
                .then().statusCode(200)
                .extract().path("[0].id");

        Number addressId = given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "cep", "77001001",
                        "logradouro", "Rua de teste",
                        "numero", "10",
                        "bairro", "Centro",
                        "municipioId", municipalityId.longValue(),
                        "clienteId", customerId.longValue()))
                .when().post("/enderecos")
                .then()
                .statusCode(200)
                .body("clienteId", is(customerId.intValue()))
                .extract().path("id");

        given()
                .when().get("/enderecos/" + addressId)
                .then().statusCode(200)
                .body("logradouro", is("Rua de teste"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "cep", "77001002",
                        "logradouro", "Avenida de teste",
                        "numero", "11",
                        "bairro", "Centro",
                        "municipioId", municipalityId.longValue(),
                        "clienteId", customerId.longValue()))
                .when().put("/enderecos/" + addressId)
                .then().statusCode(200)
                .body("logradouro", is("Avenida de teste"));

        given()
                .when().delete("/enderecos/" + addressId)
                .then().statusCode(204);
    }
}
