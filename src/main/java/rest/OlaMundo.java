package rest;


import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;

public class OlaMundo {

    public static void main(String[] args) {
       Response response = RestAssured.request(Method.GET,"http://restapi.wcaquino.me/ola");
       Assert.assertEquals("Ola Mundo!",response.getBody().asString() );
       Assert.assertEquals(200,response.statusCode());
       System.out.println("Resposta: "+response.getBody().asString().equals("Ola Mundo!"));
       System.out.println(response.statusCode() == 200);

        ValidatableResponse validacao = response.then();
        validacao.statusCode(201);
    }
}
