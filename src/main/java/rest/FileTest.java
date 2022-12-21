package rest;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class FileTest {

    @Test
    public void devoObrigarEnviarArquivo(){
        RestAssured.given()
                    .log().all()
                .when()
                    .post("http://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                    .statusCode(404)
                    .body("error", Matchers.is("Arquivo não enviado"))
                ;
    }

    @Test
    public void devoFazerUploadArquivo(){
        RestAssured.given()
                    .log().all()
                    .multiPart("arquivo", new File("src\\main\\resources\\users.pdf"))
                .when()
                    .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .statusCode(200)

        ;
    }

    @Test
    public void naoDeveFazerUploadArquivoGrande(){
        RestAssured.given()
                .log().all()
                .multiPart("arquivo", new File("src\\main\\resources\\Capas_001_100.pdf"))
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                    .log().all()
                .time(Matchers.lessThan(5000l))
                    .statusCode(413)

        ;
    }

    @Test
    public void deveBaixarArquivo() throws IOException {
        byte[] image =
        RestAssured.given()
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/download")
                .then()
                .statusCode(200)
                .extract().asByteArray()
        ;
        File imagem = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(imagem);
        out.write(image);
        out.close();

        System.out.println(imagem.length());
        Assert.assertThat(imagem.length(), Matchers.lessThan(100000l));
    }
}
