package ee.al32.qrcode;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class QRCodeResourceTest {

    @Test
    public void testGenerateQrCode() {
        given()
                .param("message", "some message")
                .param("imageUrl", "https://img.icons8.com/material/72/small-axe.png")
                .param("size", 19)
                .when().get("/qrcode")
                .then()
                .statusCode(200);
    }

}