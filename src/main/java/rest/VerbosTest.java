package rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VerbosTest {

    @Test
    public void deveSalvarUsuario(){
        RestAssured.given()
                    .log().all()
                    .contentType("application/json")
                    .body("{\"name\": \"Jose\",\"age\": 50}")
                .when()
                    .post("http://restapi.wcaquino.me/users")
                .then()
                    .log().all()
                    .statusCode(201)
                    .body("id", Matchers.is(Matchers.notNullValue()))
                    .body("name",Matchers.is("Jose"))
                    .body("age",Matchers.is(50))
                ;
    }

    @Test
    public void naoDeveSalvarUsuarioSemName(){
        RestAssured.given()
                    .log().all()
                    .contentType("application/json")
                    .body("{\"age\": 50}")
                .when()
                    .post("http://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(400)
                .body("id", Matchers.is(Matchers.nullValue()))
                .body("error", Matchers.is("Name é um atributo obrigatório"))

        ;
    }

    @Test
    public void deveSalvarUsuarioViaXml(){
        RestAssured.given()
                .log().all()
                .contentType(ContentType.XML)
                .body("<user><name>Jose</name><age>50</age></user>")
                .when()
                .post("http://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .body("user.id", Matchers.is(Matchers.notNullValue()))
                .body("user.name",Matchers.is("Jose"))
                .body("user.age",Matchers.is("50"))
        ;
    }

    @Test
    public void deveAlterarUsuario(){
        RestAssured.given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body("{\"name\": \"Usuario alterado\",\"age\": 50}")
                .when()
                    .put("http://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.is(1))
                    .body("name",Matchers.is("Usuario alterado"))
                    .body("age",Matchers.is(50))
                    .body("salary", Matchers.is(1234.5677F))
        ;
    }

    @Test
    public void devoCostumizarUrl(){
        RestAssured.given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body("{\"name\": \"Usuario alterado\",\"age\": 50}")
                .when()
                    .put("http://restapi.wcaquino.me/{entidade}/{userId}","users","1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.is(1))
                    .body("name",Matchers.is("Usuario alterado"))
                    .body("age",Matchers.is(50))
                    .body("salary", Matchers.is(1234.5677F))
        ;
    }

    @Test
    public void devoCostumizarUrlParteDois(){
        RestAssured.given()
                    .log().all()
                    .contentType(ContentType.JSON)
                    .body("{\"name\": \"Usuario alterado\",\"age\": 50}")
                    .pathParam("entidade","users")
                    .pathParam("userId",1)
                .when()
                    .put("http://restapi.wcaquino.me/{entidade}/{userId}")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("id", Matchers.is(1))
                    .body("name",Matchers.is("Usuario alterado"))
                    .body("age",Matchers.is(50))
                    .body("salary", Matchers.is(1234.5677F))
        ;
    }

    @Test
    public void deveRemoverUsuario(){
        RestAssured.given()
                    .log().all()
                .when()
                    .delete("http://restapi.wcaquino.me/users/1")
                .then()
                    .log().all()
                    .statusCode(204)
                ;
    }

    @Test
    public void naoDeveRemoverUsuarioInexistente(){
        RestAssured.given()
                    .log().all()
                .when()
                    .delete("http://restapi.wcaquino.me/users/100201")
                .then()
                    .log().all()
                    .statusCode(400)
                    .body("error", Matchers.is("Registro inexistente"))
        ;
    }

    @Test
    public void deveSalvarUsuarioUsandoMap(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name","Usuario via map");
        params.put("age",25);

        RestAssured.given()
                .log().all()
                .contentType("application/json")
                .body(params)
                .when()
                .post("http://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", Matchers.is(Matchers.notNullValue()))
                .body("name",Matchers.is("Usuario via map"))
                .body("age",Matchers.is(25))
        ;
    }

    @Test
    public void deveSalvarUsuarioUsandoObjeto(){
        User user = new User("Usuario via objeto", 35);

        RestAssured.given()
                .log().all()
                .contentType("application/json")
                .body(user)
                .when()
                .post("http://restapi.wcaquino.me/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", Matchers.is(Matchers.notNullValue()))
                .body("name",Matchers.is("Usuario via objeto"))
                .body("age",Matchers.is(35))
        ;
    }

    @Test
    public void deveDeserializarObjetoAoSalvarUsuario(){
        User user = new User("Usuario deserializado", 35);

        User usuarioInserido =
                    RestAssured.given()
                        .log().all()
                        .contentType("application/json")
                        .body(user)
                    .when()
                        .post("http://restapi.wcaquino.me/users")
                    .then()
                        .log().all()
                        .statusCode(201)
                        .extract().body().as(User.class);

        System.out.println(usuarioInserido);
        Assert.assertNotNull(usuarioInserido.getId());
        Assert.assertEquals(user.getName(),usuarioInserido.getName());
        Assert.assertEquals(user.getAge(),usuarioInserido.getAge());
    }

    @Test
    public void deveSalvarUsuarioViaXmlUsandoObjeto(){
        User user = new User("Usuario XML", 40);

        RestAssured.given()
                .log().all()
                .contentType(ContentType.XML)
                .body(user)
                .when()
                .post("http://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .body("user.id", Matchers.is(Matchers.notNullValue()))
                .body("user.name",Matchers.is("Usuario XML"))
                .body("user.age",Matchers.is("40"))
        ;
    }

    @Test
    public void deveDeserializarXMLAoSalvarUsuario(){
        User user = new User("Usuario XML", 40);

        User usuarioInserido = RestAssured.given()
                .log().all()
                .contentType(ContentType.XML)
                .body(user)
                .when()
                .post("http://restapi.wcaquino.me/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .extract().body().as(User.class)
        ;

        System.out.println(usuarioInserido);
        Assert.assertNotNull(usuarioInserido.getId());
        Assert.assertEquals("Usuario XML",usuarioInserido.getName());
        Assert.assertEquals(40,usuarioInserido.getAge().intValue());

    }
}



