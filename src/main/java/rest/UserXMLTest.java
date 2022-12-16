package rest;

import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserXMLTest {

    @Test
    public void devoTrabalharComXML(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/usersXML/3")
                .then()
                .statusCode(200)
                .body("user.name", is("Ana Julia"))
                .body("user.@id", is("3"))
                .body("user.filhos.name.size()", is(2))
                .body("user.filhos.name[0]",is("Zezinho"))
                .body("user.filhos.name[1]",is("Luizinho"))
                .body("user.filhos.name",hasItem("Luizinho"))
                .body("user.filhos.name",hasItems("Zezinho","Luizinho"))
        ;
    }

    @Test
    public void devoTrabalharComXMLDois(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/usersXML/3")
                .then()
                .statusCode(200)
                .rootPath("user")
                .body("name", is("Ana Julia"))
                .body("@id", is("3"))
                .body("filhos.name.size()", is(2))
                .body("filhos.name[0]",is("Zezinho"))
                .body("filhos.name[1]",is("Luizinho"))
                .body("filhos.name",hasItem("Luizinho"))
                .body("filhos.name",hasItems("Zezinho","Luizinho"))
        ;
    }

    @Test
    public void devoTrabalharComXMLTres(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/usersXML/3")
                .then()
                .statusCode(200)
                .rootPath("user")
                .body("name", is("Ana Julia"))
                .body("@id", is("3"))

                .rootPath("user.filhos")
                .body("name.size()", is(2))
                .body("name[0]",is("Zezinho"))
                .body("name[1]",is("Luizinho"))
                .body("name",hasItem("Luizinho"))
                .body("name",hasItems("Zezinho","Luizinho"))
        ;
    }

    @Test
    public void devoTrabalharComXMLQuatro(){
        given()
                .when()
                .get("http://restapi.wcaquino.me/usersXML/3")
                .then()
                .statusCode(200)
                .rootPath("user")
                .body("name", is("Ana Julia"))
                .body("@id", is("3"))

                .appendRootPath("filhos")
                .body("name.size()", is(2))
                .body("name[0]",is("Zezinho"))
                .body("name[1]",is("Luizinho"))
                .body("name",hasItem("Luizinho"))
                .body("name",hasItems("Zezinho","Luizinho"))

                .detachRootPath("filhos")
                .body("age",is("20"))
        ;
    }
}
