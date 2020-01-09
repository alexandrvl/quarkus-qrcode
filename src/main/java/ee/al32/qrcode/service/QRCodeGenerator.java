package ee.al32.qrcode.service;

import java.awt.image.BufferedImage;

public interface QRCodeGenerator {

    byte[] generateQRCode(String message, BufferedImage logoImage, Integer size);
}
