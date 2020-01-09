package ee.al32.qrcode.service;

import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class HttpLogoProviderService implements LogoProvider<URL> {

    @Override
    public BufferedImage getLogo(URL imagePath) {
        try {
            return ImageIO.read(imagePath);
        } catch (IOException e) {
            log.error("cannot read image from url", e);
            throw new RuntimeException(e);
        }
    }

    public static URL buildLogoUrl(String imagePath) {
        try {
            return new URL(imagePath);
        } catch (MalformedURLException e) {
            log.error("cannot read image from url", e);
            throw new RuntimeException(e);
        }
    }
}
