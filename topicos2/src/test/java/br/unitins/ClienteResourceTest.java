package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class ClienteResourceTest {

    private static final String CLIENTE_JSON = """
        {
          "nome": "Maria da Silva",
          "cpf": "52998224725",
          "email": "maria@example.com",
          "telefone": "63999990000",
                    "enderecos": [
                        { "cep": "77000000", "logradouro": "Avenida Tocantins", "numero": "100", "complemento": "", "bairro": "Centro", "municipioId": 0 },
                        { "cep": "77001000", "logradouro": "Rua dos Testes", "numero": "200", "complemento": "Sala 2", "bairro": "Plano Diretor", "municipioId": 0 }
                    ]
        }
        """;

    private Number municipioId;

    @BeforeEach
    void ensureStructuredLocation() {
        List<Map<String, Object>> estados = given().when().get("/estados").then().statusCode(200).extract().jsonPath().getList("");
        Number estadoId = estados.stream()
            .filter(estado -> "TO".equals(estado.get("sigla")))
            .map(estado -> (Number) estado.get("id"))
            .findFirst()
            .orElseGet(() -> given()
                .contentType(ContentType.JSON)
                .body(Map.of("nome", "Tocantins", "sigla", "TO"))
                .when().post("/estados")
                .then().statusCode(200)
                .extract().path("id"));

        List<Map<String, Object>> municipios = given().when().get("/municipios").then().statusCode(200).extract().jsonPath().getList("");
        municipioId = municipios.stream()
            .filter(municipio -> "Palmas".equals(municipio.get("nome")) && ((Map<?, ?>) municipio.get("estado")).get("id").toString().equals(estadoId.toString()))
            .map(municipio -> (Number) municipio.get("id"))
            .findFirst()
            .orElseGet(() -> given()
                .contentType(ContentType.JSON)
                .body(Map.of("nome", "Palmas", "estadoId", estadoId.longValue()))
                .when().post("/municipios")
                .then().statusCode(200)
                .extract().path("id"));
    }

    private String clienteJson() {
        return CLIENTE_JSON.replace("\"municipioId\": 0", "\"municipioId\": " + municipioId);
    }

    @Test
    void createsClientWithStructuredAddressAndRejectsDuplicateCpf() {
        Number id = given()
            .contentType(ContentType.JSON)
            .body(clienteJson())
            .when().post("/clientes")
            .then()
            .statusCode(200)
            .body("nome", is("Maria da Silva"))
            .body("enderecos[0].municipio.nome", is("Palmas"))
            .body("enderecos[0].municipio.estado.sigla", is("TO"))
            .body("enderecos.size()", is(2))
            .extract().path("id");

        given()
            .contentType(ContentType.JSON)
            .body(clienteJson())
            .when().post("/clientes")
            .then()
            .statusCode(409);

        given()
            .when().get("/clientes/" + id)
            .then()
            .statusCode(200)
            .body("nome", is("Maria da Silva"));

        String updatedClient = clienteJson().replace("Maria da Silva", "Maria Souza");
        given()
            .contentType(ContentType.JSON)
            .body(updatedClient)
            .when().put("/clientes/" + id)
            .then()
            .statusCode(200)
            .body("nome", is("Maria Souza"));

        given()
            .when().delete("/clientes/" + id)
            .then()
            .statusCode(204);

        given()
            .when().get("/clientes/" + id)
            .then()
            .statusCode(404);
    }

    @Test
    void rejectsMalformedCep() {
        String invalidCep = clienteJson().replace("\"cep\": \"77000000\"", "\"cep\": \"77000\"");

        given()
            .contentType(ContentType.JSON)
            .body(invalidCep)
            .when().post("/clientes")
            .then()
            .statusCode(400);
    }

    @Test
    void rejectsInvalidCpfDigits() {
        String invalidCpf = clienteJson().replace("52998224725", "00000000000");

        given()
            .contentType(ContentType.JSON)
            .body(invalidCpf)
            .when().post("/clientes")
            .then()
            .statusCode(400);
    }
}