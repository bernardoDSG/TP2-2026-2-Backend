package br.unitins;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

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
          "cep": "77000000",
          "logradouro": "Avenida Tocantins",
          "numero": "100",
          "complemento": "",
          "bairro": "Centro",
          "municipio": "Palmas",
          "estadoNome": "Tocantins",
          "estadoSigla": "TO"
        }
        """;

    @Test
    void createsClientWithStructuredAddressAndRejectsDuplicateCpf() {
        given()
            .contentType(ContentType.JSON)
            .body(CLIENTE_JSON)
            .when().post("/clientes")
            .then()
            .statusCode(200)
            .body("nome", is("Maria da Silva"))
            .body("municipio.nome", is("Palmas"))
            .body("municipio.estado.sigla", is("TO"));

        given()
            .contentType(ContentType.JSON)
            .body(CLIENTE_JSON)
            .when().post("/clientes")
            .then()
            .statusCode(409);
    }

    @Test
    void rejectsMalformedCep() {
        String invalidCep = CLIENTE_JSON.replace("77000000", "77000");

        given()
            .contentType(ContentType.JSON)
            .body(invalidCep)
            .when().post("/clientes")
            .then()
            .statusCode(400);
    }

    @Test
    void rejectsInvalidCpfDigits() {
        String invalidCpf = CLIENTE_JSON.replace("52998224725", "00000000000");

        given()
            .contentType(ContentType.JSON)
            .body(invalidCpf)
            .when().post("/clientes")
            .then()
            .statusCode(400);
    }
}