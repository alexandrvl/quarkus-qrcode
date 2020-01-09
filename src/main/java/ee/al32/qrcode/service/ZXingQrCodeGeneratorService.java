package ee.al32.qrcode.service;


import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ZXingQrCodeGeneratorService implements QRCodeGenerator {
    private static final String IMAGE_FORMAT = "png";
    public static final Color MAIN_COLOR = new Color(51, 150, 153);
    public static final int FONT_SIZE = 30;

    @Override
    public byte[] generateQRCode(String message, BufferedImage logoImage, Integer size) {
        BitMatrix bitMatrix = buildBitMatrix(message, size);
        BufferedImage bufferedImage = new BufferedImage(bitMatrix.getWidth(), bitMatrix.getWidth(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = getGraphics(bitMatrix, bufferedImage.createGraphics(), bufferedImage.getWidth());
        fillRect(bitMatrix, graphics);
        double rate = calcRate(bufferedImage, logoImage);
        logoImage = getScaledImage(logoImage, (int) (logoImage.getWidth() * rate), (int) (logoImage.getHeight() * rate));
        graphics.drawImage(logoImage, bufferedImage.getWidth() / 2 - logoImage.getWidth() / 2,
                bufferedImage.getHeight() / 2 - logoImage.getHeight() / 2,
                bufferedImage.getWidth() / 2 + logoImage.getWidth() / 2,
                bufferedImage.getHeight() / 2 + logoImage.getHeight() / 2,
                0, 0, logoImage.getWidth(), logoImage.getHeight(), null);
        byte[] byteArrayOutputStream = getBytesFromImage(message, bufferedImage);
        if (byteArrayOutputStream != null) return byteArrayOutputStream;
        throw new RuntimeException("cannot generate qrcode");
    }

    private byte[] getBytesFromImage(String message, BufferedImage bufferedImage) {
        if (isCorrect(message, bufferedImage)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage, IMAGE_FORMAT, byteArrayOutputStream);
            } catch (IOException e) {
                log.error("error getting bytes for image", e);
            }
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    private boolean isCorrect(String message, BufferedImage image) {
        Result qrResult = decode(image);
        return qrResult != null && message != null && (message.equals(qrResult.getText()));
    }

    private Result decode(BufferedImage image) {
        if (image == null) {
            return null;
        }
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        try {
            return reader.decode(bitmap, Collections.emptyMap());
        } catch (FormatException | NotFoundException | ChecksumException e) {
            log.error("cannot decode", e);
            throw new RuntimeException(e);
        }
    }


    private BufferedImage getScaledImage(BufferedImage image, Integer width, Integer height) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double scaleX = width.doubleValue() / imageWidth;
        double scaleY = height.doubleValue() / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);
        return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
    }

    private double calcRate(BufferedImage image, BufferedImage logo) {
        if ((logo.getWidth() / image.getHeight()) > 0.3) {
            return 0.3;
        }
        return 1;
    }

    private Graphics2D getGraphics(BitMatrix bitMatrix, Graphics2D graphics, int bufferedImageWidth) {
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, FONT_SIZE);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, bufferedImageWidth, bufferedImageWidth);
        graphics.setColor(MAIN_COLOR);
        return graphics;
    }

    private void fillRect(BitMatrix bitMatrix, Graphics2D graphics) {
        int i = 0;
        while (i < bitMatrix.getWidth()) {
            int j = 0;
            while (j < bitMatrix.getWidth()) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
                j++;
            }
            i++;
        }
    }

    private BitMatrix buildBitMatrix(String message, Integer size) {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            return qrCodeWriter.encode(message, BarcodeFormat.QR_CODE, size, size, hintMap);
        } catch (WriterException e) {
            log.error("cannot create bit matrix", e);
            throw new RuntimeException(e);
        }
    }
}
