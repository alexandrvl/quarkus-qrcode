package ee.al32.qrcode.service;

import java.awt.image.BufferedImage;

public interface LogoProvider<T> {

    BufferedImage getLogo(T imagePath);
}
