package ee.al32.qrcode.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZXingQrCodeGeneratorServiceTest {
    private ZXingQrCodeGeneratorService zxingQrCodeGeneratorService;

    @BeforeEach
    public void setUp() {
        zxingQrCodeGeneratorService = new ZXingQrCodeGeneratorService();
    }

    @Test
    public void testQrCodeIsGenerated() throws IOException {
        String message = "some random message";
        Integer size = 200;
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("corgi.png");
        assertNotNull(inputStream);
        BufferedImage logoImage = ImageIO.read(inputStream);
        byte[] response = zxingQrCodeGeneratorService.generateQRCode(message, logoImage, size);
        assertTrue(response.length > 0);
    }

}