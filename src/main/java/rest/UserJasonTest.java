package rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class UserJasonTest {

    @Test
    public void deveVerificarPrimeiroNivel(){
        RestAssured.given()
                .when()
                    .get("http://restapi.wcaquino.me/users/1")
                .then()
                    .statusCode(200)
                    .body("id", Matchers.is(1))
                    .body("name", Matchers.containsString("Silva"))
                    .body("age",Matchers.greaterThan(18))
                ;
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas(){
        Response response = RestAssured.request(Method.GET,"http://restapi.wcaquino.me/users/1");

        //path
        Assert.assertEquals(new Integer(1),response.path("id"));
        Assert.assertEquals(new Integer(1),response.path("%s","id"));
        System.out.println(response.path("id").toString());

        //jsonPath
        JsonPath jpath = new JsonPath(response.asString());
        Assert.assertEquals(1,jpath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1,id);
    }

    @Test
    public void deveVerificarSegundoNivel(){
        RestAssured.given()
                .when()
                .get("http://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
                .body("id", Matchers.is(2))
                .body("name", Matchers.containsString("Joaquina"))
                .body("endereco.rua",Matchers.is("Rua dos bobos"))
        ;
    }

    @Test
    public void deveVerificarLista(){
        RestAssured.given()
                .when()
                    .get("http://restapi.wcaquino.me/users/3")
                .then()
                    .statusCode(200)
                    .body("id", Matchers.is(3))
                    .body("name", Matchers.containsString("Ana"))
                    .body("filhos",Matchers.hasSize(2))
                    .body("filhos[0].name",Matchers.containsString("Zezinho"))
                    .body("filhos[1].name",Matchers.containsString("Luizinho"))
                    .body("filhos.name",Matchers.hasItem("Zezinho"))
                    .body("filhos.name",Matchers.hasItems("Zezinho","Luizinho"))
        ;
    }

    @Test
    public void deveRetornarErroUsuarioInexistente(){
        RestAssured.given()
                .when()
                    .get("http://restapi.wcaquino.me/users/4")
                .then()
                    .statusCode(404)
                    .body("error", Matchers.is("Usuário inexistente"))
        ;
    }

    @Test
    public void deveVerificarListaRaiz(){
        RestAssured.given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize(3))
                .body("name", Matchers.hasItems("João da Silva","Maria Joaquina", "Ana Júlia"))
                .body("age[1]", Matchers.is(25))
                .body("filhos.name",Matchers.hasItem(Arrays.asList("Zezinho","Luizinho")))
                .body("[2].filhos[1].name",Matchers.is("Luizinho"))
                .body("salary",Matchers.contains(1234.5677f,2500,null))
                ;
    }

    @Test
    public void devoFazerVerificacoesAvancadas(){
        RestAssured.given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", Matchers.hasSize(3))
                .body("age.findAll{it <= 25}.size()",Matchers.is(2))
                .body("age.findAll{it <= 25 && it > 20}.size()",Matchers.is(1))
                .body("findAll{it.age <= 25 && it.age > 20}.name",Matchers.hasItem("Maria Joaquina")) //retorna uma lista
                .body("findAll{it.age <= 25}[0].name",Matchers.is("Maria Joaquina"))
                .body("findAll{it.age <= 25}[1].name",Matchers.is("Ana Júlia"))
                .body("findAll{it.age <= 25}[-1].name",Matchers.is("Ana Júlia")) //começa do ultimo registro da lista
                .body("find{it.age <= 25}.name",Matchers.is("Maria Joaquina")) // retorna sempre o primeiro registro encontrado
                .body("findAll{it.name.contains('n')}.name", Matchers.hasItems("Maria Joaquina", "Ana Júlia"))
                .body("findAll{it.name.length() > 10}.name", Matchers.hasItems("João da Silva","Maria Joaquina"))
                .body("name.collect{it.toUpperCase()}",Matchers.hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}",Matchers.hasItem("MARIA JOAQUINA"))
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()",Matchers.allOf(
                        Matchers.arrayContaining("MARIA JOAQUINA"),Matchers.arrayWithSize(1)))
                .body("age.collect{it * 2}", Matchers.hasItems(60,50,40))
                .body("id.max()", Matchers.is(3))
                .body("salary.min()", Matchers.is(1234.5677F))
                .body("salary.findAll{it != null}.sum()", Matchers.is(Matchers.closeTo(3734.5677f,0.001)))
                .body("salary.findAll{it != null}.sum()", Matchers.allOf(Matchers.greaterThan(3000d),Matchers.lessThan(5000d)))
        ;
    }

    @Test
    public void devoUnirJsonPathComJava(){
        ArrayList<String> names =
        RestAssured.given()
                .when()
                .get("http://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}")
                ;

        Assert.assertEquals(1,names.size());
        Assert.assertTrue(names.get(0).equalsIgnoreCase("maria joaquina"));
        Assert.assertEquals(names.get(0).toUpperCase(),("maria joaquina").toUpperCase());
    }
}
