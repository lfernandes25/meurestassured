package rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AuthTest {

    private String tokenWeather = ""; // pegar token no site http://openweathermap.org

    private static Map<String,String> login;

    //Por segurança foram removidos os usuários e senhas
    @BeforeClass
    public static void dadosLogin() {
        login = new HashMap<String,String>();
        login.put("email", "");
        login.put("senha", "");
    }

    @Test
    public void deveAcessarSWAPI(){
        given()
                    .log().all()
                .when()
                    .get("https://swapi.dev/api/people/1")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", is("Luke Skywalker"))
                ;
    }

    @Test
    public void deveObterClima(){
        given()
                    .log().all()
                    .queryParam("q","Fortaleza,BR")
                    .queryParam("appid",tokenWeather)
                    .queryParam("units","metric")
                .when()
                    .get("http://api.openweathermap.org/data/2.5/weather")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("name", is("Fortaleza"))
                    .body("coord.lon", is(-38.5247f))
                    .body("main.temp", greaterThan(25f))
                ;
    }

    @Test
    public void naoDeveAcessarSemSenha(){
        given()
                    .log().all()
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(401)
                ;
    }

    @Test
    public void deveFazerAutenticacaoBasica(){
        given()
                .log().all()
                .when()
                .get("https://admin:senha@restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaPassandoAuthSeparadoDaUrl(){
        given()
                    .log().all()
                    .auth().basic("admin","senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasicaChallenge(){
        given()
                    .log().all()
                    .auth().preemptive().basic("admin","senha")
                .when()
                    .get("https://restapi.wcaquino.me/basicauth2")
                .then()
                    .log().all()
                    .statusCode(200)
                    .body("status", is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoComTokenJWT(){

        String token = given()
                    .log().all()
                    .body(login)
                    .contentType(ContentType.JSON)
                .when()
                    .post("http://barrigarest.wcaquino.me/signin")
                .then()
                    .log().all()
                    .statusCode(200)
                    .extract().path("token")
                ;

                given()
                    .log().all()
                        .header("Authorization", "JWT " + token)
                .when()
                    .get("http://barrigarest.wcaquino.me/contas")
                .then()
                    .log().all()
                        .body("nome",hasItem("Conta de teste"))
                ;
    }

    @Test
    public void deveAcessarAplicacaoWeb(){

        String cookie = given()
                .log().all()
                    .formParam("email", login.get("email"))
                    .formParam("senha",login.get("senha"))
                    .contentType(ContentType.URLENC.withCharset("UTF-8"))
                .when()
                    .post("http://seubarriga.wcaquino.me/logar")
                .then()
                    .log().all()
                    .statusCode(200)
                    .extract().header("set-cookie")
        ;

        cookie = cookie.split("=")[1].split(";")[0];
        System.out.println(cookie);

        String body = given()
                        .log().all()
                        .cookie("connect.sid", cookie)
                .when()
                        .get("http://seubarriga.wcaquino.me/contas")
                .then().log().all().statusCode(200)
                        .body("html.body.table.tbody.tr[0].td[0]", is("Conta de teste"))
                        .extract().body().asString();

        System.out.println("\n**************************************************\n");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML,body);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
    }
}
