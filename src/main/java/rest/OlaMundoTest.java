package rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class OlaMundoTest {

    @Test
    public void testOlaMundo(){
        Response response = RestAssured.request(Method.GET,"http://restapi.wcaquino.me/ola");
        Assert.assertEquals("Ola Mundo!",response.getBody().asString() );
        Assert.assertEquals(200,response.statusCode());
        System.out.println("Resposta: "+response.getBody().asString().equals("Ola Mundo!"));
        System.out.println(response.statusCode() == 200);

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test
    public void devoConhecerOutrasFormasRestAssured(){
        Response response = RestAssured.request(Method.GET,"http://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        RestAssured.get("http://restapi.wcaquino.me/ola").then().statusCode(200);

        RestAssured.given()
                //Pré condições
                .when() //Ação
                    .get("http://restapi.wcaquino.me/ola")
                .then()//Assertivas
                    .statusCode(200);
    }

    @Test
    public void devoConhecerMatchersHancrest(){
        Assert.assertThat("Maria", Matchers.is("Maria"));
        Assert.assertThat(128,Matchers.is(128));
        Assert.assertThat(128,Matchers.isA(Integer.class));
        Assert.assertThat(128d, Matchers.isA(Double.class));
        Assert.assertThat(128d, Matchers.greaterThan(120d));
        Assert.assertThat(128d, Matchers.lessThan(130d));

        List<Integer> impares = Arrays.asList(1,3,5,7,9);
        Assert.assertThat(impares, Matchers.hasSize(5));
        Assert.assertThat(impares, Matchers.contains(1,3,5,7,9));
        Assert.assertThat(impares, Matchers.containsInAnyOrder(1,3,5,9,7));

        Assert.assertThat("Maria", Matchers.is(Matchers.not("João")));
        Assert.assertThat("Maria", Matchers.not("João"));
        Assert.assertThat("Maria", Matchers.anyOf(Matchers.is("Maria"),Matchers.is("Joaquina")));
        Assert.assertThat("Joaquina",Matchers.allOf(Matchers.startsWith("Joa"), Matchers.endsWith("ina"),Matchers.containsString("qui")));
    }

    @Test
    public void devaValidarBody(){
        RestAssured.given()
                .when()
                .get("http://restapi.wcaquino.me/ola")
                .then()
                .statusCode(200)
                .body(Matchers.is("Ola Mundo!"))
                .body(Matchers.containsString("Mundo"))
                .body(Matchers.is(Matchers.notNullValue()));
    }
}
