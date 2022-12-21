package rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

public class EnviaDadosTest {

    @Test
    public void deveEnviarValorViaQuery(){
        RestAssured.given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/v2/users?format=json")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
        ;
    }

    @Test
    public void deveEnviarValorViaQueryViaParam(){
        RestAssured.given()
                    .log().all()
                    .queryParam("format","xml")
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.XML)
                    .contentType(Matchers.containsString("utf-8"))
        ;
    }

    @Test
    public void deveEnviarValorViaHeader(){
        RestAssured.given()
                    .log().all()
                    .accept(ContentType.JSON)
                .when()
                    .get("https://restapi.wcaquino.me/v2/users")
                .then()
                    .log().all()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
        ;
    }
}
